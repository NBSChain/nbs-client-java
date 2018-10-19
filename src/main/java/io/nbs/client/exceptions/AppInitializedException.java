package io.nbs.client.exceptions;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project nbs-client4j
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/19
 */
public class AppInitializedException extends Exception {
    private final static String msg = "App bootstrap config error.";

    public AppInitializedException() {
        super(msg);
    }

    public AppInitializedException(String message) {
        super(message);
    }

    public AppInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppInitializedException(Throwable cause) {
        super(cause);
    }

    public AppInitializedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
