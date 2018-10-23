package io.nbs.client.ui.panels.media;


import io.nbs.client.Launcher;
import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.ui.components.adapters.MessageMouseListener;
import io.nbs.client.ui.panels.TitlePanel;
import io.nbs.client.ui.panels.WinResizer;

import io.nbs.commons.utils.IconUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    private static MediaCard currentCard = MediaCard.searcher;
    //top
    private TitlePanel winTitlePanel;

    private JPanel centerPanel;

    private CardLayout cardLayout;

    private HashSearchPanel searchPanel;
    private JMediaPlayer mediaPlayer;

    private JLabel goSearchIcon;

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
        ImageIcon searchIcon = IconUtil.getIcon(this,"/icons/go-searcher.png");
        goSearchIcon = new JLabel();
        goSearchIcon.setIcon(searchIcon);
        this.winTitlePanel = new TitlePanel(this,this,goSearchIcon);
        winTitlePanel.setTitle(Launcher.appSettings.getConfigVolme("nbs.ui.panel.media.label","MultiMedia"));
        winTitlePanel.setBackground(ColorCnst.LIGHT_GRAY);

        centerPanel = new JPanel();
        cardLayout = new CardLayout();


    }

    /**
     * [initView description]
     *
     * @return {[type]} [description]
     */
    private void initView() {
        this.setLayout(new BorderLayout());

        centerPanel.setLayout(cardLayout);

        //添加卡片
        searchPanel = new HashSearchPanel(this);
        centerPanel.add(searchPanel,MediaCard.searcher.name());

        mediaPlayer = new JMediaPlayer();
        centerPanel.add(mediaPlayer,MediaCard.player.name());


        this.add(winTitlePanel,BorderLayout.NORTH);
        this.add(centerPanel,BorderLayout.CENTER);
    }

    private void setListeners() {
        goSearchIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logger.info("show searcher...");
                MediaMasterPanel.getContext().switchCard(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                goSearchIcon.setBackground(ColorCnst.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                goSearchIcon.setBackground(ColorCnst.WINDOW_BACKGROUND);
            }
        });
    }

    /**
     * [getContext description]
     *
     * @return {[type]} [description]
     */
    public static MediaMasterPanel getContext() {
        return context;
    }

    public static enum MediaCard {
        searcher,player;
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/23
     * @Description  :
     * null 切换，
     * not 设置
     */
    public void switchCard(MediaCard mediaCard){
        if(mediaCard == null ){
            for(MediaCard card : MediaCard.values()){
                if(card != currentCard){
                    currentCard = card;
                    this.cardLayout.show(centerPanel,currentCard.name());
                    return;
                }
            }
        }else {
            currentCard = mediaCard;
            this.cardLayout.show(centerPanel,mediaCard.name());
        }
    }

    @Override
    public void resize() {
        this.mediaPlayer.resize();
    }

    public JMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void openAndLoadHash(String hash){
        if(StringUtils.isBlank(hash)){
            this.switchCard(MediaCard.searcher);
        }else {
            this.mediaPlayer.loadHash(hash);
        }
    }
}