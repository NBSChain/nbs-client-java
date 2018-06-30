package io.ipfs.nbs.ui.panels;

import UI.ConstantsUI;
import com.nbs.ui.components.VerticalFlowLayout;
import io.ipfs.nbs.cnsts.ColorCnst;
import io.ipfs.nbs.ui.components.GBC;
import io.ipfs.nbs.ui.components.LamButtonIcon;
import io.ipfs.nbs.ui.components.NBSIconButton;
import io.ipfs.nbs.ui.frames.MainFrame;
import io.ipfs.nbs.utils.ButtonIconUtil;
import io.ipfs.nbs.utils.IconUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Package : io.ipfs.nbs.ui.panels
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/6/30-16:41
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class ToolbarPanel extends JPanel {
    private static ToolbarPanel context;
    private JPanel upButtonPanel;
    private JPanel bottomPanel;
    /**
     * 头像
     */
    private JLabel avatarLabel;
    /**
     *
     */
    private static NBSIconButton infoBTN;
    /**
     *
     */
    private static NBSIconButton imBTN;
    /**
     *
     */
    private static NBSIconButton dataBTN;
    /**
     *
     */
    private static NBSIconButton musicBTN;
    private static NBSIconButton setBTN;

    private static NBSIconButton aboutBTN;




    public ToolbarPanel() {
        initComponents();
        initView();
    }


    private void initComponents(){

        upButtonPanel = new JPanel();
        upButtonPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP,0,15,false,false));

        avatarLabel = new JLabel();

        avatarLabel.setIcon(IconUtil.getIcon(this,"/icons/lambor48.png"));
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);
        initialButton();

        /**
         *
         */
        bottomPanel = new JPanel();
        bottomPanel.setBackground(ColorCnst.DARKER);
        bottomPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.BOTTOM,0,10,false,false));


    }

    private void initView(){
        //setPreferredSize(new Dimension(MainFrame.TOOLBAR_WIDTH,MainFrame.HEIGHT));
        setLayout(new GridBagLayout());
        upButtonPanel.setBackground(ColorCnst.DARKER);
        upButtonPanel.add(avatarLabel);
        avatarLabel.setBackground(ColorCnst.DARK);

        upButtonPanel.add(infoBTN);
        upButtonPanel.add(imBTN);
        imBTN.actived();
        upButtonPanel.add(dataBTN);
        upButtonPanel.add(musicBTN);


        bottomPanel.add(aboutBTN);

        add(upButtonPanel,
                new GBC(0,0).setWeight(1,7).setFill(GBC.VERTICAL).setInsets(2,0,0,0));

        add(bottomPanel,
                new GBC(0,1).setWeight(1,1).setFill(GBC.VERTICAL).setInsets(0,0,2,0));


    }


    private void initialButton(){
        infoBTN = ButtonIconUtil.infoBTN;
        imBTN = ButtonIconUtil.imBTN;
        dataBTN =ButtonIconUtil.dataBTN;
        musicBTN = ButtonIconUtil.musicBTN;
        aboutBTN = ButtonIconUtil.aboutBTN;
    }
}
