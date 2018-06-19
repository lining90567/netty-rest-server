package org.leo.web.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.leo.web.rest.RequestDispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Http 请求处理器
 * 
 * @author Leo
 * @date 2018/3/16
 */
final class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
    private final static Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause.getMessage());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(WebServer.getIgnoreUrls().contains(request.uri())) {
            return;
        }
//        if (request instanceof HttpContent) {
//            HttpDataFactory factory =
//                    new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
//            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request, CharsetUtil.UTF_8);
//            List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
//            for (InterfaceHttpData data : datas) {
//                if(data.getHttpDataType() == HttpDataType.FileUpload) {
//                    FileUpload fileUpload = (FileUpload) data;
//                    String fileName = fileUpload.getFilename();
//                    if(fileUpload.isCompleted()) {
//                        // 保存到磁盘
//                        //fileUpload.renameTo(new File("d:\\6.png"));
//                        java.io.OutputStream out = null;
//                        try {
//                            out = new java.io.FileOutputStream("d:\\ok.png");
//                            out.write(fileUpload.get());
//                        } finally {
//                            out.close();
//                        }
//                    }
//                }
//            }
//            
//            //messageOfClient = buf.toString(io.netty.util.CharsetUtil.UTF_8);
//            //logger.debug(buf.toString(io.netty.util.CharsetUtil.UTF_8));
// 
////            if(content instanceof LastHttpContent){  
////                //此处对其他客户端的心跳包不予处理，因为服务端掉线之后会客户端会循环侦测连接，客户端断掉之后将服务端将不打印输出信息  
////                if(messageToSend.length()>12&&messageToSend.substring(0, 2).contentEquals("DB")){  
////
////                    //消息长度符合一定条件，则是需要向其他客户端发送的数据库消息，调用方法转发  
////                    System.out.println("Server已读取数据库持久化信息，将开始向所有客户端发送");  
////                    ServerDataSyncServer.channels.remove(ctx.channel());  
////                    System.out.println("messageToSend   "+messageToSend );  
////                    String messageContent = messageToSend;  
////                    ServerDataSyncServerSendGroup.sendToAllChannel(messageContent);  
////                    messageToSend = "";  
////                }  
////            }  
//        }
        new RequestDispatcher().doDispatch(request, ctx);
    }

}
