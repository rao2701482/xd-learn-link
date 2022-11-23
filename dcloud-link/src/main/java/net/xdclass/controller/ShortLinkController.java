package net.xdclass.controller;


import net.xdclass.controller.request.ShortLinkAddRequest;
import net.xdclass.controller.request.ShortLinkDelRequest;
import net.xdclass.controller.request.ShortLinkPageRequest;
import net.xdclass.controller.request.ShortLinkUpdateRequest;
import net.xdclass.service.ShortLinkService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 二当家小D
 * @since 2021-12-09
 */
@RestController
@RequestMapping("/api/link/v1")
public class ShortLinkController {

    @Autowired
    private ShortLinkService shortLinkService;


    @PostMapping("add")
    public JsonData createShortLink(@RequestBody ShortLinkAddRequest request){

        JsonData jsonData = shortLinkService.createShortLink(request);

        return jsonData;
    }

    /**
     * 分页查找短链
     */

    @RequestMapping("page")
    public JsonData pageByGroupId(@RequestBody ShortLinkPageRequest request){


        Map<String,Object> result = shortLinkService.pageByGroupId(request);

        return JsonData.buildSuccess(result);

    }


    /**
     * 删除短链
     * @param request
     * @return
     */
    @PostMapping("del")
    public JsonData del(@RequestBody ShortLinkDelRequest request){

        JsonData jsonData = shortLinkService.del(request);

        return jsonData;
    }





    /**
     * 更新短链
     * @param request
     * @return
     */
    @PostMapping("update")
    public JsonData update(@RequestBody ShortLinkUpdateRequest request){

        JsonData jsonData = shortLinkService.update(request);

        return jsonData;
    }

}

