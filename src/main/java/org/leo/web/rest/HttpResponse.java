package org.leo.web.rest;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.HashMap;

/**
 * Http 响应类
 * 
 * @author Leo
 * @date 2018/3/27
 */
public final class HttpResponse {
    
    public HttpResponse(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
    
    private ChannelHandlerContext channelHandlerContext;
    
    private Map<String, String> headers = new HashMap<>(16);
    
    private Map<String, String> cookies = new HashMap<>(16);
    
    public ChannelHandlerContext getChannelHandlerContext() {
        return this.channelHandlerContext;
    }
    
    public Map<String, String> getHeaders() {
        return this.headers;
    }
    
    public Map<String, String> getCookies() {
        return this.cookies;
    }
    
    /**
     * 输出响应
     * @param status
     * @param body
     * @throws InterruptedException 
     */
    public void write(HttpStatus status, String body) {
        HttpResponseStatus responstStatus = HttpResponseStatus.parseLine(String.valueOf(status.value()));
        FullHttpResponse response = null;
        if(body == null || body.trim().equals("")) {
            response = new DefaultFullHttpResponse(HTTP_1_1, responstStatus);
        } else {
            response = new DefaultFullHttpResponse(HTTP_1_1, responstStatus, Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
        }
        
        Set<Entry<String, String>> entrySet = headers.entrySet();
        for(Entry<String, String> entry : entrySet) {
            response.headers().add(entry.getKey(), entry.getValue());
        }
        response.headers().setInt("Content-Length", response.content().readableBytes());
        channelHandlerContext.writeAndFlush(response);
    }
    
    /**
     * 关闭Channel
     */
    public void closeChannel() {
        if(this.channelHandlerContext != null && this.channelHandlerContext.channel() != null) {
            this.channelHandlerContext.channel().close();
        }
    }
    
}
