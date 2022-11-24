package net.xdclass.controller;


import lombok.extern.slf4j.Slf4j;
import net.xdclass.controller.request.ConfirmOrderRequest;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.ClientTypeEnum;
import net.xdclass.enums.ProductOrderPayTypeEnum;
import net.xdclass.service.ProductOrderService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 二当家小D
 * @since 2021-12-25
 */
@Slf4j
@RestController
@RequestMapping("/productOrderDO")
public class ProductOrderController {

    @Autowired
    private ProductOrderService productOrderService;


    /**
     * 分页接口
     *
     * @return
     */
    @GetMapping("page")
    public JsonData page(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "state", required = false) String state
    ) {

        Map<String, Object> pageResult = productOrderService.page(page, size, state);
        return JsonData.buildSuccess(pageResult);
    }


    /**
     * 查询订单状态: (前端轮询查询)
     *
     * @param outTradeNo
     * @return
     */
    @GetMapping("query_state")
    public JsonData queryState(@RequestParam(value = "out_trade_no") String outTradeNo) {

        String state = productOrderService.queryProductOrderState(outTradeNo);

        return StringUtils.isBlank(state) ?
                JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST) : JsonData.buildSuccess(state);

    }


    /**
     * 下单接口
     * @param orderRequest
     * @param response
     */
    @PostMapping("confirm")
    public void confirmOrder(@RequestBody ConfirmOrderRequest orderRequest, HttpServletResponse response) {

        // 重要
        JsonData jsonData = productOrderService.confirmOrder(orderRequest);

        // 处理创建订单, 预支付步骤该返回上面数据类型
        if (jsonData.getCode() == 0) {

            //端类型
            String client = orderRequest.getClientType();
            //支付类型
            String payType = orderRequest.getPayType();

            //如果是支付宝支付，跳转网页，sdk除非
            if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.ALI_PAY.name())) {

                if (client.equalsIgnoreCase(ClientTypeEnum.PC.name())) {

                    CommonUtil.sendHtmlMessage(response, jsonData);

                } else if (client.equalsIgnoreCase(ClientTypeEnum.APP.name())) {

                } else if (client.equalsIgnoreCase(ClientTypeEnum.H5.name())) {

                }

            } else if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT_APY.name())) {
                //微信支付
                CommonUtil.sendJsonMessage(response, jsonData);
            }

        } else {
            log.error("创建订单失败{}", jsonData.toString());
            CommonUtil.sendJsonMessage(response, jsonData);
        }

    }

}

