package io.nbs.client.ui.panels.media;

import io.nbs.client.Launcher;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/23
 */
public class MediaPlayerView extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MediaPlayerView.class);
    public static final String DEFAULT_JQUERY_MIN_VERSION = "1.7.2";
    public static final String JQUERY_LOCATION = "http://code.jquery.com/jquery-1.7.2.min.js";
    public static String hash;
    public static String title;
    public static String url;
    private static WebEngine engine;
/*    public MediaPlayerView(String hash,String name){
        this.hash = hash;
        this.title = name;
    }*/

    public static void launcher(String... args){
        if(args.length==1){
            hash = args[0];
            title = args[0];
            url = Launcher.appSettings.getGatewayURL(hash);
        }else if(args.length==2){
            hash = args[0];
            title = args[1];
            url = Launcher.appSettings.getGatewayURL(hash);
        }else if(args.length>=3){
            hash = args[0];
            title = args[1];
            url = args[2]+hash;
        }else {
            url = "http://nbsio.net";
        }
        launch(MediaPlayerView.class,args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final WebView webView = new WebView();
        engine = webView.getEngine();
        logger.info(url);
        engine.load(url);
        engine.documentProperty().addListener(new ChangeListener<Document>() {
            @Override public void changed(ObservableValue<? extends Document> prop, Document oldDoc, Document newDoc) {
                executejQuery(
                        engine,
                        "$(\"a\").click(function(event){" +
                                "  event.preventDefault();" +
                                "  $(this).hide(\"slow\");" +
                                "});"
                );
            }
        });
        primaryStage.setScene(new Scene(webView));
        primaryStage.show();
    }

    private static void enableFirebug(final WebEngine engine) {
        engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
    }

    private static Object executejQuery(final WebEngine engine, String minVersion, String jQueryLocation, String script) {
        return engine.executeScript(
                "(function(window, document, version, callback) { "
                        + "var j, d;"
                        + "var loaded = false;"
                        + "if (!(j = window.jQuery) || version > j.fn.jquery || callback(j, loaded)) {"
                        + "  var script = document.createElement(\"script\");"
                        + "  script.type = \"text/javascript\";"
                        + "  script.src = \"" + jQueryLocation + "\";"
                        + "  script.onload = script.onreadystatechange = function() {"
                        + "    if (!loaded && (!(d = this.readyState) || d == \"loaded\" || d == \"complete\")) {"
                        + "      callback((j = window.jQuery).noConflict(1), loaded = true);"
                        + "      j(script).remove();"
                        + "    }"
                        + "  };"
                        + "  document.documentElement.childNodes[0].appendChild(script) "
                        + "} "
                        + "})(window, document, \"" + minVersion + "\", function($, jquery_loaded) {" + script + "});"
        );
    }

    private static Object executejQuery(final WebEngine engine, String minVersion, String script) {
        return executejQuery(engine, DEFAULT_JQUERY_MIN_VERSION, JQUERY_LOCATION, script);
    }

    private Object executejQuery(final WebEngine engine, String script) {
        return executejQuery(engine, DEFAULT_JQUERY_MIN_VERSION, script);
    }

    public void stop(){

    }
}
