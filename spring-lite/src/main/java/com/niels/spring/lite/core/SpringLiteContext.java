package com.niels.spring.lite.core;

import com.niels.annotation.Automired;
import com.niels.annotation.Component;
import com.niels.annotation.Interceptor;
import com.niels.spring.lite.core.interceptor.BeanMethodInterceptor;
import com.niels.spring.lite.core.interceptor.BeanMethodInterceptorManager;
import com.niels.spring.lite.utils.AopUtil;
import com.niels.spring.lite.utils.ScanUtil;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Niels Wang
 * @date 2018/1/30
 */
public class SpringLiteContext {

    private static final Map<String, Object> BEAN_MAP = new HashMap<>();
    private static final Map<String, Class> BEAN_TYPE_MAP = new HashMap<>();
    private static final Map<Class, Set<String>> CLASS_NAME_SET_MAP = new HashMap<>();

    private static MethodInterceptor interceptor;

    public static void init(final String packName) {
        init(packName, new DefaultMethodInterceptor());
    }

    public static void init(final String packName, MethodInterceptor interceptor) {
        SpringLiteContext.interceptor = interceptor;
        List<Class> list = ScanUtil.scan(packName);
        list.forEach((Class clazz) -> checkBeanClass(clazz));
        try {
            for (String key : BEAN_MAP.keySet()) {
                injectionBeanFields(BEAN_MAP.get(key), BEAN_TYPE_MAP.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void injectionBeanFields(Object obj, Class objType) throws Exception {
        Set<Field> fieldSet = getFieldSet(objType);
        for (Field field : fieldSet) {
            injectionBeanField(obj, field);
        }

    }

    private static Set<Field> getFieldSet(Class objType) {
        Set<Field> set = new HashSet<>();
        Field[] fields = objType.getDeclaredFields();
        for (Field field : fields) {
            set.add(field);
        }
        if (!objType.getSuperclass().equals(Object.class)) {
            set.addAll(getFieldSet(objType.getSuperclass()));
        }
        return set;
    }

    private static void injectionBeanField(Object obj, Field field) throws Exception {
        Annotation[] anns = field.getDeclaredAnnotations();
        if (anns == null || anns.length == 0) {
            return;
        }
        Annotation automired = getFromArray(anns, Automired.class);
        if (null == automired) {
            return;
        }
        String name = ((Automired) automired).value();
        Object value = null;
        if (null == name || name.trim().length() == 0) {
            Set<String> nameSet = CLASS_NAME_SET_MAP.get(field.getType());
            if (nameSet == null || nameSet.isEmpty()) {
                throw new Exception("Can't find the bean named:" + name);
            } else if (nameSet.size() == 1) {
                name = nameSet.iterator().next();
            } else {
                name = field.getName();
            }
        }
        value = BEAN_MAP.get(name);
        if (null == value) {
            throw new Exception("Can't find the bean named:" + name);
        }
        field.setAccessible(true);
        field.set(obj, value);
        field.setAccessible(false);
    }

    private static void checkBeanClass(Class clazz) {
        Annotation[] anns = clazz.getDeclaredAnnotations();
        if (anns == null || anns.length == 0) {
            return;
        }
        Annotation ann = getFromArray(anns, Component.class);
        String beanName = null;
        if (ann != null) {
            beanName = ((Component) ann).value();
            if (beanName == null || beanName.trim().length() == 0) {
                String start = clazz.getSimpleName().substring(0, 1).toLowerCase();
                String end = clazz.getSimpleName().substring(1);
                beanName = start + end;
            }
            loadBean(beanName, clazz);
        }
        Annotation interceptorAnn = getFromArray(anns, Interceptor.class);
        if (null != interceptorAnn) {
            BeanMethodInterceptor interceptor = null;
            if (null != beanName) {
                interceptor = (BeanMethodInterceptor) SpringLiteContext.getBean(beanName);
            } else {
                try {
                    Constructor constructor = clazz.getDeclaredConstructor();
                    interceptor = (BeanMethodInterceptor) constructor.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            BeanMethodInterceptorManager.addBeanMethodInterceptor(((Interceptor) interceptorAnn).value(), interceptor);
        }
    }

    private static Annotation getFromArray(Annotation[] anns, Class clazz) {
        for (Annotation ann : anns) {
            if (ann.annotationType().equals(clazz)) {
                return ann;
            }
        }
        return null;
    }

    private static void loadBean(String beanName, Class clazz) {
        if (BEAN_MAP.containsKey(beanName)) {
            System.out.println("bean name repetition (" + beanName + "):" + clazz.getName());
            return;
        }
        Object bean = AopUtil.createProxy(clazz, interceptor);
        BEAN_MAP.put(beanName, bean);
        BEAN_TYPE_MAP.put(beanName, clazz);
        addClassNameMap(clazz, beanName);
    }

    private static void addClassNameMap(Class clazz, String beanName) {
        Set<String> nameSet = CLASS_NAME_SET_MAP.get(clazz);
        if (null == nameSet) {
            nameSet = new HashSet<>();
        }
        nameSet.add(beanName);
        CLASS_NAME_SET_MAP.put(clazz, nameSet);
        if (null != clazz.getSuperclass() && !clazz.getSuperclass().equals(Object.class)) {
            addClassNameMap(clazz.getSuperclass(), beanName);
        } else if (clazz.getInterfaces() != null && clazz.getInterfaces().length > 0) {
            for (Class intfClass : clazz.getInterfaces()) {
                addClassNameMap(intfClass, beanName);
            }
        }
    }

    public static Object getBean(String beanName) {
        return BEAN_MAP.get(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) throws Exception {
        Set<String> nameSet = CLASS_NAME_SET_MAP.get(beanClass);
        if (null == nameSet || nameSet.isEmpty()) {
            throw new Exception("Can't find bean of " + beanClass.getName());
        }
        if (nameSet.size() > 1) {
            throw new Exception("There are " + nameSet.size() + " beans of " + beanClass.getName());
        }
        for (String beanName : nameSet) {
            return (T) BEAN_MAP.get(beanName);
        }
        return null;
    }

}
