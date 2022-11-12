package net.xdclass.interceptor;

import feign.Request;
import io.jsonwebtoken.Claims;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.model.LoginUser;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JWTUtil;
import net.xdclass.util.JsonData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<LoginUser>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 前端可能发送一个无含义的内容, 这个类型的请求类型放行
        if (Request.HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }

        String accessToken = request.getHeader("token");
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter("token");
        }

        if (StringUtils.isNotBlank(accessToken)) {
            Claims claims = JWTUtil.checkJWT(accessToken);
            if (claims == null) {
                // 未登录
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }

            LoginUser loginUser = getLoginUser(claims);

            threadLocal.set(loginUser);
            return true;
        }
        CommonUtil.sendJsonMessage(response,JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;

    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }


    /**
     * 取值并构造
     * @param claims
     * @return
     */
    private LoginUser getLoginUser(Claims claims) {
        Long accountNo = Long.parseLong(claims.get("account_no").toString());
        String headImg = (String) claims.get("head_img");
        String username = (String) claims.get("username");
        String mail = (String) claims.get("mail");
        String phone = (String) claims.get("phone");
        String auth = (String) claims.get("auth");

        LoginUser loginUser = LoginUser.builder()
                .accountNo(accountNo)
                .auth(auth)
                .phone(phone)
                .headImg(headImg)
                .mail(mail)
                .username(username)
                .build();
        return loginUser;
    }

}
