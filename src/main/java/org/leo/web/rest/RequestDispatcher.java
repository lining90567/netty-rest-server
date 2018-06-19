package org.leo.web.rest;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.leo.web.multipart.MultipartFile;
import org.leo.web.rest.interceptor.Interceptor;
import org.leo.web.rest.interceptor.InterceptorRegistry;

import java.util.Map.Entry;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

/**
 * 请求分派器
 * 
 * @author Leo
 * @date 2018/3/27
 */
public final class RequestDispatcher {

    /**
     * 执行请求分派
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    public void doDispatch(FullHttpRequest request, ChannelHandlerContext channelHandlerContext) throws Exception {
        HttpResponse response = new HttpResponse(channelHandlerContext);

        // 执行拦截器
        java.util.Stack<Interceptor> executedInterceptors = new java.util.Stack<Interceptor>();
        for (Interceptor interceptor : InterceptorRegistry.getInterceptors()) {
            if (this.allowExecuteInterceptor(request.uri(), interceptor)) {
                executedInterceptors.push(interceptor);
                if (!interceptor.preHandle(request, response)) {
                    // 调用已执行的所有拦截器的afterCompletion方法
                    while(!executedInterceptors.isEmpty()) {
                        executedInterceptors.pop().afterCompletion(request, response);
                    }
                    return;
                }
            }
        }
        
        ChannelFuture f = null;
        if(request.method().name().equalsIgnoreCase("OPTIONS")) {
            // 处理“预检”请求
            f = processOptionsRequest(request, response, channelHandlerContext);
        }

        if(!request.method().name().equalsIgnoreCase("OPTIONS")) {
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setRequest(request);
            requestInfo.setResponse(response);
            QueryStringDecoder queryStrdecoder = new QueryStringDecoder(request.uri());
            Set<Entry<String, List<String>>> entrySet = queryStrdecoder.parameters().entrySet();
            entrySet.forEach(entry -> {
                requestInfo.getParameters().put(entry.getKey(), entry.getValue().get(0));
            });
    
            Set<String> headerNames = request.headers().names();
            for (String headerName : headerNames) {
                requestInfo.getHeaders().put(headerName, request.headers().get(headerName));
            }
    
            if (!request.method().name().equalsIgnoreCase("GET")) {
                String contentType = requestInfo.getHeaders().get("Content-Type");
                if (contentType != null) {
                    if(contentType.contains(";")) {
                        contentType = contentType.split(";")[0];
                    }
                    switch (contentType.toLowerCase()) {
                    case "application/json":
                    case "application/json;charset=utf-8":
                    case "text/xml":
                        requestInfo.setBody(request.content().toString(Charset.forName("UTF-8")));
                        break;
                    case "application/x-www-form-urlencoded":
                        HttpPostRequestDecoder formDecoder = new HttpPostRequestDecoder(request);
                        formDecoder.offer(request);
                        List<InterfaceHttpData> parmList = formDecoder.getBodyHttpDatas();
                        for (InterfaceHttpData parm : parmList) {
                            Attribute data = (Attribute) parm;
                            requestInfo.getFormData().put(data.getName(), data.getValue());
                        }
                        break;
                    case "multipart/form-data":
                        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
                        List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
                        for (InterfaceHttpData data : datas) {
                            if(data.getHttpDataType() == HttpDataType.FileUpload) {
                                FileUpload fileUpload = (FileUpload) data;
                                if(fileUpload.isCompleted()) {
                                    MultipartFile file = new MultipartFile();
                                    file.setFileName(fileUpload.getFilename());
                                    file.setFileType(fileUpload.getContentType());
                                    file.setFileData(fileUpload.get());
                                    requestInfo.getFiles().add(file);
                                }
                                continue;
                            }
                            if(data.getHttpDataType() == HttpDataType.Attribute) {
                                Attribute attribute = (Attribute) data;
                                requestInfo.getFormData().put(attribute.getName(), attribute.getValue());
                            }
                        }
                    }
                }
            }
    
            f = new RequestHandler().handleRequest(requestInfo);
        }

        // 执行拦截器
        for (Interceptor interceptor : InterceptorRegistry.getInterceptors()) {
            if (this.allowExecuteInterceptor(request.uri(), interceptor)) {
                interceptor.postHandle(request, response);
            }
        }
        
        // 如果是“预检”请求，则处理后关闭连接。
        if(request.method().name().equalsIgnoreCase("OPTIONS")) {
            if(f != null) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }
        if(!HttpUtil.isKeepAlive(request)) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 判断是否执行拦截器
     * 
     * @param url
     * @param interceptor
     * @return
     */
    private boolean allowExecuteInterceptor(String url, Interceptor interceptor) {
        List<String> excludeMappings = InterceptorRegistry.getExcludeMappings(interceptor);
        if(excludeMappings != null) {
            for (String excludeMapping : excludeMappings) {
                if (url.startsWith(excludeMapping)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 处理Options请求
     * @param request
     * @param response
     * @return
     */
    private ChannelFuture processOptionsRequest(FullHttpRequest request, HttpResponse response, ChannelHandlerContext channelHandlerContext) {
        String[] requestHeaders = request.headers().get("Access-Control-Request-Headers").split(",");
        for(String requestHeader : requestHeaders) {
            if(!requestHeader.trim().isEmpty()) {
                if(!requestHeaderAllowed(requestHeader, response.getHeaders())) {
                    FullHttpResponse optionsResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND,
                            Unpooled.copiedBuffer("", CharsetUtil.UTF_8));
                    HttpContextHolder.getResponse().getChannelHandlerContext().writeAndFlush(optionsResponse).addListener(ChannelFutureListener.CLOSE);
                    return null;
                }
            }
        }
        FullHttpResponse optionsResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer("", CharsetUtil.UTF_8));
        Map<String, String> responseHeaders = response.getHeaders();
        Set<Entry<String, String>> headersEntrySet = responseHeaders.entrySet();
        for(Entry<String, String> entry : headersEntrySet) {
            optionsResponse.headers().add(entry.getKey(), entry.getValue());
        }
        optionsResponse.headers().setInt("Content-Length", optionsResponse.content().readableBytes());        
        return channelHandlerContext.writeAndFlush(optionsResponse);
    }
    
    /**
     * 判断请求头是否被允许
     * @param requestHeader
     * @param responseHeaders
     * @return
     */
    private boolean requestHeaderAllowed(String requestHeader, Map<String, String> responseHeaders) {
        String allowedHeader = responseHeaders.get("Access-Control-Allow-Headers");
        if(allowedHeader != null && !allowedHeader.trim().isEmpty()) {
            String[] allowedHeaders = allowedHeader.split(",");
            for(String header : allowedHeaders) {
                if(requestHeader.equalsIgnoreCase(header)) {
                    return true;
                }
            }
        }
        return false;
    }

}
