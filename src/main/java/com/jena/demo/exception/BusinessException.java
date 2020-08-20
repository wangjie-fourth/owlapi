package com.jena.demo.exception;


/**
 * 业务异常：一般是用户不当操作，并且这种错误是被我们之前处理的；
 * 这种异常不需要打error日志
 */
public class BusinessException extends Exception {


    private static final long serialVersionUID = -2004612073982288041L;

    public BusinessException() {
        super();
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

}
