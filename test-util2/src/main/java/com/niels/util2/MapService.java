package com.niels.util2;

import com.niels.annotation.Component;

import javax.xml.ws.soap.Addressing;

/**
 * @author Niels Wang
 * @date 2018/1/30
 */
@Component
public class MapService {


    @Addressing
    public void doit() {

        System.out.println("map service do it");

    }
}
