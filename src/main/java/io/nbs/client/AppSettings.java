package io.nbs.client;

import com.nbs.ipfs.IpfsCnst;
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
    
    public String remove(String key){
        if(StringUtils.isBlank(key)|| !SETTINGS.containsKey(key))return null;
        String res = SETTINGS.get(key);
        SETTINGS.remove(key);
        return res;
    }
    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/18
     * @Description  :
     * 
     */
    public String getValue(String key,String defaultVal){
        return SETTINGS.getOrDefault(key,defaultVal);
    }

    public AppSettings clear(){
        SETTINGS.clear();
        return this;
    }

    public static ConcurrentHashMap<String, String> getSttings() {
        return SETTINGS;
    }

    public AppSettings loadExtProperties(Properties props){
        if(props==null||props.isEmpty())return this;
        Enumeration enumeration = props.propertyNames();
        return this;
    }

    public static String getProperty(String key){
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

}
