package com.jena.demo.controller.vo;

import lombok.*;

/**
 * @ClassName RelationShip
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 11:37 下午
 * @Email wangjie_fourth@163.com
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RelationShip {
    private String subject;
    private String objectPropoty;
    private String object;
}

