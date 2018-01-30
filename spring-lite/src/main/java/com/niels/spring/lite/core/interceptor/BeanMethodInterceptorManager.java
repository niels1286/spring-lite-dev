package com.niels.spring.lite.core.interceptor;

import com.niels.spring.lite.core.SpringLiteContext;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niels Wang
 * @date 2018/1/30
 */
public class BeanMethodInterceptorManager {

    private static final Map<Class, BeanMethodInterceptorChain> FILTER_MAP = new HashMap<>();

    public static void addBeanMethodInterceptor(Class annotationType, BeanMethodInterceptor interceptor) {
        BeanMethodInterceptorChain interceptorChain = FILTER_MAP.get(annotationType);
        if (null == interceptorChain) {
            interceptorChain = new BeanMethodInterceptorChain();
        }
        interceptorChain.add(interceptor);
        FILTER_MAP.put(annotationType, interceptorChain);
    }

    public static Object doFilter(Annotation[] anns, Object obj, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        List<Annotation> annotations = new ArrayList<>();
        List<BeanMethodInterceptorChain> chainList = new ArrayList<>();
        for(Annotation ann:anns){
            BeanMethodInterceptorChain chain = FILTER_MAP.get(ann.annotationType());
            if(null!=chain){
                chainList.add(chain);
                annotations.add(ann );
            }
        }
        if(annotations.isEmpty()){
            return methodProxy.invokeSuper(obj,params);
        }
        MultipleBeanMethodInterceptorChain chain = new MultipleBeanMethodInterceptorChain(annotations,chainList);
        return chain.startFilter(null,obj,method,params,methodProxy);
    }
}
