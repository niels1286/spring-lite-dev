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
public class MultipleBeanMethodInterceptorChain extends BeanMethodInterceptorChain {
    protected List<Annotation> annotationList = new ArrayList<>();
    protected Integer index = -1;
    protected MethodProxy methodProxy;


    public MultipleBeanMethodInterceptorChain(List<Annotation> annotations, List<BeanMethodInterceptorChain> chainList) {
        if (null == annotations || annotations.isEmpty()) {
            return;
        }
        for (int i = 0; i < annotations.size(); i++) {
            fillInterceptorList(annotations.get(i), chainList.get(i));
        }
    }

    private void fillInterceptorList(Annotation annotation, BeanMethodInterceptorChain beanMethodInterceptorChain) {
        for (BeanMethodInterceptor interceptor : beanMethodInterceptorChain.interceptorList) {
            annotationList.add(annotation);
            interceptorList.add(interceptor);
        }
    }

    @Override
    public Object startFilter(Annotation ann, Object obj, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        this.methodProxy = methodProxy;
        index = -1;
        Object result;
        try {
            result = execute(null, obj, method, params);
        } finally {
            index = -1;
            this.methodProxy = null;
        }
        return result;
    }

    @Override
    public Object execute(Annotation ann, Object obj, Method method, Object[] params) throws Throwable {
        index += 1;
        if (index == interceptorList.size()) {
            return methodProxy.invokeSuper(obj, params);
        }
        ann = annotationList.get(index);
        BeanMethodInterceptor interceptor = interceptorList.get(index);
        return interceptor.intercept(ann, obj, method, params, this);
    }
}
