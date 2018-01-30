package com.niels;

import com.niels.annotation.Automired;
import com.niels.annotation.Component;
import com.niels.util1.StringService;

/**
 * Hello world!
 */
@Component
public class App extends SuperApp{
    @Automired
    private StringService stringService;

    public void doit() {
        this.stringService.doit();
        this.mapService.doit();
    }
}
