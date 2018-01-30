package com.niels.annotation;

import java.lang.annotation.*;

/**
 * author: Niels Wang
 * date: 2018/1/30
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Interceptor {
    /**
     * the type of the Annotation on a method
     * @return
     */
    Class value() ;
}
