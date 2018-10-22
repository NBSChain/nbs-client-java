package io.nbs.client.ui.panels.about;

import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.ui.frames.MainFrame;
import io.nbs.client.ui.panels.ParentAvailablePanel;
import io.nbs.commons.utils.IconUtil;

import javax.swing.*;
import java.awt.*;


/**
 * @Package : io.nbs.client.ui.panels.about
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/7/9-11:38
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class AboutBodyPanel extends ParentAvailablePanel {
    private static AboutBodyPanel context;

    private JLabel backgroundLabel;
    private ScaleIcon icon;

    /**
     * construction
     */
    public AboutBodyPanel(JPanel parent) {
        super(parent);
        context =this;

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
        Icon iconDef = IconUtil.getIcon(context,"/icons/about_bg.png");
        icon = new ScaleIcon(iconDef);
        backgroundLabel = new JLabel();
        backgroundLabel.setIcon(icon);
        //this.setBorder(ColorCnst.RED_BORDER);
    }

    /**
     * [initView description]
     *
     * @return {[type]} [description]
     */
    private void initView() {
        this.add(backgroundLabel);
    }

    private void setListeners() {

    }

    /**
     * [getContext description]
     *
     * @return {[type]} [description]
     */
    public static AboutBodyPanel getContext() {
        return context;
    }


    public void setPreSize(){
        Rectangle rect = MainFrame.getContext().getBounds();
        int cW = rect.width;
        int cH = rect.height;
        this.backgroundLabel.setPreferredSize(new Dimension(cW,cH));
        this.backgroundLabel.setIcon(icon);
        this.updateUI();
    }




    private class ScaleIcon implements Icon{
        private Icon icon;
        public ScaleIcon (Icon icon){
            this.icon = icon;
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            float w = c.getWidth();
            float h = c.getHeight();
            int iconW = getIconWidth();
            int iconH = getIconHeight();

            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.scale(w/iconW,h/iconH);
            icon.paintIcon(c,g2d,0,0);
        }

        @Override
        public int getIconWidth() {
            return icon == null ? 0 : icon.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            return icon == null ? 0 : icon.getIconHeight();
        }
    }
}