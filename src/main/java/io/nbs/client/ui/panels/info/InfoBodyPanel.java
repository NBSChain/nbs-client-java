package io.nbs.client.ui.panels.info;

import io.ipfs.IpfsCnst;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.nbs.helper.IPAddressHelper;
import io.nbs.client.Launcher;
import io.nbs.client.cnsts.AppGlobalCnst;
import io.nbs.client.cnsts.ColorCnst;
import io.nbs.client.cnsts.FontUtil;
import io.nbs.client.helper.AvatarImageHandler;
import io.nbs.client.listener.AbstractMouseListener;
import io.nbs.client.ui.components.GBC;
import io.nbs.client.ui.components.LCJlabel;
import io.nbs.client.ui.components.VerticalFlowLayout;
import io.nbs.client.ui.frames.MainFrame;
import io.nbs.client.ui.panels.ParentAvailablePanel;

import io.nbs.commons.utils.IconUtil;
import io.nbs.sdk.beans.PeerInfo;
import io.nbs.sdk.constants.ConfigKeys;
import io.nbs.sdk.prot.IPMParser;
import org.apache.commons.lang3.StringUtils;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @Package : io.ipfs.nbs.ui.panels.info
 * @Description : <p></p>
 * @Author : lambor.c
 * @Date : 2018/7/1-11:00
 * Copyright (c) 2018, NBS , lambor.c<lanbery@gmail.com>.
 * All rights reserved.
 */
public class InfoBodyPanel extends ParentAvailablePanel {

    private static InfoBodyPanel context;
    private JPanel avatarJPanel;
    private JLabel avatarLabel;
    private LCJlabel nickLabel;
    private LCJlabel locationsLabel;
    private JPanel peerPanel;
    private LCJlabel peerIDLabel;
    private JTextField peerIdField;
    private JFileChooser fileChooser;
    private IPFS ipfs;
    private AvatarImageHandler imageHandler ;

    public InfoBodyPanel(JPanel parent) {
        super(parent);
        context = this;
        ipfs = Launcher.getContext().getIpfs();
        imageHandler = AvatarImageHandler.getInstance();
        initComponents();
        initView();
        setListeners();
        setLoaction();
    }

    /**
     *
     */
    private void initComponents(){
        avatarJPanel = new JPanel();
        avatarLabel = new JLabel();
        PeerInfo current = getCurrent();
        String avatarName = current.getAvatarName();
        ImageIcon avatarIcon;
        avatarIcon = IconUtil.getIcon(this,"/images/nbs750.jpg",128,128);

        avatarLabel.setIcon(avatarIcon);
        avatarLabel.setBackground(ColorCnst.WINDOW_BACKGROUND_LIGHT);

        //nick
        String nick = current.getNick()==null? current.getId() : current.getNick();
        nickLabel = new LCJlabel(nick);
        nickLabel.setFont(FontUtil.getDefaultFont(30));
        nickLabel.setHorizontalAlignment(JLabel.CENTER);
        //locations
        String locations = current.getLocations();
        if(StringUtils.isBlank(locations))locations = current.getIp()==null ? "" : current.getIp();
        locationsLabel = new LCJlabel(locations);
        locationsLabel.setFont(FontUtil.getDefaultFont(13));
        locationsLabel.setHorizontalAlignment(JLabel.CENTER);

        //peer
        peerPanel = new JPanel();
        peerIDLabel = new LCJlabel("Peer ID :");
        peerIDLabel.setHorizontalAlignment(JLabel.RIGHT);
        peerIDLabel.setFont(FontUtil.getDefaultFont(13));
        peerIdField = new JTextField(current.getId());
        peerIdField.setBorder(null);
        peerIdField.setHorizontalAlignment(JTextField.LEFT);
        peerIdField.setEditable(false);
        peerIdField.setBackground(ColorCnst.WINDOW_BACKGROUND);
    }

    private void syncLoadAvatar(PeerInfo peer){
        AvatarImageHandler imageHandler = AvatarImageHandler.getInstance();
        if(StringUtils.isNotBlank(peer.getAvatar())&&ipfs!=null){
            new Thread(){
                @Override
                public void run() {
                    try{
                        String urlPath = Launcher.appSettings.getGatewayURL(peer.getAvatar());
                        URL url = new URL(urlPath);
                        String path = AppGlobalCnst.consturactPath(AvatarImageHandler.getAvatarProfileHome(),peer.getAvatar()+AvatarImageHandler.AVATAR_SUFFIX);
                        File avatarFile = new File(path);
                        boolean b =false;
                        if(avatarFile.isFile()&&avatarFile.exists()){
                            b = true;
                        }else {
                            b = imageHandler.getFileFromIPFS(url,avatarFile);
                        }
                        if(b){
                            ImageIcon icon = imageHandler.getImageIconFromOrigin(avatarFile,128);
                            avatarLabel.setIcon(icon);
                            avatarJPanel.updateUI();
                            ImageIcon icon48 = imageHandler.getImageIconFromOrigin(avatarFile,48);
                            MainFrame.getContext().refreshAvatar(icon48);
                        }
                    }catch (Exception e){

                    }
                }
            }.start();
        }
    }

    private void initView(){
        setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP,0,10,true,false));
        /*=====================================================*/
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);

        peerPanel.setLayout(new GridBagLayout());
        JLabel placeHolder = new JLabel();
        placeHolder.setMinimumSize(new Dimension(100,50));
        add(placeHolder);
        add(avatarLabel);
        peerPanel.add(peerIDLabel
        ,new GBC(0,0).setWeight(1,1).setFill(GBC.BOTH).setInsets(0,0,0,0));
        peerPanel.add(peerIdField
                ,new GBC(1,0).setWeight(1,1).setFill(GBC.BOTH).setInsets(0,10,0,0));

        add(avatarLabel);
               // new GBC(0,0).setWeight(1,5).setFill(GBC.HORIZONTAL).setInsets(0,0,0,0));
        add(nickLabel);
              //  new GBC(0,1).setWeight(1,2).setFill(GBC.HORIZONTAL).setInsets(0,0,0,0));


        add(peerPanel);
        add(locationsLabel);
        //
        syncLoadAvatar(getCurrent());
    }
    private void setListeners(){
        //头像事件
        avatarLabel.addMouseListener(new AbstractMouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                uploadAvatar();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                avatarLabel.setCursor(AppGlobalCnst.HAND_CURSOR);
                avatarLabel.setToolTipText("点击修改头像");
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
            }
        });

        //修改昵称
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
                    String enUpText = IPMParser.urlEncode(upText);
                    ipfs.config.set(ConfigKeys.nickname.key(),enUpText);
                    MainFrame.getContext().getCurrentPeer().setNick(upText);
                    nickLabel.setText(upText);
                } catch (IOException ioe) {
                    logger.error("更新 IPFS config error :{}",ioe.getMessage());
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                nickLabel.setToolTipText("点击修改昵称");
                nickLabel.setCursor(AppGlobalCnst.HAND_CURSOR);
                super.mouseEntered(e);
            }
        });
    }

    private PeerInfo getCurrent(){
        return Launcher.currentPeer;
    }

    /**
     *
     */
    private void uploadAvatar(){
        PeerInfo self = getCurrent();
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showDialog(this,"选择图片");
        File file = fileChooser.getSelectedFile();
        if(file!=null) {
            String name = file.getName();//源文件名

            List<MerkleNode> nodes;
            try {
                //上传前先压缩
                String originAvatarName = imageHandler.createdAvatar4Profile(file,name);
                File file128 = new File(AppGlobalCnst.consturactPath(AvatarImageHandler.getAvatarProfileHome(),originAvatarName));
                NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file128);
                //上传ipfs
                nodes = ipfs.add(fileWrapper);
                String fileHash = nodes.get(0).hash.toBase58();

                self.setAvatar(fileHash);
                self.setAvatarName(originAvatarName);
                self.setAvatarSuffix(name.substring(name.lastIndexOf(".")));

                ipfs.config.set(ConfigKeys.avatarHash.key(),fileHash);
                ipfs.config.set(ConfigKeys.avatarSuffix.key(),self.getAvatarSuffix());
                ipfs.config.set(ConfigKeys.avatarName.key(),originAvatarName);

                //TODO 存数据库upload
                /**
                 * 创建Hash 头像 :cache/avatar/custom
                 */
                String hashFileName = fileHash + AvatarImageHandler.AVATAR_SUFFIX;
                try {
                    imageHandler.createContactsAvatar(file,hashFileName);
                    //BufferedImage image = ImageIO.read(file128);
                    ImageIcon avatarIcon = imageHandler.getAvatarScaleIcon(file128,128);

                    logger.info( file128.getAbsolutePath());
                    if(avatarIcon!=null){
                        logger.info(fileHash);
                        avatarLabel.setIcon(avatarIcon);
                        avatarLabel.validate();
                        avatarLabel.updateUI();
                        //MainFrame.getContext().refreshAvatar();
                        MainFrame.getContext().refreshAvatar(imageHandler.getImageIconFromOrigin(file128,48));
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
            // new Thread(()->{ }).start();
        }
    }

    /**
     * 更新数据库
     */
    private void setLoaction(){
        IPAddressHelper addressHelper = IPAddressHelper.getInstance();
        String host = Launcher.appSettings.getHost();
        new Thread(){

            @Override
            public void run() {
                super.run();
                StringBuilder sb = new StringBuilder();
                sb.append("HOST:").append(host);
                String realIP = addressHelper.getRealIP();
                if(StringUtils.isNotBlank(realIP)){
                    sb.append("[").append(realIP).append("]");
                }
                String loacation = addressHelper.getLocations(host);
                if(StringUtils.isNotBlank(loacation)){
                    sb.append("(").append(loacation).append(")");
                }
                locationsLabel.setText(sb.toString());
                locationsLabel.setVisible(true);
                context.updateUI();
                logger.info("INFO >>>>update Host: {}",sb.toString());
            }
        }.start();
    }
}
