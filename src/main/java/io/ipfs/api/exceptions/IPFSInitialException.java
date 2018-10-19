package io.ipfs.api.exceptions;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project ipfs-mm
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/16
 */
public class IPFSInitialException extends Exception {

    public IPFSInitialException() {
        super("IPFS initialized error.");
    }

    public IPFSInitialException(String message) {
        super(message);
    }

    public IPFSInitialException(String message, Throwable cause) {
        super(message, cause);
    }

    public IPFSInitialException(Throwable cause) {
        super(cause);
    }

    public IPFSInitialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
