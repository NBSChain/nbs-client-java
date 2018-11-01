package io.nbs.client.ui.filters;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Copyright © 2015-2020 NBSChain Holdings Limited.
 * All rights reserved.
 *
 * @project ipfs-mm
 * <p>
 * Author   : lanbery
 * Created  : 2018/10/17
 */
public class AvatarImageFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        if(f.isDirectory())return true;
        String name = f.getName();
        return name.toLowerCase().endsWith(".png")||
                name.toLowerCase().endsWith(".jpg") ||
                name.toLowerCase().endsWith(".jpeg") ||
                name.toLowerCase().endsWith(".gif");
    }

    @Override
    public String getDescription() {
        return "*.png;*.jpg;*.jpeg;*.gif";
    }
}
