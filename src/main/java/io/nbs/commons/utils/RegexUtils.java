package io.nbs.commons.utils;

import java.util.regex.Pattern;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project ipfs-mm
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/16
 */
public class RegexUtils {

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/16
     * @Description  : 检查IP
     *
     */
    public static boolean checkIPv4Address(String ipv4){
        if(ipv4 == null)return false;
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
        return Pattern.matches(regex,ipv4);
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/16
     * @Description  :
     * 端口验证 80~65535 之间（含边界）
     */
    public static boolean checkPort(String port){
        if(port == null)return false;
        String regex = "^[1-9]\\d*|0$ ";
        boolean b = Pattern.matches(regex,port);
        if(!b)return false;
        if(Integer.parseInt(port) <80 || Integer.parseInt(port) > 65535)return false;
        return true;
    }
}
