package org.leo.web;

import org.junit.Test;
import org.leo.web.controller.ExceptionController;
import org.leo.web.core.WebServer;
import org.leo.web.interceptor.CorsInterceptor;

public class WebServerTest {

    @Test
    public void test() {
        // 忽略指定url
        WebServer.getIgnoreUrls().add("/favicon.ico");
        
        // 全局异常处理
        WebServer.setExceptionHandler(new ExceptionController());
        
        // 设置监听端口号
        WebServer server = new WebServer(2006);
        
        // 设置Http最大内容长度（默认 为10M）
        server.setMaxContentLength(1024 * 1024 * 50);
        
        // 设置Controller所在包
        server.setControllerBasePackage("org.leo.web.controller");
        
        // 添加拦截器，按照添加的顺序执行。
        // 跨域拦截器
        server.addInterceptor(new CorsInterceptor(), "/不用拦截的url");
        
        try {
            server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
