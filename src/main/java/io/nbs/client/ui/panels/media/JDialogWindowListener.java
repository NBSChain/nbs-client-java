package io.nbs.client.ui.panels.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/25
 */
public class JDialogWindowListener implements WindowListener {
    private static final Logger logger = LoggerFactory.getLogger(JDialogWindowListener.class);

    public JDialogWindowListener() {

    }

    @Override
    public void windowOpened(WindowEvent e) {
        logger.info("open");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        logger.info("closing.");
        e.getWindow().dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
