package io.ipfs.api.exceptions;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/25
 */
public class FileFormatUnSupportException extends Exception {
    private final static String MSG = "Unresolvable file format.";

    public FileFormatUnSupportException() {
        super(MSG);
    }

    public FileFormatUnSupportException(String message) {
        super(message);
    }

    public FileFormatUnSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileFormatUnSupportException(Throwable cause) {
        super(cause);
    }

    protected FileFormatUnSupportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
