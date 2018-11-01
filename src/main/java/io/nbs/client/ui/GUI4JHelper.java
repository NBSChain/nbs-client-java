package io.nbs.client.ui;

import io.nbs.client.cnsts.ColorCnst;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 *
 * Author   : lanbery
 * Created  : 2018/10/19
 */
public class GUI4JHelper {

    public static void addWinCloseIconListener(Component component){
        if(component==null)return;
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(1);
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBackground(ColorCnst.LIGHT_GRAY);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setBackground(ColorCnst.WINDOW_BACKGROUND);
                super.mouseExited(e);
            }
        });
    }
}
