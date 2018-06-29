package io.ipfs.nbs.ui.frames;

import com.nbs.biz.service.PeerLoginService;
import com.nbs.biz.service.TableService;
import com.nbs.ui.components.VerticalFlowLayout;
import com.nbs.ui.listener.AbstractMouseListener;
import io.ipfs.api.IPFS;
import io.ipfs.api.JSONParser;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.nbs.Launcher;
import io.ipfs.nbs.cnsts.AppGlobalCnst;
import io.ipfs.nbs.cnsts.ColorCnst;
import io.ipfs.nbs.cnsts.FontUtil;
import io.ipfs.nbs.cnsts.OSUtil;
import io.ipfs.nbs.peers.PeerInfo;
import io.ipfs.nbs.ui.components.GBC;
import io.ipfs.nbs.ui.components.NBSButton;
import io.ipfs.nbs.ui.filters.ImageFileFilter;
import io.ipfs.nbs.utils.DataBaseUtil;
import io.ipfs.nbs.utils.IconUtil;
import io.ipfs.nbs.utils.RadomCharactersHelper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @Package : io.ipfs.nbs.ui.frames
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/6/29-18:34
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class InitialFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(InitialFrame.class);

    private static final int W = 550;
    private static final int H = 350;


    private IPFS ipfs;
    private static Point origin = new Point();

    private SqlSession sqlSession;
    private PeerLoginService peerLoginService;
    private TableService tableService;
    private final static String DEFAULT_NICK_PREFFIX = "NBSChain_";

    private final static String TIP = "点击头像图标或下方按钮可上传头像.";

    /**
     *
     */
    private JPanel ctrlPanel;
    private JPanel editPanel;
    private JLabel closeLabel;
    private JTextArea peerIdText;
    private JTextField nickField;
    private JPanel buttonPanel;

    private NBSButton initButton;
    private NBSButton cancleButton;
    private JLabel statusLabel;
    private JPanel statusPanel;
    private JLabel avatarLabel;


    private NBSButton avatarButton;

    private PeerInfo tempInfo;

    private JFileChooser fileChooser;

    public InitialFrame(IPFS ipfs){
        this.ipfs = ipfs;
        /**
         * first
         */
        initWorkDist();
        /**
         * initService 保持最先加载 second
         */
        initService();

        initComponents();
        initView();
        setListeners();
        centerScreen();
    }

    /**
     *
     */
    private void initComponents(){
        Dimension windowSize = new Dimension(W, H);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);


        ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new GridBagLayout());

        JPanel closePanel = new JPanel();
        closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT,10,5));
        closeLabel = new JLabel();
        closeLabel.setIcon(IconUtil.getIcon(this,"/icons/close.png"));
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closePanel.add(closeLabel);

        /**
         * 标题
         */
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,2));
        //titlePanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("欢迎加入NBS Chain，请设置信息.");
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setFont(FontUtil.getDefaultFont(18));
        titlePanel.add(titleLabel);

        ctrlPanel.add(titlePanel,
                new GBC(0,0).setWeight(6,1).setFill(GBC.HORIZONTAL).setInsets(0,0,0,0));
        ctrlPanel.add(closePanel,
                new GBC(1,0).setWeight(1,1).setFill(GBC.HORIZONTAL).setInsets(0,40,30,0));


        /**
         * 内容编辑区
         */
        editPanel = new JPanel();
        editPanel.setLayout(new GridBagLayout());


        JPanel editLeft = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                Image icon = getIconImage();
                ImageIcon imageIcon = new ImageIcon(icon);
                if(icon != null){
                   g.drawImage(icon,0,0,getWidth(),getHeight(),imageIcon.getImageObserver());
                }
                super.paintComponent(g);

            }
        };
        editLeft.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP,15,20,false,false));
        avatarLabel = new JLabel();
        avatarLabel.setIcon(IconUtil.getIcon(this,"/icons/nbs128.png"));
        avatarLabel.setPreferredSize(new Dimension(128,128));
        avatarButton = new NBSButton("上传头像",ColorCnst.MAIN_COLOR,ColorCnst.MAIN_COLOR_DARKER);
        avatarButton.setPreferredSize(new Dimension(100,25));

        editLeft.add(avatarLabel);
        editLeft.add(avatarButton);

        JPanel editRight = new JPanel();
        editRight.setLayout(new VerticalFlowLayout(VerticalFlowLayout.LEADING,10,25,false,false));



        JPanel peerLabelPanel = new JPanel();
        peerLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT,5,2));
        JLabel peerLabel = new JLabel("Peer ID:");
        peerLabel.setFont(FontUtil.getDefaultFont(15));
        peerLabel.setHorizontalAlignment(JLabel.RIGHT);
        peerLabel.setPreferredSize(new Dimension(60,35));
        peerLabelPanel.add(peerLabel);

        peerIdText = new JTextArea();
        peerIdText.setPreferredSize(new Dimension(245,35));
        peerIdText.setFont(FontUtil.getDefaultFont(13));
        peerIdText.setForeground(ColorCnst.FONT_GRAY);
        peerIdText.setLineWrap(true);
        peerIdText.setEditable(false);

        JPanel nickPanel = new JPanel();
        nickPanel.setLayout(new FlowLayout(FlowLayout.LEFT,5,2));
        JLabel nickLabelTitle = new JLabel("昵称:");
        nickLabelTitle.setHorizontalAlignment(JLabel.RIGHT);
        nickLabelTitle.setFont(FontUtil.getDefaultFont(15));
        nickLabelTitle.setPreferredSize(new Dimension(60,30));

        nickField = new JTextField();
        nickField.setForeground(ColorCnst.FONT_GRAY_DARKER);
        nickField.setHorizontalAlignment(JLabel.LEFT);
        nickField.setFont(FontUtil.getDefaultFont(15));
        nickField.setPreferredSize(new Dimension(245,30));

        nickPanel.add(nickLabelTitle);
        nickPanel.add(nickField);

        peerLabelPanel.add(peerIdText);
        editRight.add(peerLabelPanel);

        editRight.add(nickPanel);

        /**
         * 放置编辑
         */
        editPanel.add(editLeft,
                new GBC(0,0).setWeight(1,1).setFill(GBC.BOTH).setInsets(0,10,0,0));

        editPanel.add(editRight,
                new GBC(1,0).setWeight(7,1).setFill(GBC.BOTH).setInsets(0,0,0,10));

        /**
         * 按钮区
         * 2行
         */
        statusPanel = new JPanel();
        statusLabel = new JLabel();
        statusLabel.setFont(FontUtil.getDefaultFont(14));
        statusLabel.setForeground(ColorCnst.RED);
        statusLabel.setVisible(true);
        statusPanel.add(statusLabel);

        buttonPanel = new JPanel();
        initButton = new NBSButton("保存",ColorCnst.MAIN_COLOR,ColorCnst.MAIN_COLOR_DARKER);
        initButton.setFont(FontUtil.getDefaultFont(14));
        initButton.setPreferredSize(new Dimension(115,35));

        cancleButton = new NBSButton("关闭",ColorCnst.FONT_GRAY_DARKER,ColorCnst.DARK);
        cancleButton.setFont(FontUtil.getDefaultFont(14));
        cancleButton.setPreferredSize(new Dimension(115,35));


    }


    private void initView(){
        //frame
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new LineBorder(ColorCnst.LIGHT_GRAY));
        contentPanel.setLayout(new GridBagLayout());

        //添加顶部操作
        if(OSUtil.getOsType() != OSUtil.Mac_OS){
            setUndecorated(true);
            contentPanel.add(ctrlPanel,
                    new GBC(0,0).setFill(GBC.HORIZONTAL).setWeight(1,1).setInsets(0,0,10,0));
        }


        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,5));
        buttonPanel.add(initButton);
        buttonPanel.add(cancleButton);

        //
        if(tempInfo!=null){
            String pid = tempInfo.getId()==null ? "" :tempInfo.getId();
            peerIdText.setText(pid);
            if( tempInfo.getNick()!=null)nickField.setText(tempInfo.getNick());
        }


        //statusLabel.setText("IPFS 初始化错误.");

        add(contentPanel);
       // setTitle("欢迎加入NBS Chain，请设置信息.");

        /**
         * fill
         */
        contentPanel.add(editPanel,
                new GBC(0,1).setWeight(1,6).setFill(GBC.BOTH).setInsets(0,0,0,0));
        contentPanel.add(statusPanel,
                new GBC(0,2).setWeight(1,1).setFill(GBC.BOTH).setInsets(5,0,0,0));
        contentPanel.add(buttonPanel,
                new GBC(0,3).setWeight(1,1).setFill(GBC.BOTH).setInsets(5,0,10,0));

    }

    private void setListeners(){
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(1);
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setBackground(ColorCnst.LIGHT_GRAY);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setBackground(ColorCnst.WINDOW_BACKGROUND);
                super.mouseExited(e);
            }
        });

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
                    Point p = InitialFrame.this.getLocation();
                    // 设置窗口的位置
                    InitialFrame.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
                            - origin.y);
                }
            });
        }

        cancleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        /**
         * 保存按钮
         */
        initButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /**
                 * 1.ipfs config
                 *
                 */
            }
        });

        avatarLabel.addMouseListener(new AbstractMouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                uploadAvatar();
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(TIP);
                statusLabel.setVisible(true);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setVisible(false);
                super.mouseExited(e);
            }
        });

        avatarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadAvatar();
            }
        });
    }

    /**
     *
     */
    private void initService(){
        sqlSession = DataBaseUtil.getSqlSession();
        peerLoginService = new PeerLoginService(sqlSession);
        tableService = new TableService(sqlSession);
        tableService.initClientDB();

        tempInfo = new PeerInfo();
        //获取fromid
        try {
            Map peerMap = ipfs.id();
            String randomNick = RadomCharactersHelper.getInstance().generated(DEFAULT_NICK_PREFFIX,6);
            tempInfo.setNick(randomNick);
            if(peerMap.containsKey("ID")){
                String peerId = peerMap.get("ID").toString();
                tempInfo.setId(peerId);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        getFromid(tempInfo);
    }

    private void getFromid(PeerInfo info){
        String tmpTopic = RadomCharactersHelper.getInstance().generated(8);

        try {
            Stream<Map<String,Object>> subs = ipfs.pubsub.sub(tmpTopic);
            logger.info("get from id topic {}",tmpTopic);
            ipfs.pubsub.pub(tmpTopic,info.getId());
            ipfs.pubsub.pub(tmpTopic,info.getId());

            List<Map<String, Object>> lst = subs.limit(1).collect(Collectors.toList());
            Object fromidObj = JSONParser.getValue(lst.get(0),"from");
            if(fromidObj!=null){
                info.setFrom((String)fromidObj);
            }
        } catch (Exception e) {
            logger.error("获取消息失败,{}",e.getMessage());
        }
    }

    private void centerScreen()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - W) / 2,
                (tk.getScreenSize().height - H) / 2);
    }


    private void initWorkDist(){
        /**
         *
         */
         String nbsWorkBase = Launcher.CURRENT_DIR+Launcher.FILE_SEPARATOR + AppGlobalCnst.NBS_ROOT;
         String tempBase = Launcher.CURRENT_DIR+Launcher.FILE_SEPARATOR + AppGlobalCnst.TEMP_FILE;
         String avatarCache = AppGlobalCnst.getAvatarPath();
         File tmpFile = new File(tempBase);
         if(!tmpFile.exists())tmpFile.mkdirs();
         File avatarFile = new File(avatarCache);
         if(!avatarFile.exists())avatarFile.mkdirs();
    }
    /**
     * 上传头像
     */
    private void uploadAvatar(){
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showDialog(this,"选择图片");
       // fileChooser.setFileFilter(new ImageFileFilter());
        File file = fileChooser.getSelectedFile();
        if(file!=null){
            new Thread(()->{
                List<MerkleNode> nodes;
                NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file);
                FileOutputStream fos = null;
                try {
                    nodes = ipfs.add(fileWrapper);
                    String fileHash = nodes.get(0).hash.toBase58();
                    String name = file.getName();
                    tempInfo.setAvatar(fileHash);
                    tempInfo.setAvatarSuffix(name.substring(name.lastIndexOf(".")));
                    //TODO 存数据库upload
                    byte[] bytes = ipfs.get(nodes.get(0).hash);
                    String avatarFileName = fileHash+ name.substring(name.lastIndexOf("."));

                    File avatarFile = new File(AppGlobalCnst.getAvatarPath(),avatarFileName);
                    if(avatarFile!=null){
                        avatarFile.delete();
                    }
                    avatarFile.createNewFile();

                    fos = new FileOutputStream(avatarFile);
                    fos.write(bytes);
                    fos.flush();
                    fos.close();
                    //图片压缩TODO
                    URL url = new URL("http://127.0.0.1:8080/ipfs/"+fileHash);
                    ImageIcon icon = new ImageIcon(url) ;
                    avatarLabel.setIcon(icon);
                    avatarLabel.updateUI();
                } catch (IOException e) {
                   logger.error("上传失败：{}",e.getMessage());
                   statusLabel.setText(e.getMessage());
                   statusPanel.setVisible(true);
                }
            }).start();
        }
    }
}
