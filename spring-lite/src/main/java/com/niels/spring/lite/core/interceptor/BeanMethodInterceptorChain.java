package com.niels.spring.lite.core.interceptor;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Wang
 * @date 2018/1/30
 */
public class BeanMethodInterceptorChain {
    protected List<BeanMethodInterceptor> interceptorList = new ArrayList<>();
    private ThreadLocal<Integer> index = new ThreadLocal<>();
    private ThreadLocal<MethodProxy> methodProxyThreadLocal = new ThreadLocal<>();

    protected void add(BeanMethodInterceptor filter) {
        interceptorList.add(filter);
    }

    public Object startFilter(Annotation ann, Object obj, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        methodProxyThreadLocal.set(methodProxy);
        index.set(-1);
        Object result = null;
        try {
            result = execute(ann, obj, method, params);
        } finally {
            index.remove();
            methodProxyThreadLocal.remove();
        }
        return result;
    }


    public Object execute(Annotation ann, Object obj, Method method, Object[] params) throws Throwable {
        index.set(1 + index.get());
        if (index.get() == interceptorList.size()) {
            return methodProxyThreadLocal.get().invokeSuper(obj, params);
        }
        BeanMethodInterceptor interceptor = interceptorList.get(index.get());
        return interceptor.intercept(ann, obj, method, params, this);
    }

}
