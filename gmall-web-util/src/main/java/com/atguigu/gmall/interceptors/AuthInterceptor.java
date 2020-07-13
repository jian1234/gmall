package com.atguigu.gmall.interceptors;

import com.atguigu.gmall.annotation.LoginRequired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handle){

        //拦截代码

        //判断被拦截的请求的访问方法的注解
        //反射
        HandlerMethod hm = (HandlerMethod) handle;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);
        if (methodAnnotation == null){
            return true;
        }
        System.out.println("进入拦截方法");
        return true;
    }
}
