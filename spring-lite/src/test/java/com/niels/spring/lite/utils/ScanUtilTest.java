package com.niels.spring.lite.utils;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Niels Wang
 * @date 2018/1/30
 */
public class ScanUtilTest {
    @Test
    public void scan() throws Exception {
        List<Class> classList = ScanUtil.scan("org.junit");
        classList.forEach((Class clazz) -> System.out.println(clazz));
        assertTrue(true);
    }

}