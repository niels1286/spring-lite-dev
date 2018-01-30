package com.niels.annotation;

import java.lang.annotation.*;

/**
 * @author Niels Wang
 * @date 2018/1/30
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Automired {
    String value() default "";
}
