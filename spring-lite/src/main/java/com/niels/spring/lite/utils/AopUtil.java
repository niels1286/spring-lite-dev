package com.niels.spring.lite.utils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author: Niels Wang
 * @date: 2018/1/30
 */
public class AopUtil {

    public static final <T> T createProxy(Class<T> clazz, MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(interceptor);
        return (T) enhancer.create();
    }
}
