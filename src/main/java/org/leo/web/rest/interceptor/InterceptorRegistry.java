package org.leo.web.rest.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 拦截器注册类
 * 
 * @author Leo
 * @date 2018/3/16
 */
public final class InterceptorRegistry {
    
    private final static Set<Interceptor> interceptors = new LinkedHashSet<>(8);
    
    private final static Map<String, List<String>> excludeMappings = new HashMap<String, List<String>>(8);
    
    public static void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public static void addInterceptor(Interceptor interceptor, String... excludeMappings) {
        interceptors.add(interceptor);
        List<String> excludeMappingList = new ArrayList<String>(8);
        for(String excludeMapping : excludeMappings) {
            excludeMappingList.add(excludeMapping);
        }
        InterceptorRegistry.excludeMappings.put(interceptor.getClass().getName(), excludeMappingList);
    }
    
    public static Set<Interceptor> getInterceptors() {
        return interceptors;
    }
    
    public static List<String> getExcludeMappings(Interceptor interceptor) {
        return excludeMappings.get(interceptor.getClass().getName());
    }

}
