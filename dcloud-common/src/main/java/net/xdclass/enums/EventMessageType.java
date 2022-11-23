package net.xdclass.enums;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

public enum EventMessageType {

    /**
     * 短链创建
     */
    SHORT_LINK_ADD,
    /**
     * 短链创建 C端
     */
    SHORT_LINK_ADD_LINK,

    /**
     * 短链创建 B端
     */
    SHORT_LINK_ADD_MAPPING,



    /**
     * 短链删除
     */
    SHORT_LINK_DEL,


    /**
     * 短链删除 C端
     */
    SHORT_LINK_DEL_LINK,

    /**
     * 短链删除 B端
     */
    SHORT_LINK_DEL_MAPPING,


    /**
     * 短链更新
     */
    SHORT_LINK_UPDATE,


    /**
     * 短链更新 C端
     */
    SHORT_LINK_UPDATE_LINK,

    /**
     * 短链更新 B端
     */
    SHORT_LINK_UPDATE_MAPPING,
}
