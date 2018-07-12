package com.ybwh.iov.gateway.up.handler;

import java.util.List;

import com.ybwh.iov.protocol.api.PlainPackData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 对解析出来的包数据进行业务处理
 *
 */
public class IovMessageHandler extends SimpleChannelInboundHandler<List<PlainPackData>>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, List<PlainPackData> msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
