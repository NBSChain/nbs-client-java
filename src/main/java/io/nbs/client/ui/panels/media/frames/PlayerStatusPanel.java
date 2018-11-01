package io.nbs.client.ui.panels.media.frames;

import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.FontUtil;
import io.nbs.client.ui.components.GBC;
import io.nbs.commons.utils.IconUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/27
 */
public class PlayerStatusPanel extends JPanel {
    private ImageIcon loadingbar;
    private JLabel loaingLabel;
    private JLabel statusLabel;
    public static int status_H = 28;

    private HorLine horLine;

    public PlayerStatusPanel(){
        this.loadingbar = IconUtil.getIcon(this,"/icons/loading-bar.gif");
        initComponents();
        initView();
    }

    private void initComponents(){
        horLine = new HorLine(ColorCnst.FONT_GRAY_DARKER);
        horLine.setBackground(ColorCnst.FONT_GRAY_DARKER);
        horLine.setBorder(null);

        loaingLabel = new JLabel(this.loadingbar);
        loaingLabel.setHorizontalAlignment(JLabel.LEFT);
        loaingLabel.setMinimumSize(new Dimension(320,status_H));

        statusLabel = new JLabel("Loading...");
        statusLabel.setHorizontalAlignment(JLabel.RIGHT);
        statusLabel.setFont(FontUtil.getDefaultFont(10));
        statusLabel.setForeground(ColorCnst.FONT_WHITE);
    }

    private void initView(){
        this.setLayout(new BorderLayout());
        JPanel showPanel = new JPanel();
        showPanel.setLayout(new GridBagLayout());
        showPanel.setBackground(ColorCnst.DARK);
//        this.add(horLine,
//                new GBC(0,0).setFill(GBC.HORIZONTAL).setWeight(800,1).setInsets(0,0,0,0));
        showPanel.add(loaingLabel,
                new GBC(0,0).setFill(GBC.HORIZONTAL).setWeight(75,1).setInsets(0,5,0,0));
        showPanel.add(statusLabel,
                new GBC(1,0).setFill(GBC.BOTH).setWeight(25,1).setInsets(0,5,0,5));

        this.add(horLine,BorderLayout.CENTER);
        this.add(showPanel,BorderLayout.SOUTH);
        //this.setBorder(new LineBorder(ColorCnst.FONT_GRAY_DARKER));
    }

    public void setState(String state){
        if(StringUtils.isBlank(state))return;
        this.loaingLabel.setVisible(false);
        this.statusLabel.setText(state);
        this.updateUI();
    }


    private class HorLine extends JPanel{

        private Color color;
        private int broderSize = 1;

        public HorLine(Color color,int broderSize){
            this.color = color == null ? ColorCnst.FONT_GRAY_DARKER : color;
            if(broderSize>0)this.broderSize=broderSize;
            this.setBorder(null);
        }
        public HorLine(Color color){
            this(color,1);
        }

        public HorLine(){
            this(ColorCnst.FONT_GRAY_DARKER,1);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(this.color);
            int w = getWidth();
            int h = getHeight();
            int start = getX();
            g2.draw(new Line2D.Double(getX(),getY(),getX()+w,getY()+broderSize));

        }
    }

}
