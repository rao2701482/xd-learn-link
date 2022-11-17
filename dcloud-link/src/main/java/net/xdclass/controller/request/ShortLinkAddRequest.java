package net.xdclass.controller.request;

import lombok.Data;

import java.util.Date;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Data
public class ShortLinkAddRequest {


    /**
     * 组
     */
    private Long groupId;

    /**
     * 短链标题
     */
    private String title;

    /**
     * 原生url
     */
    private String originalUrl;

    /**
     * 域名id
     */
    private Long domainId;

    /**
     * 域名类型
     */
    private String domainType;

    /**
     * 过期时间
     */
    private Date expired;

}
