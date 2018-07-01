# IOV  车联网项目架构
Internet of Vehicles  project   framework



## 设备连接网关模块
	提供车载主设备连接通讯的模块
	
## 车辆注册中心
	实时反馈车辆车载主设备与哪台主机连接,功能类似rocketmq的nameserver,也将借鉴用rocketmq的nameserver的原理来实现



## 数据解析分发模块
	解析数据、将数据分发到各个需要的系统或存入数据存储系统，如将数据发送到国家平台等。
	
	



## 数据转存模块
	将数据从一个存储系统转存到另一个系统，如从hbase复制到mysql


## web后台模块
	



