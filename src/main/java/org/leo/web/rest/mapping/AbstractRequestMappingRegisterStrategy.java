package org.leo.web.rest.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import org.leo.web.annotation.JsonResponse;
import org.leo.web.annotation.PathVariable;
import org.leo.web.annotation.RequestBody;
import org.leo.web.annotation.RequestHeader;
import org.leo.web.annotation.RequestParam;
import org.leo.web.annotation.UploadFile;
import org.leo.web.annotation.UploadFiles;
import org.leo.web.annotation.UrlEncodedForm;
import org.leo.web.rest.HttpResponse;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 请求映射注册抽象策略类
 * 
 * @author Leo
 * @date 2018/3/27
 */
abstract class AbstractRequestMappingRegisterStrategy implements RequestMappingRegisterStrategy {
    
    /**
     * 注册请求映射
     * @param clazz
     * @param baseUrl
     * @param method
     */
    @Override
    public void register(Class<?> clazz, String baseUrl, Method method) {
        // 得到url
        String methodMappingUrl = getMethodUrl(method);
        String url = getMethodUrl(baseUrl, methodMappingUrl);
        if(url == null || url.trim().isEmpty()) {
            return;
        }
        
        ControllerMapping mapping = new ControllerMapping();
        mapping.setUrl(url);
        mapping.setClassName(clazz.getName());
        mapping.setClassMethod(method.getName());
        mapping.setJsonResponse(method.getAnnotation(JsonResponse.class) != null);
        String httpMethod = getHttpMethod();
        
        if(httpMethod != null) {
            // 得到参数
            Parameter[] ps = method.getParameters();
            if(ps.length > 0) {
                // 得到所有参数名
                String[] paramNames = getMethodParameterNamesByAsm4(clazz, method);
                for(int i = 0; i < ps.length; i++) {
                    ControllerMappingParameter cmp = new ControllerMappingParameter();
                    cmp.setDataType(ps[i].getType());
                    if(ps[i].getType().equals(FullHttpRequest.class)) {
                        cmp.setName(paramNames[i]);
                        cmp.setType(ControllerMappingParameterTypeEnum.HTTP_REQUEST);
                        mapping.getParameters().add(cmp);
                        continue;
                    }
                    
                    if(ps[i].getType().equals(HttpResponse.class)) {
                        cmp.setName(paramNames[i]);
                        cmp.setType(ControllerMappingParameterTypeEnum.HTTP_RESPONSE);
                        mapping.getParameters().add(cmp);
                        continue;
                    }                    
                    
                    if(ps[i].getAnnotation(RequestParam.class) != null) {
                        RequestParam requestParam = ps[i].getAnnotation(RequestParam.class);
                        cmp.setName(requestParam.value());
                        cmp.setRequired(requestParam.required());
                        cmp.setType(ControllerMappingParameterTypeEnum.REQUEST_PARAM);
                        mapping.getParameters().add(cmp);
                        continue;
                    }
                    
                    if(ps[i].getAnnotation(RequestHeader.class) != null) {
                        RequestHeader requestHeader = ps[i].getAnnotation(RequestHeader.class);
                        cmp.setName((requestHeader.value() != null && !requestHeader.value().trim().isEmpty()) ?
                                requestHeader.value().trim() : paramNames[i]);
                        cmp.setRequired(requestHeader.required());
                        cmp.setType(ControllerMappingParameterTypeEnum.REQUEST_HEADER);
                        mapping.getParameters().add(cmp);
                        continue;
                    }
                    
                    if(ps[i].getAnnotation(PathVariable.class) != null) {
                        PathVariable pathVariable = ps[i].getAnnotation(PathVariable.class);
                        cmp.setName((pathVariable.value() != null && !pathVariable.value().trim().isEmpty()) ?
                                pathVariable.value().trim() : paramNames[i]);
                        cmp.setType(ControllerMappingParameterTypeEnum.PATH_VARIABLE);
                        mapping.getParameters().add(cmp);
                        continue;
                    }
                    
                    if(ps[i].getAnnotation(RequestBody.class) != null) {
                        cmp.setName(paramNames[i]);
                        cmp.setType(ControllerMappingParameterTypeEnum.REQUEST_BODY);
                        mapping.getParameters().add(cmp);
                        continue;
                    }
                    
                    if(ps[i].getAnnotation(UrlEncodedForm.class) != null) {
                        cmp.setName(paramNames[i]);
                        cmp.setType(ControllerMappingParameterTypeEnum.URL_ENCODED_FORM);
                        mapping.getParameters().add(cmp);
                        continue;
                    }
                    
                    if(ps[i].getAnnotation(UploadFile.class) != null) {
                        cmp.setName(paramNames[i]);
                        cmp.setType(ControllerMappingParameterTypeEnum.UPLOAD_FILE);
                        mapping.getParameters().add(cmp);
                        continue;
                    }
                    
                    if(ps[i].getAnnotation(UploadFiles.class) != null) {
                        cmp.setName(paramNames[i]);
                        cmp.setType(ControllerMappingParameterTypeEnum.UPLOAD_FILES);
                        mapping.getParameters().add(cmp);
                        continue;
                    }
                    
                    cmp.setName(paramNames[i]);
                    cmp.setType(ControllerMappingParameterTypeEnum.REQUEST_PARAM);
                    mapping.getParameters().add(cmp);
                }
            }
        }
        registerMapping(url, mapping);
    }
    
    /**
     * 得到控制器方法的Url
     * @param method
     * @return
     */
    abstract String getMethodUrl(Method method);
    
    /**
     * 得到Http请求的方法类型
     * @return
     */
    abstract String getHttpMethod();
    
    /**
     * 注册Mapping
     * @param url
     * @param mapping
     */
    abstract void registerMapping(String url, ControllerMapping mapping);
    
    /**
     * 得到方法Url
     * @param baseUrl
     * @param methodMappingUrl
     * @return
     */
    private String getMethodUrl(String baseUrl, String methodMappingUrl) {
        StringBuilder url = new StringBuilder(256);
        url.append((baseUrl == null || baseUrl.trim().isEmpty()) ? "" : baseUrl.trim());
        if(methodMappingUrl != null && !methodMappingUrl.trim().isEmpty()) {
            String methodMappingUrlTrim = methodMappingUrl.trim();
            if(!methodMappingUrlTrim.startsWith("/")) {
                methodMappingUrlTrim = "/" + methodMappingUrlTrim;
            }
            if(url.toString().endsWith("/")) {
                url.setLength(url.length() - 1);
            }
            url.append(methodMappingUrlTrim);
        }
        return url.toString();
    }
    
    /**
     * 得到方法的所有参数名称
     * @param clazz
     * @param method
     * @return
     */
    private String[] getMethodParameterNamesByAsm4(Class<?> clazz, final Method method) {  
        final Class<?>[] parameterTypes = method.getParameterTypes();  
        if (parameterTypes == null || parameterTypes.length == 0) {  
            return null;  
        }  
        final Type[] types = new Type[parameterTypes.length];  
        for (int i = 0; i < parameterTypes.length; i++) {  
            types[i] = Type.getType(parameterTypes[i]);  
        }  
        final String[] parameterNames = new String[parameterTypes.length];  
  
        String className = clazz.getName();  
        int lastDotIndex = className.lastIndexOf(".");  
        className = className.substring(lastDotIndex + 1) + ".class";  
        InputStream is = clazz.getResourceAsStream(className);  
        try {  
            ClassReader classReader = new ClassReader(is);  
            classReader.accept(new ClassVisitor(Opcodes.ASM4) {  
                @Override  
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {  
                    // 只处理指定的方法  
                    Type[] argumentTypes = Type.getArgumentTypes(desc);  
                    if (!method.getName().equals(name) || !Arrays.equals(argumentTypes, types)) {  
                        return null;  
                    }  
                    return new MethodVisitor(Opcodes.ASM4) {  
                        @Override  
                        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {  
                            // 静态方法第一个参数就是方法的参数，如果是实例方法，第一个参数是this  
                            if (Modifier.isStatic(method.getModifiers())) {  
                                parameterNames[index] = name;  
                            }
                            else if (index > 0 && index <= parameterNames.length) {  
                                parameterNames[index - 1] = name;  
                            }  
                        }  
                    };  
  
                }  
            }, 0);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return parameterNames;  
    }    

}
