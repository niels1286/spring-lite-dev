package com.niels.spring.lite.core.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author: Niels Wang
 * @date: 2018/1/30
 */
public interface BeanMethodInterceptor {
    Object intercept(Annotation annotation, Object object, Method method, Object[] params, BeanMethodInterceptorChain interceptorChain) throws Throwable;
}
