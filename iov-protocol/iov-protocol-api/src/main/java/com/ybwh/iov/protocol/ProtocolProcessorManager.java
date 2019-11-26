package com.ybwh.iov.protocol;

import com.ybwh.iov.common.extension.ExtensionUtils;
import com.ybwh.iov.protocol.api.ProtocolProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 协议解析器的管理类
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 9:51
 * @Modified By:
 */
public class ProtocolProcessorManager {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolProcessorManager.class);
    /**
     * 协议解析器的配置路径
     */
    private static final String PROTOCOL_PROCESSOR_DIRECTORY = "META-INF/protocol/";

    private static volatile ProtocolProcessorManager processorManager = null;


    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 初始化后只读所以不需要用ConcurrentHashMap
     */
    private HashMap<String/*协议名称*/, Class<?>> processorClasseTable = new HashMap<String, Class<?>>();
    private HashMap<String/*协议名称*/, ProtocolProcessor> processorTable = new HashMap<String, ProtocolProcessor>();

    private ProtocolProcessorManager() {
        init();
    }

    // synchronized in getInstance
    private void init() {

        ExtensionUtils.loadFile(processorClasseTable, PROTOCOL_PROCESSOR_DIRECTORY, ProtocolProcessor.class);
        if (processorClasseTable.size() > 0) {
            for (Map.Entry<String, Class<?>> entry : processorClasseTable.entrySet()) {
                try {
                    processorTable.put(entry.getKey(), (ProtocolProcessor) entry.getValue().newInstance());
                } catch (Exception e) {
                    logger.error("create ProtocolProcessor error! ", e);
                }
            }
        }

    }


    public static ProtocolProcessorManager getInstance() {
        if (null == processorManager) {
            synchronized (ProtocolProcessorManager.class) {
                if (null == processorManager) {
                    processorManager = new ProtocolProcessorManager();
                }
            }
        }

        return processorManager;
    }


    /**
     * 获取通讯协议的解析器
     *
     * @return
     */
    public ProtocolProcessor acquireProtocolProcessor(String protocolName) {
        if (StringUtils.isBlank(protocolName)) {
            throw new IllegalArgumentException();
        }

        return processorTable.get(protocolName);
    }


    /**
     * 获取所有的通讯协议的解析器
     *
     * @return
     */
    public Collection<ProtocolProcessor> allProtocolProcessor() {
        return processorTable.values();
    }


}
