package com.jena.demo.service;

import com.jena.demo.controller.vo.AddNodeInstanceVO;
import com.jena.demo.controller.vo.DeleteNodeInstanceVO;
import com.jena.demo.controller.vo.DeleteRelationShipVo;
import com.jena.demo.controller.vo.RelationShip;
import com.jena.demo.exception.BusinessException;
import com.jena.demo.util.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Objects;

/**
 * @ClassName DemoService
 * @Description
 * @Author wangjie
 * @Date 2020/7/4 11:47 下午
 * @Email wangjie_fourth@163.com
 **/
@Service
@Slf4j
public class DemoService {
    @Resource
    private Demo demo;

    /**
     * 新增节点：也就是新增一个实例，可能为这个实例添加关系
     *
     * @param addNodeInstanceVO 添加节点信息
     * @return 操作结果
     * @throws  BusinessException   业务异常
     */
    public ResultVO addNodeInstance(AddNodeInstanceVO addNodeInstanceVO) throws BusinessException {
        if (!demo.hasCLass(addNodeInstanceVO.getClassName())) {
            log.warn("【" + addNodeInstanceVO.getClassName() + "】类不存在");
            throw new BusinessException("【" + addNodeInstanceVO.getClassName() + "】类不存在");
        }
        if (!demo.hasOWLNamedIndividual(addNodeInstanceVO.getName())) {
            log.info("不存在【" + addNodeInstanceVO.getName() + "】实例，开始新增");
            log.info("添加实例");
            demo.createOWLNamedIndividual(addNodeInstanceVO.getClassName(), addNodeInstanceVO.getName());
        }

        if (Objects.nonNull(addNodeInstanceVO.getRelationShips())) {
            for (RelationShip item : addNodeInstanceVO.getRelationShips()) {
                demo.createObjectPropertyForIndividual(item.getSubject(), item.getObject(), item.getObjectPropoty());
            }
        }
        return ResultVO.success(addNodeInstanceVO.getId(), new Date());
    }


    public ResultVO deleteNodeInstance(DeleteNodeInstanceVO deleteNodeInstanceVO) throws FileNotFoundException {
        if (Objects.nonNull(deleteNodeInstanceVO.getName())) {
            demo.deleteOWLNamedIndividual(deleteNodeInstanceVO.getName());
        }
        return ResultVO.success(deleteNodeInstanceVO.getId(), new Date());
    }

    public ResultVO deleteObjectProperty(DeleteRelationShipVo deleteRelationShipVo) {
        for (RelationShip item : deleteRelationShipVo.getRelationShips()) {
            demo.deleteObjectProperty(item.getSubject(), item.getObject(), item.getObjectPropoty());
        }
        return ResultVO.success(deleteRelationShipVo.getId(), new Date());
    }
}
