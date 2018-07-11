package com.ybwh.iov.registry.server;

/**
 * 注册中心服务端
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 12:40
 * @Modified By:
 */
public interface RegistryServer {
	
	/**
	 * 启动
	 */
	void start();
	
	/**
	 * 停止
	 */
	void shutdown();
	
}
