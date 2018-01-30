package com.niels.spring.lite.core;

import com.niels.spring.lite.core.interceptor.BeanMethodInterceptorManager;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author: Niels Wang
 * @date: 2018/1/30
 */
class DefaultMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        if (null == method.getDeclaredAnnotations() || method.getDeclaredAnnotations().length == 0) {
            return methodProxy.invokeSuper(obj, params);
        }
        return BeanMethodInterceptorManager.doFilter(method.getDeclaredAnnotations(), obj, method, params,methodProxy);
    }
}
