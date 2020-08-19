package com.jena.demo.interceptor;

import com.jena.demo.exception.BusinessException;
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
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ResultVO runtimeExceptionHandler(RuntimeException ex) {
        log.error("运行时异常, message:{}", ex.getMessage(), ex);
        ResultVO respDTO = ResultVO.fail("id", new Date(), ex.getMessage());
        return respDTO;
    }

    @ExceptionHandler(BusinessException.class)
    public ResultVO businessExceptionHandler(BusinessException ex) {
        log.warn("业务异常, message:{}", ex.getMessage());
        return ResultVO.fail("id",new Date(), ex.getMessage());
    }
}
