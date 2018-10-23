package io.nbs.client.ui.components.adapters;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/23
 */
public class PlaceholderListener implements FocusListener {
    private String placeholder;
    private Color tipColor;
    private Color normalColor = Color.BLACK;


    public PlaceholderListener (String placeholder,Color tipColor,Color normalColor){
        this.placeholder = placeholder==null ? "" : placeholder;
        this.tipColor = tipColor;
        this.tipColor = normalColor;
    }

    public PlaceholderListener(String placeholder){
        this(placeholder,Color.GRAY,Color.BLACK);
    }

    public PlaceholderListener(String placeholder,Color normalColor){
        this(placeholder,Color.GRAY,normalColor);
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/23
     * @Description  :
     *
     */
    @Override
    public void focusGained(FocusEvent e) {
        if(e.getComponent() instanceof JTextField){
            JTextField field = (JTextField)e.getComponent();
            String content = field.getText();
            if(content==null|| StringUtils.isBlank(content)|| content.trim().equals(placeholder)){
                field.setText("");
            }
            field.setForeground(normalColor);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(e.getComponent() instanceof JTextField){
            JTextField field = (JTextField)e.getComponent();
            String content = field.getText();
            if(content==null|| StringUtils.isBlank(content)){
                field.setText(placeholder);
                field.setForeground(this.tipColor);
            }else {
                field.setForeground(this.normalColor);
            }
        }
    }
}
