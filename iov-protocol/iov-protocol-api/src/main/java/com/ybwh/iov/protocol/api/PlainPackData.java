package com.ybwh.iov.protocol.api;

/**
 * 由二进制数据包解析出来的具体数据
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 11:34
 * @Modified By:
 */
public interface PlainPackData {
    /**
     * 获取协议名称
     *
     * @return
     */
    String getProtocolName();



    /**
     * 获取车辆唯一标志，有vin码返回vin-号码，有设备ID返回deviceId-号码
     *
     * @return
     */
    String getVehicleIdCode();


}
