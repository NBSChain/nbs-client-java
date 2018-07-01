package io.ipfs.nbs.ui.panels.info;

import com.nbs.ipfs.IPFSHelper;
import com.nbs.ui.listener.AbstractMouseListener;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.nbs.Launcher;
import io.ipfs.nbs.cnsts.AppGlobalCnst;
import io.ipfs.nbs.cnsts.ColorCnst;
import io.ipfs.nbs.cnsts.FontUtil;
import io.ipfs.nbs.helper.AvatarImageHandler;
import io.ipfs.nbs.helper.ConfigurationHelper;
import io.ipfs.nbs.peers.PeerInfo;
import io.ipfs.nbs.ui.components.GBC;
import io.ipfs.nbs.ui.components.LCJlabel;
import io.ipfs.nbs.ui.frames.MainFrame;
import io.ipfs.nbs.ui.panels.ParentAvailablePanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @Package : io.ipfs.nbs.ui.panels.info
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/7/1-10:58
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class InfoHeaderPanel extends ParentAvailablePanel {

    private JLabel avatarLabel;

    private LCJlabel nickLabel;
    private LCJlabel locationLabel;
    private LCJlabel peerIDPanel;
    private LCJlabel peerIDLabel;

    private PeerInfo self;
    private static InfoHeaderPanel context;
    private JFileChooser fileChooser;
    private AvatarImageHandler imageHandler ;
    private IPFS ipfs;

    public InfoHeaderPanel(JPanel parent) {
        super(parent);
        context =this;
        ipfs = IPFSHelper.getInstance().getIpfs();
        self = MainFrame.getContext().getCurrentPeer();
        imageHandler = AvatarImageHandler.getInstance();
        initComponents();
        initView();

        setListeners();

    }

    /**
     *
     */
    private void initComponents(){
        avatarLabel = new JLabel();
        nickLabel = new LCJlabel(ColorCnst.FONT_GRAY_DARKER);
        nickLabel.setFont(FontUtil.getDefaultFont(20));

        locationLabel = new LCJlabel();
        locationLabel.setFont(FontUtil.getDefaultFont(14));
        peerIDPanel = new LCJlabel();
        peerIDLabel = new LCJlabel();
        peerIDLabel.setFont(FontUtil.getDefaultFont(12));
    }

    /**
     *
     */
    private void initView(){
        setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();


        /**
         * avatar
         */
        String avatar128Path = AppGlobalCnst.consturactPath(AvatarImageHandler.getAvatarProfileHome(),self.getId()+self.getAvatarSuffix());
        ImageIcon avatar = new ImageIcon(avatar128Path);
        avatarLabel.setIcon(avatar);

        leftPanel.setPreferredSize(new Dimension(150,150));
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);
        leftPanel.add(avatarLabel);

        //right begin
        rightPanel.setLayout(new GridBagLayout());

        if(self!=null&& StringUtils.isNotBlank(self.getNick()))nickLabel.setText(self.getNick());

        nickLabel.setHorizontalAlignment(JLabel.LEFT);

        locationLabel.setText("中国 北京 海淀区");
        locationLabel.setHorizontalAlignment(JLabel.LEFT);

        JLabel peerIDTtile = new LCJlabel("Peer ID :");
        peerIDTtile.setHorizontalAlignment(JLabel.LEFT);

        peerIDTtile.setFont(FontUtil.getDefaultFont(12));

        //QmVJECTorWRbZAVnHeB2jpNnyUhfNAFJtWg8NSVRiAnrr5  测试用
        if(self!=null)peerIDLabel.setText(self.getId());

        peerIDPanel.setLayout(new FlowLayout(FlowLayout.LEFT,2,2));
        peerIDPanel.add(peerIDTtile);
        peerIDPanel.add(peerIDLabel);

        rightPanel.add(nickLabel,
                new GBC(0,0).setFill(GBC.HORIZONTAL).setWeight(1,10).setInsets(0,15,0,0)
        );
        rightPanel.add(locationLabel,
                new GBC(0,1).setFill(GBC.HORIZONTAL).setWeight(1,9).setInsets(0,15,0,0)
        );
        rightPanel.add(peerIDPanel,
                new GBC(0,2).setFill(GBC.BOTH).setWeight(1,8).setInsets(0,15,5,0)
        );

        add(leftPanel,BorderLayout.WEST);
        add(rightPanel,BorderLayout.CENTER);

    }

    private void setListeners(){
        avatarLabel.addMouseListener(new AbstractMouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                uploadAvatar();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                avatarLabel.setCursor(MainFrame.handCursor);
                avatarLabel.setToolTipText("点击修改头像");
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
            }
        });

        nickLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String oriNick = nickLabel.getText();
                String upText = JOptionPane.showInputDialog(context,"请输入新的昵称","修改昵称",JOptionPane.INFORMATION_MESSAGE);
                if(StringUtils.isBlank(upText)||upText.trim().equals(oriNick))return;

                upText = upText.trim();
                IPFS ipfs = Launcher.getContext().getIpfs();
                if(ipfs==null)return;
                try {
                    ipfs.config.set(ConfigurationHelper.JSON_NICKNAME_KEY,upText);
                    MainFrame.getContext().getCurrentPeer().setNick(upText);
                    nickLabel.setText(upText);

                    //TODO 存库
                } catch (IOException ioe) {
                    logger.error("更新 IPFS config error :{}",ioe.getMessage());
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                nickLabel.setToolTipText("点击修改昵称");
                nickLabel.setCursor(MainFrame.handCursor);
                super.mouseEntered(e);
            }
        });
    }

    /**
     *
     */
    private void uploadAvatar(){
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showDialog(this,"选择图片");
        File file = fileChooser.getSelectedFile();
        if(file!=null) {
            String name = file.getName();//源文件名
            String avatarPeerName = self.getId() + name.substring(name.lastIndexOf("."));
            new Thread(()->{
                List<MerkleNode> nodes;

                FileOutputStream fos = null;
                try {
                    //上传前先压缩
                    imageHandler.createdAvatar4Profile(file,avatarPeerName);
                    File file128 = new File(AppGlobalCnst.consturactPath(AvatarImageHandler.getAvatarProfileHome(),avatarPeerName));
                    NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file128);

                    nodes = ipfs.add(fileWrapper);
                    String fileHash = nodes.get(0).hash.toBase58();

                    self.setAvatar(fileHash);
                    self.setAvatarSuffix(name.substring(name.lastIndexOf(".")));

                    ipfs.config.set(ConfigurationHelper.JSON_AVATAR_KEY,fileHash);
                    ipfs.config.set(ConfigurationHelper.JSON_AVATAR_SUFFIX_KEY,self.getAvatarSuffix());
                    ipfs.config.set(ConfigurationHelper.JSON_AVATAR_NAME_KEY,name);

                    //TODO 存数据库upload
                    String avatarFileName = fileHash+ name.substring(name.lastIndexOf("."));
                    try {
                        imageHandler.createContactsAvatar(file,avatarFileName);
                        ImageIcon icon = new ImageIcon(AppGlobalCnst.consturactPath(AvatarImageHandler.getAvatarProfileHome(),avatarPeerName));
                        if(icon!=null){
                            logger.info(fileHash);
                            avatarLabel.setIcon(icon);
                            avatarLabel.updateUI();
                            MainFrame.getContext().refreshAvatar();
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                        return;
                    }
                } catch (Exception e) {
                    logger.error("上传失败：{}",e.getMessage());
                    JOptionPane.showMessageDialog(context,"上传失败");
                    return;
                }
            }).start();
        }
    }

    /**
     * 更新数据库
     */
    private void updatePeers(){

    }
}
