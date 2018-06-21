package com.ybwh.iov.common.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

/**
 * (这里用一句话描述这个类的作用)
 *
 * @author: Fan Beibei
 * @date: 2018/6/21 11:04
 * @Modified By:
 */
public class ExtensionUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExtensionUtils.class);

    private ExtensionUtils() {

    }

    /**
     * 获取类加载器
     *
     * @return
     */
    private static ClassLoader findClassLoader() {

        ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
        if (null != threadContextClassLoader) {
            return threadContextClassLoader;
        }

        return ExtensionUtils.class.getClassLoader();
    }


    /**
     * 加载扩展类
     *
     * @param extensionClasses
     * @param dir
     * @param type
     */
    public static void loadFile(final Map<String, Class<?>> extensionClasses, final String dir, final Class<?> type) {

        String fileName = dir + type.getName();
        try {
            Enumeration<URL> urls;


            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }


            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                        try {
                            String line = null;
                            while ((line = reader.readLine()) != null) {

                                //处理注释
                                final int ci = line.indexOf('#');
                                if (ci >= 0)
                                    line = line.substring(0, ci);
                                line = line.trim();


                                if (line.length() > 0) {
                                    try {
                                        String name = null;//扩展名称
                                        int i = line.indexOf('=');
                                        if (i > 0) {
                                            name = line.substring(0, i).trim();
                                            line = line.substring(i + 1).trim();//获取扩展类名称
                                        }
                                        if (line.length() > 0) {
                                            Class<?> clazz = Class.forName(line, true, classLoader);
                                            if (!type.isAssignableFrom(clazz)) {
                                                throw new IllegalStateException("Error when load extension class(interface: " +
                                                        type + ", class line: " + clazz.getName() + "), class "
                                                        + clazz.getName() + "is not subtype of interface.");
                                            }


                                            extensionClasses.put(name, clazz);

                                        }
                                    } catch (Throwable t) {
                                        IllegalStateException e = new IllegalStateException("Failed to load extension class(interface: " + type + ", class line: " + line + ") in " + url + ", cause: " + t.getMessage(), t);
                                        logger.error("", e);
                                    }
                                }
                            } // end of while read lines
                        } finally {
                            reader.close();
                        }
                    } catch (Throwable t) {
                        logger.error("Exception when load extension class(interface: " +
                                type + ", class file: " + url + ") in " + url, t);
                    }
                } // end of while urls
            }
        } catch (Throwable t) {
            logger.error("Exception when load extension class(interface: " +
                    type + ", description file: " + fileName + ").", t);
        }
    }

}
