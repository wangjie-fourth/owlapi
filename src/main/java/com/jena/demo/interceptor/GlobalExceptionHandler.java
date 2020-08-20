package com.jena.demo.interceptor;

import com.jena.demo.exception.BusinessException;
import com.jena.demo.exception.ServiceException;
import com.jena.demo.util.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResultVO runtimeExceptionHandler(RuntimeException ex) {
        log.error("运行时异常, message:{}", ex.getMessage(), ex);
        return ResultVO.fail("id", new Date(), ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResultVO businessExceptionHandler(BusinessException ex) {
        log.warn("业务异常, message:{}", ex.getMessage());
        return ResultVO.fail("id",new Date(), ex.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResultVO serviceExceptionHandlerr(ServiceException ex) {
        log.error("系统异常，message:{}", ex.getMessage());
        return ResultVO.fail("id", new Date(), ex.getMessage());
    }

}
