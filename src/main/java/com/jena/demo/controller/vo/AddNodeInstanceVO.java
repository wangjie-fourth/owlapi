package com.jena.demo.controller.vo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * @ClassName AddOntModelVO
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 11:29 下午
 * @Email wangjie_fourth@163.com
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "I know what I'm doing")
public class AddNodeInstanceVO {
    private String id;
    private Date updateTime;
    private Integer type;
    @NotEmpty(message = "实体名称不能为空")
    private String name;
    @NotEmpty(message = "实体所属类不能为空")
    private String className;
    private List<RelationShip> relationShips;
}
