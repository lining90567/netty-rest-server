package org.leo.web.rest.controller;

/**
 * 异常处理器
 * 
 * @author Leo
 * @date 2018/3/16
 */
public interface ExceptionHandler {

    /**
     * 处理异常
     * @param e
     */
    void doHandle(Exception e);
    
}
