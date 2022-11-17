package net.xdclass.controller;


import net.xdclass.controller.request.ShortLinkAddRequest;
import net.xdclass.service.ShortLinkService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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


}

