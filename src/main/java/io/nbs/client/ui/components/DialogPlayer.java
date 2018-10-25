package io.nbs.client.ui.components;

import io.nbs.client.Launcher;
import io.nbs.client.ui.frames.MainFrame;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.management.ThreadMXBean;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/23
 */
public class DialogPlayer extends JDialog {
    private final static Logger logger = LoggerFactory.getLogger(DialogPlayer.class);

    private String name;
    private String hash;
    private Container container;
    private final JFXPanel webBrowser = new JFXPanel();
    private static int width = 0;
    private static int height = 0;
    private static PlayerRunable playerRunable;
    private JLabel button;



    public DialogPlayer (String hash,String name){
        super(MainFrame.getContext(),name,false);
        container = getContentPane();
        this.hash = hash;
        this.name = name;
        initComponets();
        setListeners();
    }

    private void initComponets(){
        this.setBounds(MainFrame.getContext().getBounds());
        width = getWidth();
        height = getHeight();
        container.setLayout(new BorderLayout());
        container.add(webBrowser,BorderLayout.CENTER);
        button = new JLabel("TEST");
        container.add(button,BorderLayout.SOUTH);
    }

    private void setListeners(){
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();

                Thread[] threads = new Thread[threadGroup.activeCount()];
                threadGroup.enumerate(threads,true);
                for(Thread thread : threads){
                    logger.info("thread name-id - state: {} - {}- {}",thread.getName(),thread.getId(),thread.getState().name());
                    if(thread.getName().startsWith("JFXMedia Player")||thread.getName().startsWith("JavaFX Application")){

                        logger.info("stop thread player : {}",thread.getName());
                        thread.interrupt();

                        Platform.exit();

                    }
                    if(thread.isAlive()){
                        logger.info(">>>>>>:{}-{}-{}",thread.getName(),thread.isAlive(),thread.getState().name());
                    }
                }
                logger.info(">>>>>>>>>.threadGroup: {}-activeNum:{}",threadGroup.getName(),threadGroup.activeGroupCount());
            }
        });
    }

    private boolean findPlayer(Thread thread){
        ThreadGroup tg = thread.getThreadGroup();
        Thread[] threads = new Thread[tg.activeGroupCount()];
        tg.enumerate(threads,true);
        return true;
    }

    public DialogPlayer load(String hash,String name){
        //if(this.hash.equals(hash))return this;
        this.hash = hash;
        this.name = name;
        String url = Launcher.appSettings.getGatewayURL(hash);
        playerRunable = new PlayerRunable(url);
        Platform.runLater(playerRunable);
        return this;
    }

    public DialogPlayer load(){
        this.load(this.hash,this.name);
        return this;
    }

    public DialogPlayer reload(){
        if(playerRunable!=null)playerRunable.relaod();
        return this;
    }

    public DialogPlayer stop(){
        if(playerRunable!=null){
            playerRunable.stopPlayer();
            Platform.setImplicitExit(true);
        }
        return this;
    }

    public class PlayerRunable implements Runnable{
        private final static String PLAYER_THREAD_NAME = "nbs-player";
//        private Group root;
//        private WebView view;
//        private WebEngine engine;
        private String url;
        private String preUrl;
        public PlayerRunable(String url){
            this.url = url;
        }
        @Override
        public void run() {
    /*        root = new Group();
            Scene scene = new Scene(root,width,height);
            webBrowser.setScene(scene);
            webBrowser.setLayout(new BorderLayout());
            view = new WebView();
            view.setMinSize(width,height);
            view.setPrefSize(width,height);
            engine = view.getEngine();
            engine.load("http://www.nbsio.net");
            preUrl = url;
            engine.load(url);
            root.getChildren().add(view);*/
            WebView webView = new WebView();
            webBrowser.setScene(new Scene(webView));
            webView.getEngine().load(url);
        }

        public boolean needCoverLoad(){
            return (preUrl==null || preUrl.equalsIgnoreCase(url)) ? false : true;
        }

        public void relaod(){
           // engine.reload();
        }

        public void stopPlayer(){
           // engine.load("http://www.nbsio.net");
        }

        public PlayerRunable resetUrl(String url){
            this.url = url;
            return this;
        }

        public String getSignName() {
            return PLAYER_THREAD_NAME;
        }
    }
}
