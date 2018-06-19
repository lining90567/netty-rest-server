package org.leo.web.exception;

/**
 * 资源未发现异常类
 * 
 * @author Leo
 * @date 2018/3/16
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5563265404641097547L;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

}
