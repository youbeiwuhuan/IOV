package com.ybwh.iov.gateway.up;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ybwh.iov.gateway.netty.NettyServerConfig;
import com.ybwh.iov.gateway.up.handler.IovConnetManageHandler;
import com.ybwh.iov.gateway.up.handler.IovMessageHandler;
import com.ybwh.iov.gateway.up.handler.ProtocolDecoder;
import com.ybwh.iov.gateway.up.handler.VerifyProtocolHandler;
import com.ybwh.iov.gateway.util.RemotingUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
/**
 * 网关服务器
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 12:51
 * @Modified By:
 */
public class   IovGatewayHubServer {
	private static Logger logger = LoggerFactory.getLogger(IovGatewayHubServer.class);
	
	
    private ServerBootstrap serverBootstrap;
    /**
     * 处理连接的线程池
     */
    private  EventLoopGroup eventLoopGroupSelector;
    /**
     * 处理IO分发的线程池
     */
    private EventLoopGroup eventLoopGroupBoss;

    /**
     *  专门单独处理业务逻辑的线程池
     */
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    
	/**
	 * netty的配置
	 */
	private NettyServerConfig nettyServerConfig;


	private int port;

	public IovGatewayHubServer(ServerBootstrap serverBootstrap, EventLoopGroup eventLoopGroupSelector,
			EventLoopGroup eventLoopGroupBoss, DefaultEventExecutorGroup defaultEventExecutorGroup,final NettyServerConfig nettyServerConfig) {
		this.serverBootstrap = serverBootstrap;
		this.eventLoopGroupSelector = eventLoopGroupSelector;
		this.eventLoopGroupBoss = eventLoopGroupBoss;
		this.defaultEventExecutorGroup = defaultEventExecutorGroup;
		this.nettyServerConfig = nettyServerConfig;
		
		//
		this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyBoss_%d", this.threadIndex.incrementAndGet()));
			}
		});

		if (RemotingUtil.isLinuxPlatform() ) {//Linux平台使用系统调用epoll提高效率
			this.eventLoopGroupSelector = this.eventLoopGroupSelector = new EpollEventLoopGroup(nettyServerConfig.getServerSelectorThreads(),
					new ThreadFactory() {
				private AtomicInteger threadIndex = new AtomicInteger(0);
				private int threadTotal = nettyServerConfig.getServerSelectorThreads();

				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, String.format("NettyServerEPOLLSelector_%d_%d", threadTotal,
							this.threadIndex.incrementAndGet()));
				}
			});
		} else {
			this.eventLoopGroupSelector = new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(),
					new ThreadFactory() {
						private AtomicInteger threadIndex = new AtomicInteger(0);
						private int threadTotal = nettyServerConfig.getServerSelectorThreads();

						@Override
						public Thread newThread(Runnable r) {
							return new Thread(r, String.format("NettyServerNIOSelector_%d_%d", threadTotal,
									this.threadIndex.incrementAndGet()));
						}
					});
		}
	}


	public void start() {
		this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyServerConfig.getServerWorkerThreads(),
				new ThreadFactory() {

					private AtomicInteger threadIndex = new AtomicInteger(0);

					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
					}
				});

		ServerBootstrap childHandler = this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
				.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, false)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
				.option(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
				.localAddress(new InetSocketAddress(this.nettyServerConfig.getListenPort()))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(
								/**
								 * 这里指定线程池，下面handler均会用此处指定线程池,不会用selector线程池;
								 * 好处就是不会导致业务处理太耗时占用大量了线程而使得IO缺少线程被阻断.
								 */
								defaultEventExecutorGroup, 
								new VerifyProtocolHandler(), //识别通讯协议
								new ProtocolDecoder(), // 解码
								new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()), // 心跳
								new IovConnetManageHandler(), // 连接管理
								new IovMessageHandler());// 这里才是真正的业务处理
					}
				});

		if (nettyServerConfig.isServerPooledByteBufAllocatorEnable()) {
			childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		}

		try {
			ChannelFuture sync = this.serverBootstrap.bind().sync();
			InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
			this.port = addr.getPort();
		} catch (InterruptedException e1) {
			throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
		}

	}

	public void shutdown() {
		try {
		

			this.eventLoopGroupBoss.shutdownGracefully();

			this.eventLoopGroupSelector.shutdownGracefully();


			if (this.defaultEventExecutorGroup != null) {
				this.defaultEventExecutorGroup.shutdownGracefully();
			}
		} catch (Exception e) {
			logger.error("NettyRemotingServer shutdown exception, ", e);
		}

		
	}

}
