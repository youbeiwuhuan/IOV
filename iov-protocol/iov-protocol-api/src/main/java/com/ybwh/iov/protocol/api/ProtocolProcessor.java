package com.ybwh.iov.protocol.api;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ReadOnlyByteBuf;

import java.util.List;
import java.util.Set;

/**
 * 车联网通讯协议解析器
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 9:36
 * @Modified By:
 */
public interface ProtocolProcessor {
    /**
     * 是否支持某种通讯协议
     *
     * @param protocolName 通讯协议名称
     * @return
     */
    boolean support(String protocolName);

    /**
     * 支持的所有通讯协议
     *
     * @return
     */
    Set<String> supportProtocols();


    /**
     * 从二进制数据流中识别协议返回通讯协议名称，如果不能识别出协议则返回空。
     * 各个解析器只能识别出supportProtocols返回的协议。不可丢弃任何数据
     *
     * @param dataStream 只读的二进制数据流
     * @return
     */
    String verifyProtocol(ReadOnlyByteBuf dataStream);


    /**
     * 分包,可以丢弃无用数据
     *
     * @param dataStream
     * @return
     */
    List<BinaryPack> subPack(ByteBuf dataStream);


    /**
     * 将BinaryPack 解析成 PlainPackData
     *
     * @param binaryPack
     * @return
     */
    List<PlainPackData> parseBinaryPack(BinaryPack binaryPack);


    /**
     * 获取车辆唯一标志，有vin码返回vin-号码，有设备ID返回deviceId-号码
     *
     * @return
     */
    String getVehicleIdCode(BinaryPack binaryPack);
}
