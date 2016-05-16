package org.specrunner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

//CHECKSTYLE:OFF
public class CopyrightSetup extends AbstractSetup {

    public static void main(String[] args) throws IOException {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getAbsolutePath().contains("target" + File.separator)) {
                    return false;
                }
                String name = pathname.getName();
                return pathname.isDirectory() || name.endsWith(".java") || name.endsWith(".properties") || name.endsWith(".txt");
            }
        };
        adjust("", new File(System.getProperty("user.dir") + "/.."), filter, "Copyright (C) 2011-2016  Thiago Santos", "Copyright (C) 2011-2016  Thiago Santos");
    }
    // CHECKSTYLE:ON
}
