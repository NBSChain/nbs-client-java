package io.nbs.client.ui.panels.media;


import io.nbs.client.Launcher;
import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.ui.components.LCJlabel;
import io.nbs.client.ui.frames.MainFrame;
import io.nbs.client.ui.panels.TitlePanel;
import io.nbs.client.ui.panels.WinResizer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Package : io.nbs.client.ui.panels.media
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/7/13-14:53
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class MediaMasterPanel extends JPanel implements WinResizer {
    private static final Logger logger = LoggerFactory.getLogger(MediaMasterPanel.class);
    private static MediaMasterPanel context;
    //top
    private TitlePanel winTitlePanel;

    private JPanel centerPanel;
    private final JFXPanel webBrowser = new JFXPanel();
    private static Group root;
    private static WebView view;
    private static WebEngine engine;
    private JTextField searchHash;
    private MediaPlayer player;
    private PlayerThread playerThread;

    /**
     * construction
     */
    public MediaMasterPanel() {
        context = this;
        initComponents();
        initView();
        setListeners();
    }

    /**
     * [initComponents description]
     *
     * @return {[type]} [description]
     */
    private void initComponents() {
        this.winTitlePanel = new TitlePanel(this,this);
        winTitlePanel.setTitle(Launcher.appSettings.getConfigVolme("nbs.ui.panel.media.label","MultiMedia"));
        winTitlePanel.setBackground(ColorCnst.LIGHT_GRAY);
        //webBrowser.setBorder(ColorCnst.RED_BORDER);
        centerPanel = new JPanel();
    }

    /**
     * [initView description]
     *
     * @return {[type]} [description]
     */
    private void initView() {
        setLayout(new BorderLayout());
        /* ======================= 构造内部Start =====================*/
        centerPanel.setLayout(new BorderLayout());
        LCJlabel msgLabel = new LCJlabel("该功能尚未开放，尽请期待....",16);
        msgLabel.setForeground(ColorCnst.FONT_ABOUT_TITLE_BLUE);
        msgLabel.setHorizontalAlignment(JLabel.CENTER);

        centerPanel.add(msgLabel,BorderLayout.CENTER);
        /* ======================= 构造内部End =====================*/
        add(winTitlePanel,BorderLayout.NORTH);
        add(centerPanel,BorderLayout.CENTER);
    }

    private void setListeners() {

    }

    /**
     * [getContext description]
     *
     * @return {[type]} [description]
     */
    public static MediaMasterPanel getContext() {
        return context;
    }


    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/22
     * @Description  :
     * QmZpxzi13n2zHamyZoGBYRzQu5f2ZkCzTv8fDDajKtiop4
     * 疯狂假期BD国粤英三语中英双字.mp4
     */
    public void loadHash(String hash){
        hash = "QmRpbJe2MTyDKrUxsjXmgtYJPquVuZwoY6iqtD6Dh9TS5v";
        int web_width = this.centerPanel.getWidth();
        int web_height = this.centerPanel.getHeight();
        logger.info("w*h = {}*{}",web_height,web_height);
        String url;
        url = Launcher.appSettings.getGatewayURL(hash);
        if(playerThread == null || playerThread.notloaded(url)){
            playerThread = null;
            playerThread = new PlayerThread(url,new Integer(web_width).doubleValue(),new Integer(web_height).doubleValue());
            Platform.runLater(playerThread);
        }else {

        }
        //player = new MediaPlayer(url,new Integer(web_width).doubleValue(),new Integer(web_height).doubleValue());
        this.remove(centerPanel);
        this.add(webBrowser,BorderLayout.CENTER);
        this.updateUI();
    }

    @Override
    public void resize(double w, double h) {
        logger.info("media resize.....");
    }

    @Override
    public void resize() {
        Rectangle rect = MainFrame.getContext().getBounds();
        int cW = rect.width;
        int cH = rect.height;
        logger.info("media resize.....{}*{}",cW,cH);
        if(view!=null){
            view.setPrefSize(new Integer(cW).doubleValue(),new Integer(cH).doubleValue());
        }
    }

    public void destoryPlatform(){
        if(playerThread!=null){
            playerThread.interrupt();
        }
    }

    private class PlayerThread extends Thread {
        private AtomicBoolean ctrlPlayer = new AtomicBoolean(true);
        private String url ;
        private double width;
        private double height;
        public PlayerThread(String url,double w,double h){
            this.url = url;
            this.width =w;
            this.height = h;
        }
        @Override
        public void run() {
            super.run();
            if(!ctrlPlayer.get())return;
            root = new Group();
            Scene scene = new Scene(root, width,height);
            webBrowser.setScene(scene);
            webBrowser.setLayout(new BorderLayout());
            view = new WebView();
            view.setMinSize(width,height);
            view.setPrefSize(width,height);
            engine = view.getEngine();
            //http://47.52.172.234:8080/ipfs/Qmc4KoWZBR5937qzGP8hEWsfu5wZPPMRxC8jThf4bQ6k2D
            engine.load(url);
            root.getChildren().add(view);
        }

        public boolean notloaded(String url) {
            return !this.url.equalsIgnoreCase(url);
        }


    }

    private class MediaPlayer implements Runnable{
        private AtomicBoolean ctrlPlayer = new AtomicBoolean(true);
        private String url ;
        private double width;
        private double height;
        public MediaPlayer (String url,double w,double h){
            this.url = url;
            this.width =w;
            this.height = h;
        }
        @Override
        public void run() {
            if(!ctrlPlayer.get())return;
            root = new Group();
            Scene scene = new Scene(root, width,height);
            webBrowser.setScene(scene);
            webBrowser.setLayout(new BorderLayout());
            view = new WebView();
            view.setMinSize(width,height);
            view.setPrefSize(width,height);
            engine = view.getEngine();
            //http://47.52.172.234:8080/ipfs/Qmc4KoWZBR5937qzGP8hEWsfu5wZPPMRxC8jThf4bQ6k2D
            engine.load(url);
            root.getChildren().add(view);
            new Thread(()->{
                listener(root,view);
            }).start();
        }

        public void listener(Group root,WebView view){
            while (ctrlPlayer.get()){
                try{
                    TimeUnit.SECONDS.sleep(2);
                }catch (InterruptedException e){
                }
            }
            logger.info("destroy...");
            root.getChildren().remove(view);
        }

        public void stop() {
            this.ctrlPlayer.set(false);
        }
    }
}