package io.nbs.client;

import io.ipfs.api.IPFS;
import io.ipfs.api.JSONParser;
import io.ipfs.api.exceptions.IPFSInitialException;
import io.ipfs.api.exceptions.IllegalIPFSMessageException;
import io.ipfs.nbs.helper.IPAddressHelper;
import io.nbs.client.cnsts.AppGlobalCnst;
import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.OSUtil;
import io.nbs.client.exceptions.AppInitializedException;
import io.nbs.client.ui.ScreenSize;
import io.nbs.client.ui.frames.*;
import io.nbs.client.ui.panels.media.frames.MediaBrowserFrame;
import io.nbs.commons.helper.RadomCharactersHelper;
import io.nbs.commons.utils.Base64CodecUtil;
import io.nbs.sdk.beans.NodeBase;
import io.nbs.sdk.beans.PeerInfo;
import io.nbs.commons.utils.DataBaseUtil;
import io.nbs.commons.utils.IconUtil;
import io.nbs.sdk.constants.ConfigKeys;
import io.nbs.sdk.prot.IPMParser;
import io.nbs.sdk.prot.NodeDataConvertHelper;
import javafx.application.Application;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Package : io.ipfs.app
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/6/29-14:01
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class Launcher {

    private Logger logger = LoggerFactory.getLogger(Launcher.class);
    private static Launcher context;
    private static SqlSession sqlSession;
    private static final String APP_VERSION = "2.0";

    public static ImageIcon logo ;
    private static ProcessBuilder ipfsBuilder;
    private static Process ipfsProcess;

    private ImageIcon loading;
    private ImageIcon settingsIcon;
    /**
     * 文件基础路径
     * ${basedir}/.nbs/
     * .nbs/download/cache
     * files
     * music
     * videos
     * profiles
     */
    public static String appBasePath;
    /**
     *
     */
    public static String userHome;
    public static final String CURRENT_DIR;
    public static final String FILE_SEPARATOR;
    public static String DOWNLOAD_FILE_PATH;
    private static boolean ipfsRuning = false;
    private static boolean cliStartFirst = true;
    public static AppSettings appSettings;

    public static final File temDir;

    private IPFS ipfs;
    /**
     * 当前Frame
     */
    private JFrame currentFrame;

    public static PeerInfo currentPeer;
    private LoadingFrame loadingFrame;

    private Dimension screenDimension;
    private ScreenSize currentScreenSize;

    static {
        sqlSession = DataBaseUtil.getSqlSession();
        CURRENT_DIR = System.getProperty("user.dir");
        FILE_SEPARATOR = System.getProperty("file.separator");
        temDir = new File(CURRENT_DIR+FILE_SEPARATOR+".tmp");
    }

    public Launcher(){
        this(null);
    }
    public Launcher(String[] args){
        context = this;
        loading = IconUtil.getIcon(this,"/icons/loading.gif");
        settingsIcon = IconUtil.getIcon(this,"/icons/settings.gif");
        logo = IconUtil.getIcon(this,"/icons/nbs.png");
        currentPeer = new PeerInfo();
        appSettings = AppSettings.getInstance(args);
    }


    public void launch(){
        screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.currentScreenSize = ScreenSize.convertSize(screenDimension.width,screenDimension.height);
        loadingFrame = new LoadingFrame(settingsIcon);
        loadingFrame.setVisible(true);
        loadingFrame.setIconImage(logo.getImage());

        /**
         * 1.初始化目录
         */
        initialStartup();

        boolean bootstrapOk = false;
        String initMessage = "";
        try {
            bootstrapOk = appSettings.checkedBaseIPFSConfig();
        }catch (AppInitializedException aie){
            logger.warn(aie.getMessage());
        }

        /**
         * 2.构建IPFS
         */
        try{
            ipfs = new IPFS(appSettings.getHost(),appSettings.getApiPort());
            //构建CurrentPeer
            buildPeerInfo(currentPeer,ipfs);
            //bootstrapOk = true;
            try{
                String enfromid = fillFromid(ipfs);
                setEnFromid(enfromid);
            }catch (IPFSInitialException iie){
                //goto Fail
                goFailFrame(appSettings.getConfigVolme("nbs.ipfs.pubsub.failure.msg","ipfs pubsub service startup fail."));
            }
            currentFrame = new MainFrame(currentPeer);

            hideLoadFrame();
        }catch (RuntimeException re){
            logger.warn("初始化IPFS 失败{}",re.getMessage());
            hideLoadFrame();
            currentFrame = new InitialDappFrame("connected failure. host :"+ appSettings.getHost());
        }catch (IOException ioe){
            logger.warn("初始化Peer 失败 {}",ioe.getMessage());
            hideLoadFrame();
            currentFrame = new InitialDappFrame("connected failure. host :"+ appSettings.getHost());
        }

        currentFrame.setBackground(ColorCnst.WINDOW_BACKGROUND);
        currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if(OSUtil.getOsType()!=OSUtil.Mac_OS){
            //currentFrame.setIconImage(logo.getImage());
        }
        currentFrame.setIconImage(logo.getImage());
        currentFrame.setVisible(true);
    }

    private void goFailFrame(String msg){
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(msg);
        sb.append("</html");
        currentFrame = new FailFrame(sb.toString());
    }

    private void buildPeerInfo(PeerInfo info,IPFS ipfs) throws IOException{
        if(info==null)info = new PeerInfo();
        Map data = ipfs.id();
        NodeBase nodeBase = NodeDataConvertHelper.convertFormID(data);
        if(nodeBase==null)throw new IOException("获取PeerID 失败.");
        info.setId(nodeBase.getID());
        Map cfgMap = ipfs.config.show();
        if(cfgMap.containsKey(ConfigKeys.nickname.key()))
            info.setNick(cfgMap.get(ConfigKeys.nickname.key()).toString());
        if(cfgMap.containsKey(ConfigKeys.avatarHash.key()))
            info.setAvatar(cfgMap.get(ConfigKeys.avatarHash.key()).toString());
        if(cfgMap.containsKey(ConfigKeys.avatarName.key()))
            info.setAvatarName(cfgMap.get(ConfigKeys.avatarName.key()).toString());
        if(cfgMap.containsKey(ConfigKeys.avatarSuffix.key()))
            info.setAvatarSuffix(cfgMap.get(ConfigKeys.avatarSuffix.key()).toString());

    }

    private void hideLoadFrame(){
        if(loadingFrame!=null){
            loadingFrame.setVisible(false);
            loadingFrame.dispose();
        }
    }

    /**
     *
     */
    private void checkedIPFSRunning() throws Exception{
        int checkTimes = 0;
        while (!ipfsRuning&& checkTimes<5){
            if(ipfs==null){
                String apiURL;
                apiURL = appSettings.getAddressApiUrl();
                try {
                    ipfs =  new IPFS(apiURL);
                }catch (RuntimeException e){
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    checkTimes++;
                    continue;
                }
            }
            try {
                ipfs.id();
                ipfsRuning = true;
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            System.out.println("NBS 服务启动检查...{"+checkTimes+"}.");
            checkTimes++;
        }
    }

    public void reStartMain(){
        //init IPFS and check

        boolean first = false;
        try {
            checkedIPFSRunning();
            first = needInitConfig(ipfs);
            //first = true;
            if(first){
                currentFrame = new InitialFrame(ipfs);
            }else {
                currentFrame = new MainFrame(currentPeer);
                currentFrame.setVisible(true);
            }
            currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            currentFrame.setBackground(ColorCnst.WINDOW_BACKGROUND);
            if(OSUtil.getOsType()!=OSUtil.Mac_OS){
                currentFrame.setIconImage(logo.getImage());
            }
            currentFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            destoryIPFS();
            System.exit(1);
        }
    }



    /**
     *
     * @param ipfs
     * @return
     * @throws IOException
     */
    private boolean needInitConfig(IPFS ipfs) throws IOException {
        Map cfg = ipfs.config.show();
        String peerid = (String)ipfs.id().get("ID");
        if(appSettings.containsKey(ConfigKeys.nickname.key())
                && appSettings.containsKey(ConfigKeys.formid.key())){
            Object nickObj = appSettings.getConfigVolme(ConfigKeys.nickname.key());
            String nick = IPMParser.urlDecode(nickObj.toString());
            String fromid =  (String)cfg.get(ConfigKeys.formid.key());
            if(StringUtils.isBlank(fromid)||StringUtils.isBlank(nick))return true;
            currentPeer = new PeerInfo();
            currentPeer.setId(peerid);
            currentPeer.setNick(nick);
            //
            currentPeer.setFrom(fromid);

            Object avatar = appSettings.getConfigVolme(ConfigKeys.avatarHash.key());
            Object avatarSuffix = appSettings.getConfigVolme(ConfigKeys.avatarSuffix.key());
            if(avatar!=null&&!avatar.toString().equals("")
                    &&avatarSuffix!=null&& !"".equals(avatarSuffix.toString())){
                currentPeer.setAvatar(avatar.toString());
                currentPeer.setAvatarSuffix(avatarSuffix.toString());
            }
            Object avatarName = appSettings.getConfigVolme(ConfigKeys.avatarName.key());
            if(avatarName!=null){
                String avatarFileName = IPMParser.urlDecode(avatarName.toString());
                currentPeer.setAvatarName(avatarFileName);
            }
            //setIP
            new Thread(()->{
                String ip = IPAddressHelper.getInstance().getRealIP();
                if(ip!=null&&!"".equals(ip)){
                    currentPeer.setIp(ip);
                    String locations = IPAddressHelper.getInstance().getLocations(ip);
                    if(StringUtils.isNotBlank(locations))currentPeer.setLocations(locations);
                }
            }).start();
            return false;
        }else {
            return true;
        }
    }



    /**
     * 启动初始化
     */
    private void initialStartup(){
        userHome = System.getProperty("user.home");
        appBasePath = CURRENT_DIR+FILE_SEPARATOR+AppGlobalCnst.NBS_ROOT;
        if(!temDir.exists()){//临时文件
            temDir.mkdirs();
        }
        /**
         * 初始化目录
         */
        DOWNLOAD_FILE_PATH = AppGlobalCnst.consturactPath(userHome,AppGlobalCnst.NBS_ROOT);
        File userFile = new File(DOWNLOAD_FILE_PATH);
        if(!userFile.exists()){
            userFile.mkdirs();
        }
        File appBaseFile = new File(appBasePath);
        if(!appBaseFile.exists()){
            appBaseFile.mkdirs();
        }

        if(!appBaseFile.exists()){
            appBaseFile.mkdirs();
        }
        //数据库建表初始化

    }

    /**
     *
     * @return
     */
    public static Launcher getContext() {
        return context;
    }

    public IPFS getIpfs() {
        if(ipfs ==null){
            try{
                ipfs =  new IPFS(appSettings.getHost(),appSettings.getApiPort());
            }catch (RuntimeException e){
                logger.error("未能链接上IPFS服务，请检查服务是否已停止.");
            }
        }
        return ipfs;
    }

    public static SqlSession getSqlSession() {
        return sqlSession;
    }

    /**
     *
     * @return
     */
    public JFrame getCurrentFrame() {
        return currentFrame;
    }

    /**
     *
     * @return
     */
    public boolean startIPFS() throws IllegalIPFSMessageException {
        if(ipfsBuilder==null){
            //ipfs daemon --routing=dhtclient --enable-pubsub-experiment

            String ipfsExe = AppGlobalCnst.consturactPath(CURRENT_DIR,AppGlobalCnst.IPFS_BASE);
            File ipfsPath = new File(ipfsExe);
            File exeFile = new File(ipfsPath,"ipfs.exe");
            if(!exeFile.exists()){
                logger.error("IPFS SEVER NOT FOUND IN PATH :{}",ipfsExe);
                throw new IllegalIPFSMessageException("没有在["+ipfsExe+"]下找到服务文件.");
            }
            ipfsBuilder = new ProcessBuilder("ipfs.exe","daemon" ,"--routing=dhtclient","--enable-pubsub-experiment");
            ipfsBuilder.directory(ipfsPath);
            try {
                ipfsProcess = ipfsBuilder.start();
                return ipfsProcess.waitFor(2,TimeUnit.SECONDS);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public boolean initNBSSvr() throws IllegalIPFSMessageException {
        String ipfsExe = AppGlobalCnst.consturactPath(CURRENT_DIR,AppGlobalCnst.IPFS_BASE);
        File ipfsPath = new File(ipfsExe);
        File exeFile = new File(ipfsPath,"ipfs.exe");
        if(!exeFile.exists()){
            logger.error("IPFS SEVER NOT FOUND IN PATH :{}",ipfsExe);
            throw new IllegalIPFSMessageException("没有在["+ipfsExe+"]下找到服务文件.");
        }
        ProcessBuilder initNBSBuilder =  new ProcessBuilder("ipfs.exe","init" ,"-p local-discovery");
        initNBSBuilder.directory(ipfsPath);
        try {
            Process initNBSProcess = initNBSBuilder.start();
            initNBSProcess.waitFor(2,TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalIPFSMessageException("初始化失败.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IllegalIPFSMessageException("初始化失败.");
        }
        return true;
    }

    /**
     * 退出时同时结束IPFS服务
     */
    public static void destoryIPFS(){
        if(ipfsProcess!=null && appSettings.getStatus("nbs.server.exit.stop")){
            ipfsProcess.destroy();
        }
    }

    public static boolean isIpfsRuning() {
        return ipfsRuning;
    }

    public static String getSysUser(){
        return System.getProperty("user.name","");
    }

    public void setIpfs(IPFS ipfs) {
        this.ipfs = ipfs;
    }

    public PeerInfo getCurrentPeer() {
        return currentPeer;
    }

    public static void setCurrentPeer(PeerInfo currentPeer) {
        Launcher.currentPeer = currentPeer;
    }

    public void setCurrentFrame(JFrame currentFrame) {
        this.currentFrame = currentFrame;
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/22
     * @Description  :
     * 填充ipfs pubsub enfromid
     */
    public String  fillFromid(IPFS ipfs) throws IPFSInitialException {
        if(!appSettings.subWorldPeers())return null;//未启用聊天模式
        if(ipfs==null)throw new IPFSInitialException("IPFS 服务连接失败.");
        if(currentPeer==null||currentPeer.getId()==null)throw new IPFSInitialException("请先设置IPFS Peer 信息");
        int times = Launcher.appSettings.tryGetFromidTimes();
        AtomicInteger counter = new AtomicInteger(times);
        return cycleGetFromid(ipfs,counter);
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/11/5
     * @Description  :
     * return enFromid
     */
    private String cycleGetFromid(IPFS ipfs,AtomicInteger counter) throws IPFSInitialException {
        if(counter==null)return null;
        if(counter.get()<= 0 )throw new IPFSInitialException("Please check IPFS pubsub enabled.");
        try{
            String tmpTopic = RadomCharactersHelper.getInstance().generated(currentPeer.getId(),4);
            Stream<Map<String,Object>> subs = ipfs.pubsub.sub(tmpTopic);
            ipfs.pubsub.pub(tmpTopic,currentPeer.getId());
            ipfs.pubsub.pub(tmpTopic,currentPeer.getId());
            List<Map<String, Object>> lst = subs.limit(1).collect(Collectors.toList());
            Object fromidObj = JSONParser.getValue(lst.get(0),"from");
            logger.info(fromidObj.toString());
            if(fromidObj!=null){
                String fromidStr = fromidObj.toString();
                String enfromid = Base64CodecUtil.encode(fromidStr);
                logger.info("fromid encode compare and set : {} -- {}",fromidStr,enfromid);
                ipfs.config.set(ConfigKeys.formid.key(),enfromid);
                return enfromid;
            }else {
                return cycleGetFromid(ipfs,counter);
            }
        }catch (Exception exception){
            try{
                TimeUnit.SECONDS.sleep(2);
            }catch (InterruptedException ie){
            }
            counter.set(counter.get()-1);
            return cycleGetFromid(ipfs,counter);
        }
    }


    public ImageIcon getLoading() {
        return loading;
    }

    public static ImageIcon getLogo() {
        return logo;
    }

    public ImageIcon getSettingsIcon() {
        return settingsIcon;
    }


    public ScreenSize getCurrentScreenSize() {
        return currentScreenSize;
    }

    public Dimension getScreenDimension() {
        return screenDimension;
    }


    public static PeerInfo setEnFromid(String enFromid){
        if(currentPeer==null)currentPeer = new PeerInfo();
        currentPeer.setFrom(enFromid);
        return currentPeer;
    }
}
