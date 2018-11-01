package io.nbs.client.ui.components;

import io.nbs.client.Launcher;
import io.nbs.client.ui.frames.MainFrame;
import io.nbs.client.ui.panels.media.JDialogWindowListener;
import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;
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
public class DialogPlayer extends JDialog {
    private final static Logger logger = LoggerFactory.getLogger(DialogPlayer.class);

    private final static String scriptStr = "";

    private String name;
    private String hash;
    private Container container;
    private final JFXPanel webBrowser = new JFXPanel();
    private static int width = 0;
    private static int height = 0;

    private JLabel button;

    private static DialogPlayer context;




    public DialogPlayer (String hash,String name){
        super(MainFrame.getContext(),name,false);
        context = this;
        //this.addWindowListener(new JDialogWindowListener());
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

/*        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stop.set(true);
            }
        });*/
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
        PlayerRunable playerRunable = new PlayerRunable(this,url);
        Platform.runLater(playerRunable);
        return this;
    }

    public DialogPlayer load(){
        this.load(this.hash,this.name);
        return this;
    }


    public JLabel getButton() {
        return button;
    }

    public class PlayerRunable implements Runnable{
        private final static String PLAYER_THREAD_NAME = "nbs-player";
        private String url;
        private String preUrl;
        private DialogPlayer dialogPlayer;
        private WebEngine engine;
        private AtomicBoolean runing = new AtomicBoolean(true);
        private boolean flag = true;

        public PlayerRunable(DialogPlayer dialogPlayer,String url){
            this.dialogPlayer = dialogPlayer;
            this.url = url;
        }
        @Override
        public void run() {

            Group root = new Group();
            Scene scene = new Scene(root,width,height);
            webBrowser.setScene(scene);

            WebView view = new WebView();
            view.setMinSize(width,height);
            view.setPrefSize(width,height);

            engine = view.getEngine();
            engine.setJavaScriptEnabled(true);

            engine.getLoadWorker().stateProperty().addListener(
                    new ChangeListener<Worker.State>() {
                        @Override
                        public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                            logger.info(">>>>{}-{}",observable.getValue(),newValue.name());

                            if(newValue == Worker.State.SUCCEEDED){
                                logger.info("load event: {}");
                                dialogPlayer.getButton().addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        logger.info(">>>>>>>>>>>>>>>>>.test.................");
                                        //engine.executeScript("document.body.style.backgroundColor=\"red\";");
                                        Platform.exit();

                                        runing.set(true);
                                    }
                                });
                                //
                                dialogPlayer.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent e) {
                                        e.getWindow().dispose();
                                        super.windowClosing(e);

//                                        engine.load("");
                                        logger.info(">>>>>>>>>>>>>>>>>.closing...............");
                                        runing.set(true);
                                        //engine.executeScript("document.body.style.backgroundColor=\"red\";");
                                    }

                                    @Override
                                    public void windowActivated(WindowEvent e) {
                                        super.windowActivated(e);
                                        //engine.executeScript("document.body.style.backgroundColor=\"red\";");
                                    }
                                });

                                new Thread(){

                                }.start();
                            }
                        }
                    }
            );
            view.getEngine().load(url);
            root.getChildren().add(view);
        }

        public boolean needCoverLoad(){
            return (preUrl==null || preUrl.equalsIgnoreCase(url)) ? false : true;
        }

        public AtomicBoolean getRuning() {
            return runing;
        }

        public boolean isFlag() {
            return flag;
        }
    }


    public class mointorThread implements Runnable{

        @Override
        public void run() {


        }
    }
}
