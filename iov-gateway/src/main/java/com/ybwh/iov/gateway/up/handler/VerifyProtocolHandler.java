package com.ybwh.iov.gateway.up.handler;

import com.ybwh.iov.gateway.IOVGateWayConstant;
import com.ybwh.iov.protocol.ProtocolProcessorManager;
import com.ybwh.iov.protocol.api.ProtocolProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ReadOnlyByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * 只做识别协议的动作
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 12:59
 * @Modified By:
 */
public class VerifyProtocolHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(VerifyProtocolHandler.class);

    /**
     * 累积缓存区
     */
    private ByteBuf cumulation;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        cumulation = ctx.alloc().buffer();
        logger.info("Device({}) connected!", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            throw new IllegalStateException("VerifyProtocolHandler must be first!!");
        }

        ByteBuf byteBuf = (ByteBuf) msg;
        if (byteBuf.readableBytes() > 2 * 1024 * 1024) { //大于2M 直接丢弃
            byteBuf.release();
            return;
        }

        AttributeKey<ProtocolProcessor> KEY_PROTOCOL_PROCESSOR = AttributeKey.valueOf(IOVGateWayConstant.CHANNEL_PROTOCOL_PROCESSOR);
        Channel channel = ctx.channel();
        Attribute<ProtocolProcessor> protocolProcessorAttr = channel.attr(KEY_PROTOCOL_PROCESSOR);
        if (null != protocolProcessorAttr.get()) {//已经识别过协议
            ctx.fireChannelRead(msg);
            return;
        }

        /**
         * 没能识别出协议说明还没读到一个完整包，还需累积字节
         */
        cumulation.writeBytes(byteBuf);
        byteBuf.release();


        /**
         * 依次用现有的解析器去识别协议知道识别为止，识别后就将累积的数据全部交给解码器去处理
         */
        ProtocolProcessorManager processorManager = ProtocolProcessorManager.getInstance();
        Collection<ProtocolProcessor> processors = processorManager.allProtocolProcessor();

        if (null != processors && processors.size() > 0) {
            for (ProtocolProcessor processor : processors) {
                String protocolName = processor.verifyProtocol(new ReadOnlyByteBuf(cumulation));
                if (StringUtils.isNotBlank(protocolName)) {//识别出协议
                    protocolProcessorAttr.setIfAbsent(processor);
                    ctx.fireChannelRead(cumulation);//将累积区的数据传给解码器

                    return;
                }
            }
        }

    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Device({}) disconnected!", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
}
