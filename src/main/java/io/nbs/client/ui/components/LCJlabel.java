package io.nbs.client.ui.components;

import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.FontUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Package : io.ipfs.nbs.ui.components
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/7/1-11:57
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class LCJlabel extends JLabel {
    private Color defForeground;

    public LCJlabel(String text, Color foreground) {
        super(text);
        this.defForeground = foreground;
        setForeground(defForeground);
    }

    public LCJlabel(String text) {
        super(text);
        initDefault();
        setForeground(defForeground);
    }
    public LCJlabel(String text,int size) {
        super(text);
        setFont(FontUtil.getDefaultFont(size));
        initDefault();
        setForeground(defForeground);
    }

    public LCJlabel(int size) {
        initDefault();
        setFont(FontUtil.getDefaultFont(size));
        setForeground(defForeground);
    }

    public LCJlabel(Color foreground) {
        initDefault();
        setForeground(defForeground);
    }

    public LCJlabel() {
        this.defForeground = ColorCnst.FONT_GRAY;
        setForeground(defForeground);
    }

    private void initDefault(){
        this.defForeground = ColorCnst.FONT_GRAY;
    }

}
