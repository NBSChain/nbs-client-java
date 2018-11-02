package io.nbs.client.ui.frames;

import io.ipfs.api.IPFS;

import io.nbs.client.Launcher;
import io.nbs.client.cnsts.AppGlobalCnst;
import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.FontUtil;
import io.nbs.client.cnsts.OSUtil;
import io.nbs.client.ui.GUI4JHelper;
import io.nbs.client.ui.components.GBC;
import io.nbs.client.ui.panels.init.DappBaseStepPanel;
import io.nbs.client.ui.panels.init.DappIPFSStepPanel;
import io.nbs.commons.utils.IconUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project ipfs-mm
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/16
 */
public class InitialDappFrame extends JFrame {
    private Logger logger = LoggerFactory.getLogger(InitialDappFrame.class);
    private static final int W = 550;
    private static final int H = 350;

    private IPFS ipfs;
    public static Toolkit toolkit;
    private static Point origin = new Point();

    private static InitialDappFrame context;

    public final static String NICK_PREFFIX = "NBSChain_";

    /* nav */
    private JPanel ctrlPanel;
    private JLabel titleLabel;
    private JLabel closeLabel;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private DappIPFSStepPanel ipfsStepPanel;
    private DappBaseStepPanel baseStepPanel;
    public  static Dimension buttonDimesion = new Dimension(115,35);


    public InitialDappFrame(){
        this(null);
    }

    public InitialDappFrame(String msg){
        context = this;
        toolkit = Toolkit.getDefaultToolkit();
        initComponents(msg);
        initView();
        this.setIconImage(Launcher.logo.getImage());
        setListeners();
        centerScreen();
    }

    private void initComponents(String msg){
        Dimension windowSize = new Dimension(W, H);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);
        /* header start */
        ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new GridBagLayout());

        JPanel titlePanel = new JPanel();
        //titlePanel.setBackground(Color.RED);
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,2));
        titleLabel = new JLabel(Launcher.appSettings.getConfigVolme("dapp.initStepIpfs.frame.title","NBS Host 设置"));
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setFont(FontUtil.getDefaultFont(18));
        titlePanel.add(titleLabel);

        JPanel closePanel = new JPanel();
        closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT,5,5));
        closeLabel = new JLabel();
        closeLabel.setIcon(IconUtil.getIcon(this,"/icons/close.png"));
        closeLabel.setCursor(AppGlobalCnst.HAND_CURSOR);
        closePanel.add(closeLabel);

        ctrlPanel.add(titlePanel,
                new GBC(0,0).setWeight(6,1).setFill(GBC.HORIZONTAL).setInsets(0,0,0,0));
        ctrlPanel.add(closePanel,
                new GBC(1,0).setWeight(1,1).setFill(GBC.HORIZONTAL).setInsets(0,40,15,0));
        /* header end */

        /* contents cards */

        cardLayout = new CardLayout();
        contentPanel = new JPanel();
        contentPanel.setLayout(cardLayout);

        ipfsStepPanel = new DappIPFSStepPanel(msg);
        baseStepPanel = new DappBaseStepPanel();

        contentPanel.add(ipfsStepPanel, InitDappSteps.setIpfs.name());
        contentPanel.add(baseStepPanel, InitDappSteps.setDapp.name());
        cardLayout.show(contentPanel, InitDappSteps.setIpfs.name());

    }

    private void initView(){
        JPanel framePanel = new JPanel();
        framePanel.setBorder(new LineBorder(ColorCnst.LIGHT_GRAY));
        framePanel.setLayout(new GridBagLayout());

        //添加顶部操作
        if(OSUtil.getOsType() != OSUtil.Mac_OS){
            setUndecorated(true);
            framePanel.add(ctrlPanel,
                    new GBC(0,0).setFill(GBC.HORIZONTAL).setWeight(1,1).setInsets(0,0,2,0));
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }
        framePanel.add(contentPanel,
                new GBC(0,1).setWeight(1,100).setFill(GBC.BOTH).setInsets(5,0,10,0));

        add(framePanel);
    }

    private void setListeners(){
        //
        GUI4JHelper.addWinCloseIconListener(closeLabel);

        if (OSUtil.getOsType() != OSUtil.Mac_OS)
        {
            addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    // 当鼠标按下的时候获得窗口当前的位置
                    origin.x = e.getX();
                    origin.y = e.getY();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter()
            {
                public void mouseDragged(MouseEvent e)
                {
                    // 当鼠标拖动时获取窗口当前位置
                    Point p = InitialDappFrame.this.getLocation();
                    // 设置窗口的位置
                    InitialDappFrame.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
                            - origin.y);
                }
            });
        }
    }

    public static enum InitDappSteps{
        setIpfs,setDapp;
    }

    /**
     * 居中设置
     */
    private void centerScreen(){
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - W) / 2,
                (tk.getScreenSize().height - H) / 2);
    }
    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/16
     * @Description  :
     * 
     */
    public static InitialDappFrame getContext() {
        return context;
    }

    /**
     * @author      : lanbery
     * @Datetime    : 2018/10/17
     * @Description  :
     * 
     */
    public void showStep(InitDappSteps steps){
        switch (steps){
            case setIpfs:
                titleLabel.setText(Launcher.appSettings.getConfigVolme("dapp.initStepIpfs.frame.title","NBS Host 设置"));
                break;
            case setDapp:
                titleLabel.setText(Launcher.appSettings.getConfigVolme("dapp.initStepBase.frame.title","欢迎加入NBS Chain，请设置信息"));
                break;
        }
        titleLabel.updateUI();
        cardLayout.show(contentPanel,steps.name());
    }

    public DappIPFSStepPanel getIpfsStepPanel() {
        return ipfsStepPanel;
    }

    public DappBaseStepPanel getBaseStepPanel() {
        return baseStepPanel;
    }
}
