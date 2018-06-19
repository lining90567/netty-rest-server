package org.leo.web.rest.mapping;

/**
 * 请求映射参数类型枚举
 * 
 * @author Leo
 * @date 2018/3/26
 */
public enum ControllerMappingParameterTypeEnum {
    
    /**
     * Request Url 参数
     */
    REQUEST_PARAM,
    
    /**
     * 路径变量
     */
    PATH_VARIABLE,
    
    /**
     * Http Request
     */
    HTTP_REQUEST,
    
    /**
     * Http Response
     */
    HTTP_RESPONSE,
    
    /**
     * 请求体
     */
    REQUEST_BODY,
    
    /**
     * X-WWW-FORM-URLENCODED
     */
    URL_ENCODED_FORM,
    
    /**
     * Http Reqesut Header参数
     */
    REQUEST_HEADER,
    
    /**
     * 上传文件
     */
    UPLOAD_FILE,
    
    /**
     * 多个上传文件
     */
    UPLOAD_FILES

}
