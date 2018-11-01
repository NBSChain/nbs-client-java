package io.nbs.client.ui.panels.media.frames;

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
    private static Group root;
    private static WebView view;

    private BrowserThread browserThread;

    public MainPlayerPanel(MediaBrowserFrame browserFrame){
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
    }

    public void load(String url){
        Rectangle rect = browserFrame.getBounds();
        double w = rect.getWidth();
        int tbH =  browserFrame.getStatusPanel().getHeight();
        logger.info("TB>{}",tbH);
        double h = rect.getHeight() - new Integer(MediaBrowserFrame.TH_SIZE).doubleValue()-new Integer(tbH).doubleValue();
        logger.info("browser w-h :{} * {}",w,h);
        browserThread = new BrowserThread(url,w,h);
        Platform.runLater(browserThread);
        webBrowser.updateUI();
        this.updateUI();
    }


    private class BrowserThread extends Thread{
        private String url;
        private double width;
        private double height;
        public BrowserThread(String url,double w,double h){
            this.url = url;
            this.width = w;
            this.height = h;
        }
        private CloseMouseAdapter adapter;

        @Override
        public void run() {
            super.run();
            root = new Group();
            Scene scene = new Scene(root);
            webBrowser.setScene(scene);
            view = new WebView();
            view.setMinSize(width,height);
            view.setPrefSize(width,height);
            WebEngine engine = view.getEngine();
            JLabel closeLabel = browserFrame.getTitlePanel().getCloseLabel();
            adapter = new CloseMouseAdapter(engine);
            closeLabel.addMouseListener(adapter);
            engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                @Override
                public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                    logger.info("loading:{}",newValue.name() );
                    adapter.setComplated(true);
                    if(newValue == Worker.State.SUCCEEDED || newValue == Worker.State.FAILED){
                        browserFrame.getStatusPanel().setState(newValue.name());
                        webBrowser.updateUI();
                    }
                }
            });

            engine.load(url);
            root.getChildren().add(view);
        }
    }

    private class CloseMouseAdapter extends MouseAdapter{
        private boolean complated = false;
        private WebEngine webEngine;

        public CloseMouseAdapter(WebEngine webEngine){
            this.webEngine = webEngine;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(complated&&webEngine!=null){
                logger.info("closed browser....");
                try{
                    String scripts = "var myVideo = document.getElementsByTagName(\"video\")[0]; " +
                            "if(myVideo)myVideo.pause();";
                    webEngine.executeScript(scripts);
                    Platform.setImplicitExit(true);
                }catch (RuntimeException re){
                    logger.warn(re.getMessage(),re.getCause());
                }
            }
            browserFrame.dispose();
        }

        public boolean isComplated() {
            return complated;
        }

        public void setComplated(boolean complated) {
            logger.info("setTrue");
            this.complated = complated;
        }
    }

    @Override
    public void resize() {
        Rectangle rect = getBounds();
        int cW = rect.width;
        int cH = rect.height;
        if(view!=null){
            webBrowser.setPreferredSize(new Dimension(cW,cH));
            view.setPrefSize(new Integer(cW).doubleValue(),new Integer(cH).doubleValue());
            webBrowser.updateUI();
        }
    }
}
