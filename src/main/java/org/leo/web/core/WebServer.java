package org.leo.web.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.leo.web.rest.ControllerFactory;
import org.leo.web.rest.controller.ExceptionHandler;
import org.leo.web.rest.interceptor.Interceptor;
import org.leo.web.rest.interceptor.InterceptorRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Web 服务器类
 * 
 * @author Leo
 * @date 2018/3/23
 */
public final class WebServer {
    
    private final static Logger logger = LoggerFactory.getLogger(WebServer.class);
    
    /**
     * 监听端口号
     */
    private int port = 0;
    
    /**
     * Boss线程数
     */
    private int bossThreads = 1;
    
    /**
     * Worker线程数
     */
    private int workerThreads = 2;
    
    /**
     * REST控制器所在包名
     */
    private String controllerBasePackage = "";
    
    /**
     * 忽略Url列表（不搜索Mapping）
     */
    private static List<String> ignoreUrls = new ArrayList<>(16);
    
    /**
     * 以上处理器
     */
    private static ExceptionHandler exceptionHandler;
    
    /**
     * Http 最大内容长度，默认为10M。
     */
    private int maxContentLength = 1024 * 1024 * 10;
    
    public WebServer(int port) {
        this.port = port;
    }
    
    public int getBossThreads() {
        return this.bossThreads;
    }
    public void setBossThreads(int threads) {
        this.bossThreads = threads;
    }
    
    public int getWorkerThreads() {
        return this.workerThreads;
    }
    public void setWorkerThreads(int threads) {
        this.workerThreads = threads;
    }
    
    public int getMaxContentLength() {
        return this.maxContentLength;
    }
    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
    
    public String getControllerBasePackage() {
        return this.controllerBasePackage;
    }
    public void setControllerBasePackage(String controllerBasePackage) {
        this.controllerBasePackage = controllerBasePackage;
    }
    
    public void addInterceptor(Interceptor interceptor) {
        try {
            InterceptorRegistry.addInterceptor(interceptor);
        } catch (Exception e) {
            logger.error("Add filter failed, ", e.getMessage());
        }
    }
    
    public void addInterceptor(Interceptor interceptor, String... excludeMappings) {
        try {
            InterceptorRegistry.addInterceptor(interceptor, excludeMappings);
        } catch (Exception e) {
            logger.error("Add filter failed, ", e.getMessage());
        }
    }
    
    public static List<String> getIgnoreUrls() {
        return ignoreUrls;
    }
    
    public static ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
    
    public static void setExceptionHandler(ExceptionHandler handler) {
        exceptionHandler = handler;
    }
    
    /**
     * 启动服务
     * @throws InterruptedException 
     */
    public void start() throws InterruptedException {
        // 注册所有REST Controller
        new ControllerFactory().registerController(this.controllerBasePackage);
        
        // BossGroup处理nio的Accept事件（TCP连接）
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(this.bossThreads);
        // Worker处理nio的Read和Write事件（通道的I/O事件）
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(this.workerThreads);
        
        try {
            // handler在初始化时就会执行，而childHandler会在客户端成功connect后才执行。
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)       
            .childHandler(new HandlerInitializer(this.maxContentLength));
            
            ChannelFuture f = bootstrap.bind(port).sync();
            logger.info("The netty rest server is now ready to accept requests on port {}", this.port); 
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
