package org.leo.web.rest.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求映射注册类
 * 
 * @author Leo
 * @date 2018/3/16
 */
public final class ControllerMappingRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ControllerMappingRegistry.class);

    private static final Map<String, ControllerMapping> getMappings = new HashMap<>(64);

    private static final Map<String, ControllerMapping> postMappings = new HashMap<>(64);

    private static final Map<String, ControllerMapping> putMappings = new HashMap<>(64);

    private static final Map<String, ControllerMapping> deleteMappings = new HashMap<>(64);

    private static final Map<String, ControllerMapping> patchMappings = new HashMap<>(64);

    /**
     * 缓存 REST 控制器类
     */
    private static final Map<String, ControllerBean> beans = new HashMap<>(128);

    /**
     * 缓存 REST 控制器类单例
     */
    private static final Map<String, Object> singletons = new ConcurrentHashMap<>(128);

    /**
     * 注册Controller Bean
     * 
     * @param name
     * @param clazz
     */
    public static void registerBean(String name, ControllerBean bean) {
        beans.put(name, bean);
    }

    /**
     * 得到Controller Bean
     * 
     * @param name
     * @return
     */
    public static ControllerBean getBean(String name) {
        return beans.get(name);
    }

    /**
     * 注册Controller类的单例
     * 
     * @param name
     * @param singleton
     */
    public static void registerSingleton(String name, Object singleton) {
        singletons.put(name, singleton);
    }

    /**
     * 得到单例
     * 
     * @param name
     * @return
     */
    public static Object getSingleton(String name) {
        if (singletons.containsKey(name)) {
            return singletons.get(name);
        }

        Class<?> clazz = null;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            logger.error("Class not found: {}", name);
            return null;
        }
        Object instance = null;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Create class instance failure: {}", name);
            return null;
        }
        Object result = singletons.putIfAbsent(name, instance);
        if (result == null) {
            return instance;
        }
        return result;
    }

    /**
     * 注册 Get Mapping
     * 
     * @param url
     * @param mapping
     */
    public static void registerGetMapping(String url, ControllerMapping mapping) {
        getMappings.put(url, mapping);
    }

    /**
     * 得到Get映射哈希表
     * 
     * @return
     */
    public static Map<String, ControllerMapping> getGetMappings() {
        return getMappings;
    }

    /**
     * 注册 Post Mapping
     * 
     * @param url
     * @param mapping
     */
    public static void registerPostMapping(String url, ControllerMapping mapping) {
        postMappings.put(url, mapping);
    }

    /**
     * 得到Post映射哈希表
     * 
     * @return
     */
    public static Map<String, ControllerMapping> getPostMappings() {
        return postMappings;
    }

    /**
     * 注册 Put Mapping
     * 
     * @param url
     * @param mapping
     */
    public static void registerPutMapping(String url, ControllerMapping mapping) {
        putMappings.put(url, mapping);
    }

    /**
     * 得到Put映射哈希表
     * 
     * @return
     */
    public static Map<String, ControllerMapping> getPutMappings() {
        return putMappings;
    }

    /**
     * 注册 Delete Mapping
     * 
     * @param url
     * @param mapping
     */
    public static void registerDeleteMapping(String url, ControllerMapping mapping) {
        deleteMappings.put(url, mapping);
    }

    /**
     * 得到Delete映射哈希表
     * 
     * @return
     */
    public static Map<String, ControllerMapping> getDeleteMappings() {
        return deleteMappings;
    }

    /**
     * 注册 Patch Mapping
     * 
     * @param url
     * @param mapping
     */
    public static void registerPatchMapping(String url, ControllerMapping mapping) {
        patchMappings.put(url, mapping);
    }

    /**
     * 得到Patch映射哈希表
     * 
     * @return
     */
    public static Map<String, ControllerMapping> getPatchMappings() {
        return patchMappings;
    }

}
