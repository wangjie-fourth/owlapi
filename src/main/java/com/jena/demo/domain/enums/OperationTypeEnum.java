package com.jena.demo.domain.enums;

import lombok.Getter;

/**
 * @EnumName OperationTypeEnum
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 11:34 下午
 * @Email wangjie_fourth@163.com
 **/
@Getter
public enum OperationTypeEnum {

    ADD_NODE(1, "添加节点");

    private final Integer code;
    private final String msg;

    OperationTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
