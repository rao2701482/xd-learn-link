package net.xdclass.exception;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.util.JsonData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class CustomerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = BizException.class)
    public JsonData handler(Exception e) {

        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            log.error("[业务异常]: {}", e);
            return JsonData.buildCodeAndMsg(bizException.getCode(), bizException.getMessage());
        } else {
            log.error("[系统异常]: {}", e);
            return JsonData.buildError("系统异常");
        }

    }

}
