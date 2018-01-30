package com.niels;

import com.niels.annotation.Component;
import com.niels.annotation.Interceptor;
import com.niels.spring.lite.core.interceptor.BeanMethodInterceptor;
import com.niels.spring.lite.core.interceptor.BeanMethodInterceptorChain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author: Niels Wang
 * @date: 2018/1/30
 */
@Interceptor(Component.class)
public class TestInterceptor implements BeanMethodInterceptor {
    public Object intercept(Annotation annotation, Object object, Method method, Object[] params, BeanMethodInterceptorChain interceptorChain) throws Throwable {
        System.out.println("=======================sdf=================");
        return interceptorChain.execute(annotation,object,method,params);
    }
}
