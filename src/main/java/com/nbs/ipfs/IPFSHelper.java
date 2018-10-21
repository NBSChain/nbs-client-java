package com.nbs.ipfs;

import io.nbs.client.Launcher;
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



}
