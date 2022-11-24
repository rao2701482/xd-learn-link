package net.xdclass.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {

    /**
     * 定义两种防重类型
     * PARAM: 以IP + 类名 + 方法名组合为Token, 然后以Token为Key尝试加分布式锁
     * TOKEN: 先获取tokne, 然后再销毁token时利用Redis.delete()防重
     */
    enum Type { PARAM, TOKEN}

    /**
     * 默认为PARAM类型
     * @return
     */
    Type limitType() default Type.PARAM;

    /**
     * 参数类型的加锁过期时间
     * @return
     */
    long lockTime() default 5;

}
