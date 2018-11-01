package io.nbs.sdk.beans;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project ipfs-mm
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/17
 */
public class NodeBase {
    private String ID;
    private String PublicKey;
    private String[] Addresses;
    private String AgentVersion;
    private String ProtocolVersion;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPublicKey() {
        return PublicKey;
    }

    public void setPublicKey(String publicKey) {
        PublicKey = publicKey;
    }

    public String[] getAddresses() {
        return Addresses;
    }

    public void setAddresses(String[] addresses) {
        Addresses = addresses;
    }

    public String getAgentVersion() {
        return AgentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        AgentVersion = agentVersion;
    }

    public String getProtocolVersion() {
        return ProtocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        ProtocolVersion = protocolVersion;
    }
}
