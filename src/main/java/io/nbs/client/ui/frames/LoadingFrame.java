package io.nbs.client.ui.frames;

import io.nbs.client.Launcher;
import io.nbs.client.cnsts.AppGlobalCnst;
import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.FontUtil;
import io.nbs.client.cnsts.OSUtil;
import io.nbs.client.ui.components.GBC;
import io.nbs.commons.utils.IconUtil;

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
 * Created  : 2018/10/22
 */
public class LoadingFrame extends JFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    private JLabel loadinglabel;
    private JLabel tipLabel;

    public LoadingFrame(ImageIcon icon){
        loadinglabel = new JLabel(icon);
        tipLabel = new JLabel();
        initComponents();
        initView();
        centerScreen();
    }

    private void initComponents(){
        Dimension winSize = new Dimension(WIDTH,HEIGHT);
        setMinimumSize(winSize);
        setMaximumSize(winSize);

        tipLabel = new JLabel();
        tipLabel.setForeground(ColorCnst.MAIN_COLOR);
        tipLabel.setFont(FontUtil.getDefaultFont(18));
        tipLabel.setText(Launcher.appSettings.getConfigVolme("nbs.client.loading.msg","NBS Client Start..."));
        tipLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
    }

    private void initView(){
        if(OSUtil.getOsType() != OSUtil.Mac_OS) {
            setUndecorated(true);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }else {
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new LineBorder(ColorCnst.LIGHT_GRAY));
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(loadinglabel,BorderLayout.CENTER);
        contentPanel.add(tipLabel,BorderLayout.SOUTH);
        this.add(contentPanel);


    }

    private void centerScreen(){
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation(
                (tk.getScreenSize().width-WIDTH)/2,
                (tk.getScreenSize().height-HEIGHT)/2
        );
    }
}
