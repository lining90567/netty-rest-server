package org.leo.web.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandlers;

/**
 * Netty Handler 初始化类
 * 
 * @author Leo
 * @date 2018/3/29
 */
final class HandlerInitializer extends ChannelInitializer<SocketChannel> {
    
    private int maxContentLength = 0;
    
    /**
     * 业务线程池线程数
     * 可通过 -Dhttp.executor.threads 设置
     */
    private static int eventExecutorGroupThreads = 0;
    
    /**
     * 业务线程池队列长度
     * 可通过 -Dhttp.executor.queues 设置
     */
    private static int eventExecutorGroupQueues = 0;
    
    static {
        eventExecutorGroupThreads = Integer.getInteger("http.executor.threads", 0);
        if(eventExecutorGroupThreads == 0) {
            eventExecutorGroupThreads = Runtime.getRuntime().availableProcessors() * 2;
        }
        
        eventExecutorGroupQueues = Integer.getInteger("http.executor.queues", 0);
        if(eventExecutorGroupQueues == 0) {
            eventExecutorGroupQueues = 1024;
        }
    }
    
    /**
     * 业务线程组
     */
    private static final EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(
            eventExecutorGroupThreads, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "HttpRequestHandlerThread_" + this.threadIndex.incrementAndGet());
                }
            }, eventExecutorGroupQueues, RejectedExecutionHandlers.reject());   
    
    public HandlerInitializer(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        /*
         * ChannelInboundHandler按照注册的先后顺序执行，ChannelOutboundHandler按照注册的先后顺序逆序执行。
         * HttpRequestDecoder、HttpObjectAggregator、HttpHandler为InboundHandler
         * HttpContentCompressor、HttpResponseEncoder为OutboundHandler
         * 在使用Handler的过程中，需要注意：
         * 1、ChannelInboundHandler之间的传递，通过调用 ctx.fireChannelRead(msg) 实现；调用ctx.write(msg) 将传递到ChannelOutboundHandler。
         * 2、ctx.write()方法执行后，需要调用flush()方法才能令它立即执行。
         * 3、ChannelOutboundHandler 在注册的时候需要放在最后一个ChannelInboundHandler之前，否则将无法传递到ChannelOutboundHandler。
         * 4、Handler的消费处理放在最后一个处理。
         */
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(maxContentLength));
        // 启用gzip（由于使用本地存储文件，不能启用gzip）
        //pipeline.addLast(new HttpContentCompressor(1));
        pipeline.addLast(new ChunkedWriteHandler());
        // 将HttpRequestHandler放在业务线程池中执行，避免阻塞worker线程。
        pipeline.addLast(eventExecutorGroup, "httpRequestHandler", new HttpRequestHandler());
    }

}
