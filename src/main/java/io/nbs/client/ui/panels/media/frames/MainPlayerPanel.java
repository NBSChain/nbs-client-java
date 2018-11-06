package io.nbs.client.ui.panels.media.frames;

import io.nbs.client.cnsts.OSUtil;
import io.nbs.client.ui.components.DialogPlayer;
import io.nbs.client.ui.panels.WinResizer;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/27
 */
public class MainPlayerPanel extends JPanel implements WinResizer {
    private final static Logger logger = LoggerFactory.getLogger(MainPlayerPanel.class);
    private MediaBrowserFrame browserFrame;

    private final JFXPanel webBrowser = new JFXPanel();
    private  Group root;
    private  WebView view;
    private  WebEngine engine;

    private static MainPlayerPanel context;

    public MainPlayerPanel(MediaBrowserFrame browserFrame){
        context = this;
        this.browserFrame = browserFrame;
        initComponents();
        initView();
    }

    private void initComponents(){
        webBrowser.setLayout(new BorderLayout());
    }

    private void initView(){
        setBorder(null);
        this.setLayout(new BorderLayout());
        this.add(webBrowser,BorderLayout.CENTER);

        double dW = new Integer(browserFrame.currentWindowWidth).doubleValue();
        double dH = new Integer(browserFrame.currentWindowHeight-MediaBrowserFrame.TH_SIZE-PlayerStatusPanel.status_H).doubleValue();

        Platform.runLater(new MediaRunable(browserFrame.getUrl(),dW,dH));
        webBrowser.updateUI();
    }

    @Override
    public void resize() {
        int cW = browserFrame.currentWindowWidth;
        int cH = browserFrame.currentWindowHeight - MediaBrowserFrame.TH_SIZE - PlayerStatusPanel.status_H;
        if(view!=null){
            view.setPrefSize(new Integer(cW).doubleValue(),new Integer(cH).doubleValue());
        }
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/11/5
     * @Description  :
     *
     */
    public class MediaRunable implements Runnable{
        private String url;
        private double width;
        private double height;

        public MediaRunable(String url,double w,double h){
            this.url = url;
            this.width = w;
            this.height = h;
        }

        @Override
        public void run() {

            root = new Group();
            Scene scene = new Scene(root,this.width,this.height);
            webBrowser.setScene(scene);
            view = new WebView();
            view.setPrefSize(width,height);
            view.setMinSize(width,height);
            engine = view.getEngine();
            engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                @Override
                public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                    logger.info("{} loading...{}",url,oldValue.name());
                    if(oldValue == Worker.State.READY || newValue == Worker.State.RUNNING){
                        logger.info("add closed listener ...{}",oldValue.name());
                        if(OSUtil.getOsType() != OSUtil.Mac_OS){
                            JLabel closeLabel = browserFrame.getTitlePanel().getCloseLabel();
                            if(closeLabel!=null){
                                closeLabel.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        super.mouseClicked(e);
                                        Platform.runLater(new CloseRunable(engine,0));
                                    }
                                });
                            }
                        }else {
                            logger.info("add closed Mac listener ...{}",oldValue.name());
                            browserFrame.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    super.windowClosing(e);
                                    Platform.runLater(new CloseRunable(engine,0));
                                    logger.info("closed Mac Window ...{}",oldValue.name());
                                }

                            });
                        }

                    }
                    if(oldValue == Worker.State.RUNNING){

                    }
                    if(newValue == Worker.State.SUCCEEDED || newValue == Worker.State.FAILED){
                        PlayerStatusPanel statusPanel = browserFrame.getStatusPanel();
                        if(statusPanel!=null)statusPanel.setState(newValue.name());

                    }
                    logger.info("loaded State {}",newValue.name());
                }
            });
            engine.load(url);
            root.getChildren().add(view);
        }
    }

    public class CloseRunable implements Runnable {
        private WebEngine engine;
        private int state = 0;
        public CloseRunable(WebEngine webEngine,int stat){
            this.engine = webEngine;
            this.state = stat;
        }

        public CloseRunable(WebEngine webEngine){
            this.engine = webEngine;
        }

        @Override
        public void run() {
            if(engine == null)return;
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>closed .......... engine...");
            switch (state){
                case 0:
                    engine.load(null);
                    break;
                case 1:

                    break;
                case 2:

                    break;
                default:
            }
        }
    }
}
