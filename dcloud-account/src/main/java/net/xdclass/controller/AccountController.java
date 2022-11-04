package net.xdclass.controller;


import net.xdclass.enums.BizCodeEnum;
import net.xdclass.service.FileService;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 二当家小D
 * @since 2021-11-09
 */
@RestController
@RequestMapping("/api/account/v1")
public class AccountController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传 最大默认1M
     *  文件格式、拓展名等判断
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public JsonData uploadUserImg(@RequestPart("file") MultipartFile file){

        String result = fileService.uploadUserImg(file);

        return result !=null ? JsonData.buildSuccess(result):JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);

    }

}

