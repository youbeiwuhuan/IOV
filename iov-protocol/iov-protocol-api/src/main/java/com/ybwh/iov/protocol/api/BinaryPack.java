package com.ybwh.iov.protocol.api;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * 二进制数据包。由第一步拆包分包解出来的二进制数据
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 11:32
 * @Modified By:
 */
public interface BinaryPack {
    /**
     * 获取二进制数据
     *
     * @return
     */
    byte[] getBinary();

    /**
     * 用ByteBuf对象返回二进制数据
     *
     * @return
     */
    ByteBuf getBinaryByteBuf();

    /**
     * 返回二进制数据的base64编码
     *
     * @return
     */
    String getBinaryBase64();

    /**
     * 初始化二进制数据，只能初始化一次
     *
     * @param protocolName 协议名
     * @param bytes
     *
     */
    void init(String protocolName,byte[] bytes);

    /**
     * 初始化二进制数据，只能初始化一次
     * @param protocolName 协议名
     * @param byteBuf
     */
    void init(String protocolName,ByteBuf byteBuf);

    /**
     * 初始化二进制数据，只能初始化一次
     * @param protocolName 协议名
     * @param base64
     */
    void init(String protocolName,String  base64) throws IOException;

    /**
     * 获取协议名称
     *
     * @return
     */
    String getProtocolName();


}
