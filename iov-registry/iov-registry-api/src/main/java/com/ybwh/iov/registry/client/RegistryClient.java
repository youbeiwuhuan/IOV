package com.ybwh.iov.registry.client;

import com.ybwh.iov.registry.common.VehicleRemoteInfo;

/**
 * 注册中心的客户端
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 12:40
 * @Modified By:
 */
public interface RegistryClient {

	/**
	 * 注册车辆信息
	 * 
	 * @param id
	 *            车辆唯一标志符
	 * @param remoteInfo
	 *            车辆远程连接信息
	 */
	void registerVehicle(String id, VehicleRemoteInfo remoteInfo);

	/**
	 * 注册车辆信息
	 * 
	 * @param id
	 *            车辆唯一标志符
	 */
	VehicleRemoteInfo getVehicleRemoteInfo(String id);

}
