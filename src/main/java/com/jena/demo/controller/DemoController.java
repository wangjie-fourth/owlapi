package com.jena.demo.controller;

import com.jena.demo.controller.vo.AddNodeInstanceVO;
import com.jena.demo.controller.vo.DeleteNodeInstanceVO;
import com.jena.demo.controller.vo.DeleteRelationShipVo;
import com.jena.demo.service.DemoService;
import com.jena.demo.util.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Objects;

/**
 * @ClassName DemoController
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 11:20 下午
 * @Email wangjie_fourth@163.com
 **/
@RestController
@Slf4j
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    /**
     * 新增节点：也就是新增一个实例，可能为这个实例添加关系
     *
     * @param addNodeInstanceVO 添加节点信息
     * @param bindingResult     校验规则
     * @return  反应结果
     */
    @PostMapping("/addNodeInstance")
    public ResultVO addNodeInstance(@RequestBody @Validated AddNodeInstanceVO addNodeInstanceVO, BindingResult bindingResult) {
        if (Objects.isNull(addNodeInstanceVO)){
            return ResultVO.nullFail();
        }
        if (bindingResult.hasErrors()){
            return ResultVO.fail(addNodeInstanceVO.getId(), new Date(),
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return demoService.addNodeInstance(addNodeInstanceVO);
    }

    /**
     * 删除节点
     *
     * @param deleteNodeInstanceVO  删除节点信息
     * @return  反应结果
     * @throws  FileNotFoundException   目标文件不存在
     */
    @PostMapping("/deleteNodeInstance")
    public ResultVO deleteNodeInstance(@RequestBody DeleteNodeInstanceVO deleteNodeInstanceVO) throws FileNotFoundException {
        if (Objects.isNull(deleteNodeInstanceVO)){
            return ResultVO.nullFail();
        }
        return demoService.deleteNodeInstance(deleteNodeInstanceVO);
    }

    /**
     * 删除对应节点的关系
     *
     * @param deleteRelationShipVo  删除关系信息
     * @return  反应结果
     */
    @PostMapping("/deleteObjectProperty")
    public ResultVO deleteRelationShip(@RequestBody DeleteRelationShipVo deleteRelationShipVo){
        if (Objects.isNull(deleteRelationShipVo) || Objects.isNull(deleteRelationShipVo.getRelationShips())){
            return ResultVO.nullFail();
        }
        return demoService.deleteObjectProperty(deleteRelationShipVo);
    }
}
