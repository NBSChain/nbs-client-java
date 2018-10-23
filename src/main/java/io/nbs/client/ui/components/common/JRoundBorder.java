package io.nbs.client.ui.components.common;

import io.nbs.client.cnsts.ColorCnst;

import javax.swing.border.Border;
import java.awt.*;

/**
 * @Package : io.nbs.client.ui.components.common
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/7/17-13:13
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class JRoundBorder implements Border {
    private Color color;


    public JRoundBorder(Color color) {
        if(color==null){
            this.color = ColorCnst.FONT_GRAY_DARKER;
        }else {
            this.color = color;
        }
    }

    public JRoundBorder() {
        this(null);
    }



    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(color);
        int cW = 0,cH=0;
        if(c!=null){
            cW = c.getWidth();
            cH = c.getHeight();
        }
        int arcWidth = cW/2 -1 > 0 ? (cW/2 -1) : 2;
        int arcHeight = cH/2 - 1 > 0 ? (cH/2 - 1) : 2;
        int arcSize = arcWidth > arcHeight ? arcHeight : arcWidth;
        g.drawRoundRect(0,0,cW,cH,arcSize,arcSize);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0,0,0,0);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
