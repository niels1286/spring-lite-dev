package com.niels;

import com.niels.spring.lite.core.SpringLiteContext;

/**
 * @author Niels
 * @date 2018/1/30
 */
public class MainTest {
    public static void main(String[] args) {
        SpringLiteContext.init("com.niels");
        App app = null;
        try {
            app = SpringLiteContext.getBean(App.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        app.doit();
    }
}
