package com.ybwh.iov.gateway.up.handler;

import java.util.List;

import com.ybwh.iov.gateway.IOVGateWayConstant;
import com.ybwh.iov.protocol.api.BinaryPack;
import com.ybwh.iov.protocol.api.PlainPackData;
import com.ybwh.iov.protocol.api.ProtocolProcessor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * 协议解码器
 *
 */
public class ProtocolDecoder  extends ByteToMessageDecoder  {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//1.获取关联的协议解析器
		AttributeKey<ProtocolProcessor> KEY_PROTOCOL_PROCESSOR = AttributeKey.valueOf(IOVGateWayConstant.CHANNEL_PROTOCOL_PROCESSOR);
        Channel channel = ctx.channel();
        Attribute<ProtocolProcessor> protocolProcessorAttr = channel.attr(KEY_PROTOCOL_PROCESSOR);
        ProtocolProcessor protocolProcessor = protocolProcessorAttr.get();
        if (null == protocolProcessor) {
            throw new IllegalStateException("can not get protocol processor!!");
        }
        
        //2. 解析
        List<BinaryPack>  bPackList = protocolProcessor.subPack(in);
        if(null != bPackList) {
        	for(BinaryPack bp: bPackList) {
        		List<PlainPackData> packDataList = protocolProcessor.parseBinaryPack(bp);
        		out.add(packDataList);
        	}
        }
	}

}
