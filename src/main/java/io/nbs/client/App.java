package io.nbs.client;


import java.util.concurrent.ConcurrentHashMap;

/**
 * @Package : UI
 * @Description :
 * <p>
 *     NBS Chain Client For Java
 * </p>
 * @Author : lambor.c
 * @Date : 2018/6/29-13:56
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class App {
    private static ConcurrentHashMap<String,String> SETTINGS = new ConcurrentHashMap<>();
    public static void main(String[] agrs){
        Launcher launcher = new Launcher();
        launcher.launch();
    }
}
