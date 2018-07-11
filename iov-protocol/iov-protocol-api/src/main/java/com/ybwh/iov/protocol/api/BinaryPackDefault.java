package com.ybwh.iov.protocol.api;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 * BinaryPack默认实现
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 12:06
 * @Modified By:
 */
@SuppressWarnings("restriction")
public class BinaryPackDefault implements BinaryPack {
   
	private static final BASE64Encoder encoder = new BASE64Encoder();
    private static final BASE64Decoder decoder = new BASE64Decoder();

    private String protocolName;
    private ByteBuf binary;
    private transient boolean isInit = false;


    @Override
    public byte[] getBinary() {
        return binary.array();
    }

    @Override
    public ByteBuf getBinaryByteBuf() {
        return binary;
    }

	@Override
    public String getBinaryBase64() {
        return encoder.encode(getBinary());
    }

    @Override
    public void init(String protocolName, byte[] bytes) {
        if (null == protocolName || "".equals(protocolName.trim()) || null == bytes) {
            throw new IllegalArgumentException();
        }

        if (isInit) {
            throw new IllegalStateException(" has init before!!");
        } else {
            isInit = true;

            this.protocolName = protocolName;
            binary = Unpooled.copiedBuffer(bytes);


        }
    }

    @Override
    public void init(String protocolName, ByteBuf byteBuf) {
        if (null == protocolName || "".equals(protocolName.trim()) || null == byteBuf) {
            throw new IllegalArgumentException();
        }

        if (isInit) {
            throw new IllegalStateException(" has init before!!");
        } else {
            isInit = true;

            this.protocolName = protocolName;
            binary = byteBuf;
        }
    }

    @Override
    public void init(String protocolName, String base64) throws IOException {
        if (null == protocolName || "".equals(protocolName.trim()) || null == base64 || "".equals(base64.trim())) {
            throw new IllegalArgumentException();
        }


        if (isInit) {
            throw new IllegalStateException(" has init before!!");
        } else {
            isInit = true;

            this.protocolName = protocolName;
            binary = Unpooled.copiedBuffer(decoder.decodeBuffer(base64));
        }
    }

    @Override
    public String getProtocolName() {
        return protocolName;
    }
}
