package io.nbs.client;

import com.nbs.ipfs.IpfsCnst;
import io.nbs.client.exceptions.AppInitializedException;
import io.nbs.commons.helper.DateHelper;
import io.nbs.commons.utils.RegexUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * App 设置
 * Author   : lanbery
 * Created  : 2018/10/18
 */
public class AppSettings {
    private static final String PROPS_FILE_PATH = "/conf/dapp-conf.properties";
    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");
    private static final Logger logger = LoggerFactory.getLogger(AppSettings.class);
    private static ConcurrentHashMap<String,String> SETTINGS = new ConcurrentHashMap<>();
    private static Properties props = new Properties();
    private String[] args;
    private AppSettings(){
        loadProps();

        initialI18nProps();

        Enumeration enumeration = props.propertyNames();
        while (enumeration.hasMoreElements()){
            String key = (String)enumeration.nextElement();
            String val = getProperty(key);
            if(val!=null){
                SETTINGS.put(key,val);
            }
        }

        loged(true);
    }

    private static class AppSettingsHolder{
        public static AppSettings instance = new AppSettings();
    }

    public AppSettings setArgs(String[] args) {
        this.args = args;
        if(args!=null&&args.length>0){
            for(String arg : args){
                if(arg.equals("--wrap-with-directory")||arg.equals("-w")
                        ||arg.equals("--wrap-with-directory=true")){
                    SETTINGS.put(IpfsCnst.WRAP_WITH_DIRECTORY_KEY,"true");
                }
            }
        }
        return this;
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/18
     * @Description  :
     * 将dapp-conf.properties 放置到
     */
    private void initialI18nProps(){
        String i18n = props.getProperty("i18n","zh-cn");
        Reader reader = null;
        InputStream is = null;
        Properties i18nProps = new Properties();
        try{
            String file = System.getProperty("user.dir") + "/conf/"+i18n+".properties";
            is = new BufferedInputStream(new FileInputStream(file));
            reader = new InputStreamReader(is,"utf-8");
            i18nProps.load(reader);
            Iterator<Map.Entry<Object, Object>> iterator = i18nProps.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = iterator.next();
                String k = entry.getKey().toString();
                String v = entry.getValue().toString();
                SETTINGS.put(k,v);
            }
        }catch (IOException e){
            logger.error("load i18n properties error!",e.getCause());
        }finally {
            if(null != reader){
                try{
                    if(is!=null)is.close();
                    reader.close();
                }catch (IOException ioe){
                    logger.error("reader close error!",ioe.getCause());
                }
            }
        }
    }



    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/15
     * @Description  :
     *
     */
    private static Properties loadProps(){
        Reader reader = null;
        InputStream is = null;
        try{
            String file = System.getProperty("user.dir") + PROPS_FILE_PATH;
            is = new BufferedInputStream(new FileInputStream(file));
            reader = new InputStreamReader(is,"utf-8");
            props.load(reader);
        }catch (IOException e){
            logger.error("load dapp properties error!",e.getCause());
        }finally {
            if(null != reader){
                try{
                    reader.close();
                    if(is!=null)is.close();
                }catch (IOException ioe){
                    logger.error("reader close error!",ioe.getCause());
                }
            }
        }
        return props;
    }
    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/18
     * @Description  :
     *
     */
    public static AppSettings getInstance(String[] args){
        return AppSettingsHolder.instance.setArgs(args);
    }

    public AppSettings put(String key,String value){
        if(StringUtils.isBlank(key)||StringUtils.isBlank(value))return this;
        SETTINGS.put(key,value);
        return this;
    }
    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * 
     */
    public AppSettings remove(String key){
        return remove(key,false);
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * 
     */
    public AppSettings remove(String key,boolean bothProps){
        if(StringUtils.isBlank(key)|| !SETTINGS.containsKey(key))return this;
        SETTINGS.remove(key);
        if(bothProps&&props.containsKey(key))props.remove(key);
        return this;
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/18
     * @Description  :
     * 
     */
    public String getConfigVolme(String key,String defaultVal){
        return SETTINGS.getOrDefault(key,defaultVal);
    }

    public String getConfigVolme(String key){
        return SETTINGS.get(key);
    }

    public AppSettings clear(){
        SETTINGS.clear();
        return this;
    }

    public static ConcurrentHashMap<String, String> getSttings() {
        return SETTINGS;
    }
    
    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * 
     */
    public AppSettings addConfig(String key,String value,boolean containProps){
        if(StringUtils.isBlank(key)||StringUtils.isBlank(value))return this;
        SETTINGS.put(key,value);
        if(containProps)props.setProperty(key,value);
        return this;
    }
    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * 
     */
    public AppSettings addConfig(String key,String value){
        return addConfig(key,value,false);
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * 
     */
    public AppSettings loadExtProperties(Properties props){
        if(props==null||props.isEmpty())return this;
        Enumeration enumeration = props.propertyNames();
        return this;
    }

    private static String getProperty(String key){
        if(!props.containsKey(key))return null;
        String vlaue = props.getProperty(key);
        Matcher matcher = PATTERN.matcher(vlaue);
        StringBuffer buf = new StringBuffer();
        while (matcher.find()){
            String matcherKey = matcher.group(1);
            String matcherValue = props.getProperty(matcherKey);
            if(matcherValue != null){
                matcher.appendReplacement(buf,matcherValue);
            }

        }
        matcher.appendTail(buf);
        return buf.toString();
    }

    private void loged(Boolean loged){
        if(loged==null||!loged.booleanValue())return;
        logger.info(">>>>>>>>>>>>>>>>>>>>>> show app settings begin.");
        Iterator<Map.Entry<String,String>> entries = SETTINGS.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry<String,String> entry = entries.next();
            String key = entry.getKey();
            String v = entry.getValue();
            logger.info("Settings : {}={}",key,v);
        }
        logger.info(">>>>>>>>>>>>>>>>>>>>>> show app settings end.");
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * 
     */
    public AppSettings saveProps()throws Exception{
        StringBuffer sb = new StringBuffer();
        sb.append(DateHelper.currentTime());
        sb.append("system User ：").append(System.getProperty("user.name")).append(" Update config.");
        writeProps(sb.toString());
        return this;
    }

    private void writeProps(String comment) throws Exception {
        String file = System.getProperty("user.dir")+ PROPS_FILE_PATH;
        OutputStream os = null;
        try{
            os = new FileOutputStream(file);
            props.store(os,comment);
        }catch (IOException e){
            logger.error("write props config file error. file="+file,e.getCause());
            throw new Exception("save properties file error.");
        }finally {
            try{
                if(os!=null){
                    os.close();
                }
            }catch (IOException ioe){
                logger.warn("write close error.",ioe.getCause());
            }
        }
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * http://47.52.172.234:5001/api/v0/version
     */
    public String getAddressApiUrl() throws AppInitializedException {
        String host,port;
        if(!SETTINGS.containsKey(IpfsCnst.MM_HOST_KEY))throw new AppInitializedException(
                getConfigVolme("app.bootstrap.warn.host.msg","not found host config.")
        );
        host = SETTINGS.get(IpfsCnst.MM_HOST_KEY);
        if(!RegexUtils.checkIPv4Address(host))throw new AppInitializedException(
                getConfigVolme("app.bootstrap.warn.host.msg","host config format error.")
        );
        if(!SETTINGS.containsKey(IpfsCnst.MM_API_PORT_KEY))throw new AppInitializedException(
                getConfigVolme("app.bootstrap.warn.api.port.msg","not found api port config.")
        );
        port = SETTINGS.get(IpfsCnst.MM_API_PORT_KEY);
        if(!RegexUtils.checkPort(port))throw new AppInitializedException(
                getConfigVolme("app.bootstrap.warn.api.port.msg","api port config format error.")
        );
        String protocol = getConfigVolme(IpfsCnst.MM_GATEWAY_PROTOCOL_KEY,"http");
        return spliceAddressApi(host,port,protocol);
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     *  http://47.52.172.234:8080/ipfs/{Qmhash}
     */
    public String getAddressGatewayBaseUrl() throws AppInitializedException {
        String host,port;
        if(!SETTINGS.containsKey(IpfsCnst.MM_HOST_KEY))throw new AppInitializedException(
                getConfigVolme("app.bootstrap.warn.host.msg","not found host config.")
        );
        host = SETTINGS.get(IpfsCnst.MM_HOST_KEY);
        if(!RegexUtils.checkIPv4Address(host))throw new AppInitializedException(
                getConfigVolme("app.bootstrap.warn.host.msg","host config format error.")
        );
        if(!SETTINGS.containsKey(IpfsCnst.MM_GATEWAY_PROTOCOL_KEY))throw new AppInitializedException(
                getConfigVolme("app.bootstrap.warn.gateway.port.msg","not found api port config.")
        );
        port = SETTINGS.get(IpfsCnst.MM_GATEWAY_PROTOCOL_KEY);
        if(!RegexUtils.checkPort(port))throw new AppInitializedException(
                getConfigVolme("app.bootstrap.warn.gateway.port.msg","api port config format error.")
        );
        String protocol = getConfigVolme(IpfsCnst.MM_GATEWAY_PROTOCOL_KEY,"http");
        return spliceGatewayUrl(host,port,protocol);
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * 获取Gateway URL http://47.52.172.234:8080/ipfs/{Qmhash}
     */
    public String getGatewayURL(String hash){
        try {
            String base = getAddressGatewayBaseUrl();
            return base + hash;
        }catch (AppInitializedException e){
            logger.warn(e.getMessage(),e.getCause());
            throw new RuntimeException(e.getMessage());
        }
    }

    private String spliceAddressApi(String host,String port,String portocol){
        StringBuffer baseBuf =new StringBuffer();
        baseBuf.append(portocol).append("://").append(host).append(":").append(port).append("/api/v0/");
        return baseBuf.toString();
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * http://47.52.172.234:8080/ipfs/Qmhash
     */
    private String spliceGatewayUrl(String host,String port,String portocol){
        StringBuffer baseBuf =new StringBuffer();
        baseBuf.append(portocol).append("://").append(host).append(":").append(port).append("/ipfs/");
        return baseBuf.toString();
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/19
     * @Description  :
     * 检查host ，api port ,gateway port
     */
    public boolean checkedBaseIPFSConfig() throws AppInitializedException {
        String host,apiPort,gatewayPort;
        if(!SETTINGS.containsKey(IpfsCnst.MM_HOST_KEY) || !RegexUtils.checkIPv4Address(SETTINGS.get(IpfsCnst.MM_HOST_KEY)))
            throw new AppInitializedException(
                    getConfigVolme("app.bootstrap.warn.host.msg","not found host config or error format.")
            );
        if(!SETTINGS.containsKey(IpfsCnst.MM_API_PORT_KEY) || !RegexUtils.checkPort(SETTINGS.get(IpfsCnst.MM_API_PORT_KEY)))
            throw new AppInitializedException(
                    getConfigVolme("app.bootstrap.warn.api.port.msg","not found api port config or error format.")
            );
        if(!SETTINGS.containsKey(IpfsCnst.MM_GATEWAY_PORT_KEY) || !RegexUtils.checkPort(SETTINGS.get(IpfsCnst.MM_GATEWAY_PORT_KEY)))
            throw new AppInitializedException(
                    getConfigVolme("app.bootstrap.warn.swarm.port.msg","not found api port config or error format.")
            );
        return true;
    }

    public String getHost(){
        return SETTINGS.get(IpfsCnst.MM_HOST_KEY);
    }

    public int getApiPort(){
        String apiPort = SETTINGS.get(IpfsCnst.MM_API_PORT_KEY);
        return Integer.parseInt(apiPort);
    }

    public int getGatewayPort(){
        String apiPort = SETTINGS.get(IpfsCnst.MM_GATEWAY_PORT_KEY);
        return Integer.parseInt(apiPort);
    }
}
