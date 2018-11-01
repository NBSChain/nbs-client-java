package io.nbs.client.ui.panels.media;

import io.nbs.client.Launcher;
import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.FontUtil;
import io.nbs.client.ui.components.GBC;
import io.nbs.client.ui.components.VerticalFlowLayout;
import io.nbs.client.ui.components.adapters.PlaceholderListener;
import io.nbs.client.ui.components.common.JRoundBorder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/23
 */
public class HashSearchPanel extends JPanel {

    private final static Logger logger = LoggerFactory.getLogger(HashSearchPanel.class);
    private final static int searchWidth = 420;
    private final static int searchHeight = 35;
    private JTextField searchField;
    private JTextArea showArea;
    private MediaMasterPanel masterPanel;
    private String tips;



    public HashSearchPanel(MediaMasterPanel masterPanel){
        this.masterPanel = masterPanel;
        tips = Launcher.appSettings.getConfigVolme("nbs.ui.panel.media.searcher.tips","please input hash volume,enter query...");
        initComponents();
        initView();
        setListeners();
    }

    public void initComponents(){
        searchField = new JTextField(tips);
        searchField.setPreferredSize(new Dimension(searchWidth,searchHeight));
        searchField.setFont(FontUtil.getDefaultFont(16));
        searchField.setForeground(ColorCnst.DARK);
        searchField.setHorizontalAlignment(JTextField.LEFT);
        searchField.setBorder(new JRoundBorder(null));
        searchField.addFocusListener(
                new PlaceholderListener(tips,ColorCnst.DARK));

        showArea = new JTextArea();
        showArea.setLineWrap(true);
        showArea.setWrapStyleWord(false);
        showArea.setForeground(ColorCnst.FONT_ABOUT_TITLE_BLUE);
        showArea.setFont(FontUtil.getDefaultFont(14));
        showArea.setText(Launcher.appSettings.getBaseGatewayUrl());
        showArea.setBackground(UIManager.getColor("Panel.background"));


    }

    public void initView(){
        this.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        //contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.MIDDLE,30,15,true,false));
        contentPanel.add(searchField
            ,new GBC(0,0).setWeight(1,1).setFill(GBC.HORIZONTAL).setInsets(60,30,0,30));
        contentPanel.add(showArea
            ,new GBC(0,1).setWeight(1,100).setFill(GBC.BOTH).setInsets(20,30,10,30));
        this.add(contentPanel,BorderLayout.CENTER);
    }

    public void setListeners(){
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateShowArea(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateShowArea(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateShowArea(e);
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String text = searchField.getText();
                if(e.getKeyCode() == KeyEvent.VK_ENTER
                        && text != null
                        && StringUtils.isNotBlank(text.trim())
                && !text.equals(tips)){
                    logger.info("search text : {}",text);
                    masterPanel.getMediaPlayer().loadHash(text);
                    masterPanel.switchCard(MediaMasterPanel.MediaCard.player);
                }
            }
        });
    }

    private void updateShowArea(DocumentEvent e){
        Document document = e.getDocument();
        String c = "";
        try{
            c = document.getText(0,document.getLength());
        }catch (BadLocationException be){
            c = "";
        }
        if(c.equals(tips)){
            c = "";
        }
        showArea.setText(Launcher.appSettings.getBaseGatewayUrl()+c);
        showArea.updateUI();
    }

}
