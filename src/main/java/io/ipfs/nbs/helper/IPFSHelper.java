package io.ipfs.nbs.helper;

import io.ipfs.api.exceptions.FileFormatUnSupportException;
import io.ipfs.api.exceptions.IllegalFormatException;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;
import io.nbs.client.Launcher;
import io.nbs.commons.types.FileType;
import io.nbs.commons.utils.Base64CodecUtil;
import io.ipfs.api.IPFS;
import io.nbs.sdk.constants.ConfigKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Package : com.nbs.ipfs
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/6/19-11:47
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class IPFSHelper {

    private static Logger logger = LoggerFactory.getLogger(IPFSHolder.class);
    /**
     *
     */
    private ConcurrentHashMap secMap = new ConcurrentHashMap();
    public static String CLIENT_PEERID = null;

    private IPFS ipfs;

    /**
     *
     */
    public static final String NBSWORLD_CTRL_TOPIC = Base64CodecUtil.encode("$NBS.CTRL.J$");
    public static final String NBSWORLD_IMS_TOPIC = Base64CodecUtil.encode("nbsio.net");


    public IPFSHelper() {
        ipfs = new IPFS(Launcher.appSettings.getHost(),Launcher.appSettings.getApiPort());
        try {
            Map m = ipfs.id();
            secMap.putAll(m);
            CLIENT_PEERID = m.get("ID") == null ? "" : m.get("ID").toString();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    private static class IPFSHolder{
        static final IPFSHelper instance = new IPFSHelper();
    }


    public static IPFSHelper getInstance(){
        return IPFSHolder.instance;
    }


    public IPFS getIpfs() {
        return ipfs;
    }


    /**
     *
     * @param nick
     * @return
     * @throws IOException
     */
    public String updateNick(String nick) throws IOException {
        if(ipfs==null||StringUtils.isBlank(nick))return null;
        nick = nick.trim();
        ipfs.config.set(ConfigKeys.nickname.key(),nick);
        return nick;
    }


    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/25
     * @Description  :
     *
     */
    public Multihash fromHash58(String hash58) throws IllegalFormatException {
        if(StringUtils.isBlank(hash58))throw new IllegalFormatException("input hash58 string error.");
        try{
            return  Multihash.fromBase58(hash58);
        }catch (RuntimeException re){
            throw new IllegalFormatException("input hash58 string error.");
        }
    }


    public FileType getTypeFromHash(String hash) throws FileFormatUnSupportException {
        try{
            Multihash multihash = fromHash58(hash);
            byte[] data = ipfs.get(multihash);

            String tem = new String(data,0,28,"utf-8");
            logger.info(tem);
            return FileType.forValue(convert2String(data));
        }catch (IllegalFormatException ife){
            throw new FileFormatUnSupportException(ife.getMessage(),ife.getCause());
        }catch (IOException ioe){
            throw new FileFormatUnSupportException(ioe.getMessage(),ioe.getCause());
        }catch (RuntimeException r){
            throw new FileFormatUnSupportException(r.getMessage(),r.getCause());
        }
    }


    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/25
     * @Description  :
     *
     */
    private String convert2String(byte[] data){
        StringBuilder builder = new StringBuilder();
        if(data == null || data.length<=27)return null;
        for(int i = 0;i < 28 ;i++){
            int v = data[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if(hv.length() < 2){
                builder.append(0);
            }
            builder.append(hv);
        }
        logger.info("DATA:{}",builder.toString().toUpperCase());
        return builder.toString();
    }
}
