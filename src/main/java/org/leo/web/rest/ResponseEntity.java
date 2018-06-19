package org.leo.web.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * Http 响应实体类
 * 
 * @author Leo
 * @date 2018/3/16
 */
public final class ResponseEntity<T> {

    public ResponseEntity() {

    }

    public ResponseEntity(HttpStatus status) {
        this.status = status;
    }

    public ResponseEntity(HttpStatus status, T body) {
        this.status = status;
        this.body = body;
    }

    public ResponseEntity(HttpStatus status, Map<String, String> headers, T body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }
    
    public ResponseEntity(HttpStatus status, Map<String, String> headers, T body, String mimetype) {
        this.status = status;
        this.headers = headers;
        this.body = body;
        this.mimetype = mimetype;
    }
    
    public ResponseEntity(HttpStatus status, Map<String, String> headers, T body, String mimetype, String fileName) {
        this.status = status;
        this.headers = headers;
        this.body = body;
        this.mimetype = mimetype;
        this.fileName = fileName;
    }

    private HttpStatus status;

    private T body;
    
    private String mimetype;
    
    private String fileName;

    private Map<String, String> headers = new HashMap<>(16);

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public T getBody() {
        return this.body;
    }

    public void setBody(T body) {
        this.body = body;
    }
    
    public String getMimetype() {
        return this.mimetype;
    }
    
    public String getFileName() {
        return this.fileName;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * 根据状态码创建ResponseBuilder
     * 
     * @param status
     * @return
     */
    public static ResponseBuilder status(HttpStatus status) {
        return new ResponseBuilder(status);
    }

    /**
     * 创建Http OK ResponseBuilder
     * 
     * @return
     */
    public static ResponseBuilder ok() {
        return status(HttpStatus.OK);
    }

    /**
     * 创建Http OK ResponseEntity
     * 
     * @param body
     * @return
     */
    public static <T> ResponseEntity<T> ok(T body) {
        ResponseBuilder builder = ok();
        return builder.build(body);
    }
    
    /**
     * 创建Http OK ResponseEntity
     * 
     * @param body
     * @param mimetype
     * @return
     */
    public static <T> ResponseEntity<T> ok(T body, String mimetype) {
        ResponseBuilder builder = ok();
        return builder.build(body, mimetype);
    }
    
    /**
     * 创建Http OK ResponseEntity
     * 
     * @param body
     * @param mimetype
     * @param fileName
     * @return
     */
    public static <T> ResponseEntity<T> ok(T body, String mimetype, String fileName) {
        ResponseBuilder builder = ok();
        return builder.build(body, mimetype, fileName);
    }

    /**
     * 创建Http Created ResponseBuilder
     * 
     * @return
     */
    public static ResponseBuilder created() {
        return status(HttpStatus.CREATED);
    }

    /**
     * 创建Http Created ResponseEntity
     * 
     * @param body
     * @return
     */
    public static <T> ResponseEntity<T> created(T body) {
        ResponseBuilder builder = created();
        return builder.build(body);
    }

    /**
     * 创建Http No Content ResponseBuilder
     * 
     * @return
     */
    public static ResponseBuilder noContent() {
        return status(HttpStatus.NO_CONTENT);
    }

    /**
     * 创建Http No Content ResponseEntity
     * 
     * @param body
     * @return
     */
    public static <T> ResponseEntity<T> noContent(T body) {
        ResponseBuilder builder = noContent();
        return builder.build(body);
    }

    /**
     * 创建Http Not Found ResponseBuilder
     * 
     * @return
     */
    public static ResponseBuilder notFound() {
        return status(HttpStatus.NOT_FOUND);
    }

    /**
     * 创建Http Not Found ResponseEntity
     * 
     * @param body
     * @return
     */
    public static <T> ResponseEntity<T> notFound(T body) {
        ResponseBuilder builder = notFound();
        return builder.build(body);
    }

    /**
     * 创建Http Internal Server Error ResponseEntity
     * 
     * @return
     */
    public static ResponseBuilder internalServerError() {
        return status(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 创建Http Internal Server Error ResponseEntity
     * 
     * @param body
     * @return
     */
    public static <T> ResponseEntity<T> internalServerError(T body) {
        ResponseBuilder builder = internalServerError();
        return builder.build(body);
    }

    /**
     * 设置headers
     * 
     * @param headers
     * @return
     */
    public ResponseEntity<T> headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Http Response 构建器
     * 
     * @author Leo
     */
    public static class ResponseBuilder {

        private HttpStatus status;

        private Map<String, String> headers;

        public ResponseBuilder(HttpStatus status) {
            this.status = status;
        }

        public ResponseBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public <T> ResponseEntity<T> build() {
            return build(null);
        }

        public <T> ResponseEntity<T> build(T body) {
            return new ResponseEntity<>(this.status, this.headers, body);
        }
        
        public <T> ResponseEntity<T> build(T body, String mimetype) {
            return new ResponseEntity<>(this.status, this.headers, body, mimetype);
        }
        
        public <T> ResponseEntity<T> build(T body, String mimetype, String fileName) {
            return new ResponseEntity<>(this.status, this.headers, body, mimetype, fileName);
        }
    }

}
