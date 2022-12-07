package net.xdclass.aspect;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.annotation.RepeatSubmit;
import net.xdclass.constant.RedisKey;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.exception.BizException;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class RepeatSubmitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 定义使用了目标注解的方法为切点
     * @param repeatSubmit
     */
    @Pointcut("@annotation(repeatSubmit)")
    public void pointCutRepectSubmit(RepeatSubmit repeatSubmit) {

    }

    @Around("pointCutRepectSubmit(repeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, RepeatSubmit repeatSubmit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        boolean res = false;

        // 防重类型
        String type = repeatSubmit.limitType().name();

        // 参数类型防重
        if (type.equalsIgnoreCase(RepeatSubmit.Type.PARAM.name())) {

            long lockTime = repeatSubmit.lockTime();

            String ipAddr = CommonUtil.getIpAddr(request);

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

            Method method = methodSignature.getMethod();

            String className = method.getDeclaringClass().getName();

            String key = "order-server:repeat_submit:"+CommonUtil.MD5(String.format("%s-%s-%s-%s",ipAddr,className,method,accountNo));

            RLock lock = redissonClient.getLock(key);
            // 尝试加锁，最多等待0秒，上锁以后5秒自动解锁 [lockTime默认为5s, 可以自定义]
            res = lock.tryLock(0, lockTime, TimeUnit.SECONDS);

        } else {
            // Token类型防重
            String requestToken = request.getHeader("request-token");

            if (StringUtils.isBlank(requestToken)) {
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
            }

            String key = String.format(RedisKey.SUBMIT_ORDER_TOKEN_KEY, accountNo, requestToken);

            res = redisTemplate.delete(key);
        }

        if (!res) {
            log.error("请求重复提交");
            return null;
        }
        log.info("环绕通知执行前");

        Object obj = joinPoint.proceed();

        log.info("环绕通知执行后");

        return obj;
    }

}
