package net.xdclass.controller;


import net.xdclass.service.DomainService;
import net.xdclass.util.JsonData;
import net.xdclass.vo.DomainVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 二当家小D
 * @since 2021-12-09
 */
@RestController
@RequestMapping("/api/domain/v1")
public class DomainController {


    @Autowired
    private DomainService domainService;

    /**
     * 列举全部可用域名列表
     * @return
     */
    @GetMapping("list")
    public JsonData listAll(){

        List<DomainVO> list = domainService.listAll();

        return JsonData.buildSuccess(list);

    }


}

