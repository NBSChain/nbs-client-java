package io.nbs.client.ui.panels.media.frames;

import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.FontUtil;
import io.nbs.client.cnsts.OSUtil;
import io.nbs.client.listener.AbstractMouseListener;
import io.nbs.client.ui.components.GBC;
import io.nbs.client.ui.panels.WinResizer;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/27
 */
public class MediaTitlePanel extends JPanel {
    private MediaBrowserFrame browserFrame;
    private JPanel titlePanel;
    private JLabel titleLabel;
    private JPanel ctrlPanel;
    private JLabel closeLabel;
    private JLabel maxLabel;
    private JLabel minLabel;

    private ImageIcon maxIcon;
    private ImageIcon minIcon;
    private ImageIcon restoreIcon;

    private boolean windowMax ;
    private Rectangle desktopBounds; // 去除任务栏后窗口的大小
    private Rectangle normalBounds;
    private long lastClickTime;
    private WinResizer winResizer;

    private CtrlLabelMouseListener listener = new CtrlLabelMouseListener();
    private static Point origin = new Point();
    public MediaTitlePanel(MediaBrowserFrame browserFrame,String title){
        this.browserFrame = browserFrame;
        if(StringUtils.isBlank(title))title = "多媒体浏览器";
        initComponents(title);
        initView();
        setListeners();
        initBounds();
    }

    private void initComponents(String title){
        setBackground(ColorCnst.WINDOW_BACKGROUND);
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        Dimension ctrlItemSize = new Dimension(30,30);
        maxIcon = new ImageIcon(getClass().getResource("/icons/window_max.png"));
        minIcon = new ImageIcon(getClass().getResource("/icons/window_min.png"));
        restoreIcon =  new ImageIcon(getClass().getResource("/icons/window_restore.png"));

        titlePanel = new JPanel();
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.setBackground(ColorCnst.WINDOW_BACKGROUND);

        titleLabel = new JLabel(title);
        titleLabel.setFont(FontUtil.getDefaultFont(15));

        /**
         * 窗口控制
         */
        ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));

        closeLabel = new JLabel();
        closeLabel.setIcon(new ImageIcon(getClass().getResource("/icons/close.png")));
        closeLabel.setBackground(ColorCnst.WINDOW_BACKGROUND);
        closeLabel.setHorizontalAlignment(JLabel.CENTER);
        closeLabel.setOpaque(true);
        closeLabel.setPreferredSize(ctrlItemSize);
        closeLabel.setCursor(handCursor);
        closeLabel.addMouseListener(listener);

        maxLabel = new JLabel();
        maxLabel.setIcon(maxIcon);
        maxLabel.setBackground(ColorCnst.WINDOW_BACKGROUND);
        maxLabel.setHorizontalAlignment(JLabel.CENTER);
        maxLabel.setOpaque(true);
        maxLabel.setPreferredSize(ctrlItemSize);
        maxLabel.setCursor(handCursor);
        maxLabel.addMouseListener(listener);

        minLabel = new JLabel();
        minLabel.setIcon(minIcon);
        minLabel.setBackground(ColorCnst.WINDOW_BACKGROUND);
        minLabel.setHorizontalAlignment(JLabel.CENTER);
        minLabel.setOpaque(true);
        minLabel.setPreferredSize(ctrlItemSize);
        minLabel.setCursor(handCursor);
        minLabel.addMouseListener(listener);

    }

    private void initView(){
        setLayout(new GridBagLayout());
        setBorder(null);
        JPanel left = new JPanel();
        left.setLayout(new FlowLayout(FlowLayout.LEFT,5,0));
        JPanel right = new JPanel();
        right.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));

        ctrlPanel.add(minLabel);
        ctrlPanel.add(maxLabel);
        ctrlPanel.add(closeLabel);

        int margin;
        if(OSUtil.getOsType() != OSUtil.Mac_OS){
            add(left,
                    new GBC(0,0).setFill(GBC.BOTH).setWeight(85,5).setInsets(0,0,0,0));
            add(right,
                    new GBC(1,0).setFill(GBC.HORIZONTAL).setWeight(10,2).setInsets(0,0,0,0) );
            right.add(ctrlPanel);
            left.add(titlePanel);
            margin = 5;
        }else {
            left.add(titlePanel);
            add(left,
                    new GBC(0,0).setFill(GBC.BOTH).setWeight(85,5).setInsets(0,0,0,0));
            margin = 10;
        }

        titlePanel.add(titleLabel,
                new GBC(0,0).setFill(GBC.BOTH).setWeight(300,1).setInsets(0,margin,0,0));
    }

    public void setWinResizer(WinResizer winResizer) {
        this.winResizer = winResizer;
    }

    private void setListeners(){
        if(OSUtil.getOsType() != OSUtil.Mac_OS){
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    origin.x = e.getX();
                    origin.y =e.getY();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if(System.currentTimeMillis()-lastClickTime < 700){
                        maxOrRestoreWindow();
                    }
                    lastClickTime =System.currentTimeMillis();
                    super.mouseClicked(e);
                }
            };

            /**
             * 拖动
             */
            MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    Point oldP = browserFrame.getLocation();
                    browserFrame.setLocation(oldP.x +e.getX()-origin.x,oldP.y+e.getY()-origin.y);
                    //super.mouseDragged(e);
                }
            };

            ctrlPanel.addMouseListener(mouseAdapter);
            ctrlPanel.addMouseMotionListener(mouseMotionListener);
            this.addMouseListener(mouseAdapter);
            this.addMouseMotionListener(mouseMotionListener);
        }

    }

    private void initBounds(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(browserFrame.getGraphicsConfiguration());
        normalBounds = new Rectangle(
                (screenSize.width -MediaBrowserFrame.W_SIZE)/2,
                (screenSize.height -MediaBrowserFrame.H_SIZE) /2,
                MediaBrowserFrame.W_SIZE,MediaBrowserFrame.H_SIZE
        );
        desktopBounds = new Rectangle(insets.left,insets.top,
                screenSize.width - insets.left - insets.right,
                screenSize.height-insets.top-insets.bottom
        );
    }

    private void maxOrRestoreWindow(){
        if(windowMax){
            browserFrame.setBounds(normalBounds);
            maxLabel.setIcon(maxIcon);
            browserFrame.resizeBounds(normalBounds);
            windowMax = false;
        }else {
            browserFrame.setBounds(desktopBounds);
            maxLabel.setIcon(restoreIcon);
            browserFrame.resizeBounds(desktopBounds);
            windowMax = true;
        }
    }


    public JLabel getCloseLabel() {
        return closeLabel;
    }

    public class CtrlLabelMouseListener extends AbstractMouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getComponent()==closeLabel){
                browserFrame.closePlayer();
                browserFrame.dispose();
            }else if(e.getComponent() == maxLabel){
                maxOrRestoreWindow();
                if(winResizer!=null)winResizer.resize();
            }else if(e.getComponent() == minLabel){
                browserFrame.setExtendedState(JFrame.ICONIFIED);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            ((JLabel)e.getSource()).setBackground(ColorCnst.LIGHT_GRAY);
            super.mouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            ((JLabel)e.getSource()).setBackground(ColorCnst.WINDOW_BACKGROUND);
            super.mouseExited(e);
        }
    }
}
