package org.leo.web.rest.mapping;

import java.lang.reflect.Method;

/**
 * 请求映射策略上下文类
 * 
 * @author Leo
 * @date 2018/3/16
 */
public final class RequestMappingRegisterContext {
    
    private RequestMappingRegisterStrategy strategy;

    public RequestMappingRegisterContext(RequestMappingRegisterStrategy strategy) {
        this.strategy = strategy;  
    }
    
    /**
     * 注册 Mapping
     * @param clazz
     * @param baseUrl
     * @param method
     */
    public void registerMapping(Class<?> clazz, String baseUrl, Method method) {
        if(this.strategy == null) {
            return;
        }
        this.strategy.register(clazz, baseUrl, method);
    }
    
}
