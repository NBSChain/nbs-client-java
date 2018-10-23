package io.nbs.client.ui.components;

import io.nbs.client.Launcher;
import io.nbs.client.ui.frames.MainFrame;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;

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
    private static Group root;
    private static WebView view;
    private static WebEngine engine;
    public DialogPlayer (String hash,String name){
        super(MainFrame.getContext(),name,false);
        container = getContentPane();
        this.hash = hash;
        this.name = name;
        initComponets();
    }

    private void initComponets(){
        this.setBounds(MainFrame.getContext().getBounds());
        int width = getWidth();
        int height = getHeight();
        container.setLayout(new BorderLayout());
        String url = Launcher.appSettings.getGatewayURL(hash);
        final Stage stage;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                root = new Group();
                Scene scene = new Scene(root,width,height);
                webBrowser.setScene(scene);
                webBrowser.setLayout(new BorderLayout());
                view = new WebView();
                view.setMinSize(width,height);
                view.setPrefSize(width,height);
                engine = view.getEngine();
                engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                        logger.info(observable.toString());
                        if(newValue == Worker.State.SUCCEEDED){
                            Document document = engine.getDocument();
                            logger.info("Page:{}",document.getXmlEncoding());
                        }else {

                        }
                    }
                });
                engine.load(url);
                root.getChildren().add(view);
            }
        });
        container.add(webBrowser,BorderLayout.CENTER);

    }
}
