package com.jena.demo.controller.vo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DeleteRelationShipVo
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 11:44 下午
 * @Email wangjie_fourth@163.com
 **/
@Getter
@Setter
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "I know what I'm doing")
public class DeleteRelationShipVo {
    private String id;
    private Date updateTime;
    private Integer type;
    private List<RelationShip> relationShips;
}
