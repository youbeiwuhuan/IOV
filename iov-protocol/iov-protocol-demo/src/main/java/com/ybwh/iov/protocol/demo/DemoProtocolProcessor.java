package com.ybwh.iov.protocol.demo;

import com.ybwh.iov.protocol.api.BinaryPack;
import com.ybwh.iov.protocol.api.PlainPackData;
import com.ybwh.iov.protocol.api.ProtocolProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ReadOnlyByteBuf;

import java.util.List;
import java.util.Set;

/**
 * (这里用一句话描述这个类的作用)
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 10:41
 * @Modified By:
 */
public class DemoProtocolProcessor implements ProtocolProcessor {

    @Override
    public boolean support(String protocolName) {
        return false;
    }

    @Override
    public Set<String> supportProtocols() {
        return null;
    }

    @Override
    public String verifyProtocol(ReadOnlyByteBuf dataStream) {
        return null;
    }

    @Override
    public List<BinaryPack> subPack(ByteBuf dataStream) {
        return null;
    }

    @Override
    public List<PlainPackData> parseBinaryPack(BinaryPack binaryPack) {
        return null;
    }
}
