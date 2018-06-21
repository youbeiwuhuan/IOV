package com.ybwh.iov.gateway.up;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.ybwh.iov.gateway.util.RemotingUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
/**
 * (这里用一句话描述这个类的作用)
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 12:51
 * @Modified By:
 */
public class IovGatewayHubServer {
    private ServerBootstrap serverBootstrap;
    /**
     * 处理连接的线程池
     */
    private  EventLoopGroup eventLoopGroupSelector;
    /**
     * 处理分发的线程池
     */
    private EventLoopGroup eventLoopGroupBoss;

    /**
     *  专门单独处理业务的线程池
     */
    private DefaultEventExecutorGroup defaultEventExecutorGroup;

	public IovGatewayHubServer(ServerBootstrap serverBootstrap, EventLoopGroup eventLoopGroupSelector,
			EventLoopGroup eventLoopGroupBoss, DefaultEventExecutorGroup defaultEventExecutorGroup) {
		this.serverBootstrap = serverBootstrap;
		this.eventLoopGroupSelector = eventLoopGroupSelector;
		this.eventLoopGroupBoss = eventLoopGroupBoss;
		this.defaultEventExecutorGroup = defaultEventExecutorGroup;
		
		
		this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyBoss_%d", this.threadIndex.incrementAndGet()));
			}
		});

		if (RemotingUtil.isLinuxPlatform() ) {
			this.eventLoopGroupSelector = new EpollEventLoopGroup(nettyServerConfig.getServerSelectorThreads(),
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




}
