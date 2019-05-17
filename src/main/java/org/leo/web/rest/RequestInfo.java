package org.leo.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.leo.web.multipart.MultipartFile;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 请求信息类
 * 
 * @author Leo
 * @date 2018/3/16
 */
public final class RequestInfo {
    
    private FullHttpRequest request;
    
    private HttpResponse response;
    
    private Map<String, Object> parameters = new HashMap<>();
    
    private Map<String, String> headers = new HashMap<>();
    
    private String body;
    
    private Map<String, String> formData = new HashMap<>();
    
    private List<MultipartFile> files = new ArrayList<>(8);

    private Boolean isJson;

    private Boolean isXml;

    public RequestInfo() {
        this.isJson = false;
        this.isXml = false;
    }

    public FullHttpRequest getRequest() {
        return this.request;
    }
    
    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }
    
    public Map<String, Object> getParameters() {
        return this.parameters;
    }
    
    public Map<String, String> getHeaders() {
        return this.headers;
    }
    
    public String getBody() {
        return this.body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    
    public Map<String, String> getFormData() {
        return this.formData;
    }
    
    public HttpResponse getResponse() {
        return this.response;
    }
    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public Boolean getIsJson() {
        return isJson;
    }

    public void setIsJson(Boolean json) {
        isJson = json;
    }

    public Boolean getIsXml() {
        return isXml;
    }

    public void setIsXml(Boolean xml) {
        isXml = xml;
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "request=" + request +
                ", response=" + response +
                ", parameters=" + parameters +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", formData=" + formData +
                ", files=" + files +
                ", isJson=" + isJson +
                ", isXml=" + isXml +
                '}';
    }
}
