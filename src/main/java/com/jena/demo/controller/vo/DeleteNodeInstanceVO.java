package com.jena.demo.controller.vo;

import lombok.*;

import java.util.Date;

/**
 * @ClassName DeleteOntModelVO
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 11:41 下午
 * @Email wangjie_fourth@163.com
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteNodeInstanceVO {
    private String id;
    private Date updateTime;
    private Integer type;
    private String name;
}
