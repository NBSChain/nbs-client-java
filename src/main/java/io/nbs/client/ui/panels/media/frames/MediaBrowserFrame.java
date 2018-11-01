package io.nbs.client.ui.panels.media.frames;

import io.nbs.client.Launcher;
import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.FontUtil;
import io.nbs.client.cnsts.OSUtil;
import io.nbs.commons.utils.IconUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/27
 */
public class MediaBrowserFrame extends JFrame {
    private final Logger logger = LoggerFactory.getLogger(MediaBrowserFrame.class);
    public static final int W_SIZE = 1280;
    public static final int H_SIZE = 960;
    public static final int TH_SIZE = 30;
    public int currentWindowWidth = W_SIZE;
    public int currentWindowHeight = H_SIZE;

    private String hash;
    private String title;

    private MediaTitlePanel titlePanel;
    private MainPlayerPanel playerPanel;

    private PlayerStatusPanel statusPanel;
    private Container container;

    public MediaBrowserFrame (String hash){
        this(hash,null);
    }

    public MediaBrowserFrame (String hash,String title){
        this.hash = hash;

        titlePanel = new MediaTitlePanel(this,title);
        playerPanel = new MainPlayerPanel(this);
        titlePanel.setWinResizer(playerPanel);
        this.statusPanel = new PlayerStatusPanel();
        initComponents();
        initView();

        //

        playerPanel.load(getUrl());
    }

    private void initComponents(){
        Dimension winSize = new Dimension(W_SIZE,H_SIZE);
        setMinimumSize(winSize);
        setMaximumSize(winSize);

        UIManager.put("Label.font", FontUtil.getDefaultFont());
        UIManager.put("Panel.font", FontUtil.getDefaultFont());
        UIManager.put("TextArea.font", FontUtil.getDefaultFont());

        UIManager.put("Panel.background", ColorCnst.WINDOW_BACKGROUND);
        UIManager.put("CheckBox.background", ColorCnst.WINDOW_BACKGROUND);

        this.statusPanel.setBackground(ColorCnst.DARK);
        if(OSUtil.getOsType() != OSUtil.Mac_OS){
            String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            setUndecorated(true);//隐藏标题栏
            try {
                UIManager.setLookAndFeel(windows);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        container = getContentPane();
    }

    private void initView(){
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new LineBorder(ColorCnst.LIGHT_GRAY));
        contentPanel.setLayout(new BorderLayout());


        if(OSUtil.getOsType()!=OSUtil.Mac_OS){
            this.setIconImage(Launcher.logo.getImage());
        }
        container.setLayout(new BorderLayout());
        container.add(titlePanel,BorderLayout.NORTH);
        container.add(playerPanel,BorderLayout.CENTER);
        container.add(statusPanel,BorderLayout.SOUTH);

        //this.add(container,BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerScreen();
    }

    public String getUrl(){
        return Launcher.appSettings.getGatewayURL(hash);
    }

    /**
     * 居中设置
     */
    private void centerScreen(){
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - W_SIZE) / 2,
                (tk.getScreenSize().height - H_SIZE) / 2);
    }

    public PlayerStatusPanel getStatusPanel() {
        return statusPanel;
    }

    public MediaTitlePanel getTitlePanel() {
        return titlePanel;
    }
}
