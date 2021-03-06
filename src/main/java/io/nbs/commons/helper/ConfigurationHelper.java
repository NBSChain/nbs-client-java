package io.nbs.commons.helper;

import io.nbs.client.Launcher;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Package : io.ipfs.nbs.helper
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/6/29-14:24
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class ConfigurationHelper {
    /**
     * 外部配置文件目录名
     */
    private static final String CONF_ROOT = "conf";
    public static final String CURRENT_DIR = System.getProperty("user.dir");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String CONF_FILE = "nbs-conf.properties";
    private static final String I18N_FILE = "zh-cn.properties";

    public static final String PK_CFG_IPFS_ADDR = "nbs.server.address";
    private static final String IPFS_ADDR_DEFAULT = "/ip4/127.0.0.1/tcp/5001";


    /**
     * profiles
     */
    public static String JSON_NICKNAME_KEY = "nickname";
    public static String JSON_CFG_FROMID_KEY = "fromid";
    public static String JSON_AVATAR_KEY = "avatar";
    public static String JSON_AVATAR_NAME_KEY = "avatar-name";
    public static String JSON_AVATAR_SUFFIX_KEY = "suffix";


    private Properties cfgProps = new Properties();
    private Properties i18nProps = new Properties();
    private static int stats = 0;

    private ConfigurationHelper(){
        switch (stats){
            case 0:
                initLoadEnv();
                initLoadI18n();
                break;
            case 1:
                initLoadI18n();
                break;
            case 2:
                initLoadEnv();
                break;
        }
    }

    private static class ConfigHolder{
        public static ConfigurationHelper instance = new ConfigurationHelper();
    }

    /**
     *
     * @param key
     * @param defVal
     * @return
     */
    public String getI18nProperty(String key,String defVal){
        return i18nProps.getProperty(key,defVal);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getI18nProperty(String key){
        return i18nProps.getProperty(key);
    }
    /**
     *
     * @return
     */
    public static ConfigurationHelper getInstance(){
        ConfigurationHelper instance = ConfigHolder.instance;
        return instance;
    }

    /**
     *
     * @return
     */
    public int getHeartMonitorSleep(){
        String sec = cfgProps.getProperty("nbs.client.heart.monitor.seconds","300");
        return Integer.parseInt(sec);
    }


    /**
     * 获取IPFS服务地址
     * @return
     */
    public String getIPFSAddress(){
        if(cfgProps!=null){
            return cfgProps.getProperty(PK_CFG_IPFS_ADDR,IPFS_ADDR_DEFAULT);
        }else {
            return IPFS_ADDR_DEFAULT;
        }
    }

    /**
     *
     */
    private void initLoadEnv(){
        InputStream is = null;
        try{
            is = new BufferedInputStream(new FileInputStream(
                    CONF_ROOT +Launcher.FILE_SEPARATOR + CONF_FILE));
            cfgProps.load(is);
            is.close();
            if(stats==2){
                stats=3;
            }else {
                stats=1;
            }
        }catch (IOException ioe){
            System.out.println("load config error." + ioe.getMessage());
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void initLoadI18n(){
        InputStream is = null;
        try{
            is = new BufferedInputStream(new FileInputStream(
                    CONF_ROOT + Launcher.FILE_SEPARATOR + I18N_FILE));
            i18nProps.load(is);
            is.close();
            if(stats==1){
                stats=3;
            }else {
                stats=2;
            }
        }catch (IOException ioe){
            System.out.println("load i18n error."+ioe.getMessage());
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e){}
            }
        }
    }


    /**
     *
     * @return
     */
    public String getIPFSServerHost(){
        return cfgProps.getProperty("nbs.server.address.host","127.0.0.1");
    }

    /**
     * IPFS API 5001
     * @return
     */
    public String getIPFSApiPort(){
        return cfgProps.getProperty("nbs.server.address.api-port","5001");
    }

    public String getIPFSGatewayPort(){
        return cfgProps.getProperty("nbs.server.address.gateway-port","8080");
    }

    /**
     * 获取http IPFS URL
     * @return
     */
    public String getGateWayURL(){
        StringBuilder urlSb = new StringBuilder();
        urlSb.append("http://").append(getIPFSServerHost())
                .append(":").append(getIPFSGatewayPort());
        urlSb.append("/ipfs/");
        return urlSb.toString();
    }

    /**
     * 返回组装好的
     * @param hash
     * @return
     */
    public String getGateWayURL(String hash){
        String gwUrl = getGateWayURL();
        return gwUrl+hash;
    }

    public Properties getCfgProps() {
        return cfgProps;
    }

    public Properties getI18nProps() {
        return i18nProps;
    }

    /**
     * 控制显示会员
     * @return
     */
    public boolean subWorldPeers(){
        String stats = cfgProps.getProperty("nbs.client.im.topic.subworld","enabled");
        return(stats.equalsIgnoreCase("enabled")
                || stats.equalsIgnoreCase("true")
                || stats.equalsIgnoreCase("1")
        )  ? true : false;
    }

    public boolean exitStopIPFS(){
        String v = cfgProps.getProperty("nbs.server.exit.stop","true");
        return v.equalsIgnoreCase("true")||v.equals("1")||v.equalsIgnoreCase("y");
    }

    /**
     * 集成服务
     * @return
     */
    public boolean integratedServer(){
        String integrated = cfgProps.getProperty("nbs.server.integrated.enabled","disabled");
        return integrated.equalsIgnoreCase("true")||integrated.equals("1")||integrated.equalsIgnoreCase("y");
    }
}
