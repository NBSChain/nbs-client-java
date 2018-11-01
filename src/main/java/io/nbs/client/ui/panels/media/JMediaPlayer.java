package io.nbs.client.ui.panels.media;

import io.nbs.client.Launcher;
import io.nbs.client.cnsts.FontUtil;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/23
 */
public class JMediaPlayer extends JPanel {
    private final static Logger logger = LoggerFactory.getLogger(JMediaPlayer.class);
    private AtomicBoolean repeated = new AtomicBoolean(false);
    private final JFXPanel webBrowser = new JFXPanel();
    private static Group root;
    private static WebView view;
    private static WebEngine engine;
    private static JMediaPlayer context;

    private String currentHash;
    private PlayerThread player;
    private JLabel stopButton;

    public JMediaPlayer(){
        context = this;
        initComponents();
        initView();
        setListeners();
    }

    private void initComponents(){
        this.setLayout(new BorderLayout());
        stopButton = new JLabel("stop");
        stopButton.setFont(FontUtil.getDefaultFont(20));
        this.add(webBrowser,BorderLayout.CENTER);
        this.add(stopButton,BorderLayout.SOUTH);
    }

    private void initView(){

    }

    private void setListeners(){
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logger.info("stop video");
                if(engine!=null){
                    Platform.exit();
                    String scripts = "(function(window,document) {" +
                            "var videos = document.getElementsByTagName('video');" +
                            "   if(vedios&& vedios[0]){ " +
                            "       vedios[0].pause();" +
                            "   }" +
                            "})(window,document)";
                    engine.executeScript(scripts);

                }
            }
        });
    }

    private static Object addJQurey(final WebEngine engine,String minVersion,String jqueryLoction,String scripts){
        //TODO
        return null;
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/23
     * @Description  :
     * 
     */
    public JMediaPlayer loadHash(String hash){
        //hash = "QmULJfzUTHm6UJki5kdb1B8EhKpZwsoHDEV4ohPxnqxWzJ";
        if(currentHash!=null && currentHash.equals(hash)){
            repeated.set(true);
            //return context;
        }else {
            repeated.set(false);
        }
        currentHash = hash;
        String url;
        url = Launcher.appSettings.getGatewayURL(hash);
        int width = this.getWidth();
        int height = this.getHeight();
        if(player == null){
            player = new PlayerThread(url,new Integer(width).doubleValue(),new Integer(height).doubleValue());
            Platform.runLater(player);
            this.add(webBrowser,BorderLayout.CENTER);
            this.updateUI();
        }else if(player.needLoaded(url)){
            player.interrupt();
            player = new PlayerThread(url,new Integer(width).doubleValue(),new Integer(height).doubleValue());
            Platform.runLater(player);
            this.add(webBrowser,BorderLayout.CENTER);
            this.updateUI();
        }
        return context;
    }

    public JMediaPlayer reload(){
        if(player!=null && engine!=null){
            engine.reload();
        }
        return context;
    }

    public void resize(){
        Rectangle rect = MainFrame.getContext().getBounds();
        int cW = rect.width;
        int cH = rect.height;
        if(view!=null){
            view.setPrefSize(new Integer(cW).doubleValue(),new Integer(cH).doubleValue());
        }
    }

    public static JMediaPlayer getContext() {
        return context;
    }

    public JMediaPlayer destroyWebView(){
        if(webBrowser!=null){
            this.remove(webBrowser);
            this.updateUI();
        }
        return context;
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/23
     * @Description  :
     * 
     */
    public JMediaPlayer setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
        return context;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    private class PlayerThread extends Thread{
        private AtomicBoolean ctrlPlayer = new AtomicBoolean(true);

        private String url;
        private double width;
        private double height;

        public PlayerThread(String url,double w,double h){
            this.url = url;
            this.width = w;
            this.height = h;
        }

        @Override
        public void run() {
            super.run();
            ThreadGroup tg = super.getThreadGroup();
            logger.info("player thread info :{}-{} ",super.getId(),super.getName());
            if(!ctrlPlayer.get())return;
            root = new Group();
            Scene scene = new Scene(root,width,height);
            webBrowser.setScene(scene);
            webBrowser.setLayout(new BorderLayout());
            view = new WebView();
            view.setMinSize(width,height);
            view.setPrefSize(width,height);
            engine = view.getEngine();
            engine.load(url);
            root.getChildren().add(view);
        }
        /**
         * @author      : lanbery
         * @Datetime    : 2018/10/23
         * @Description  :
         * 
         */
        public boolean needLoaded(String url){
            return !this.url.equals(url);
        }

        public void interrupt(){
            try{

                if(root!=null && view != null){
                    root.getChildren().remove(view);
                }
                super.interrupt();
            }catch (RuntimeException re){
                logger.error(re.getMessage());
            }
        }

    }
}
