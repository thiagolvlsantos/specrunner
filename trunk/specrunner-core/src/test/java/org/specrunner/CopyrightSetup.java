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
                return pathname.isDirectory() || pathname.getName().endsWith(".java") || pathname.getName().endsWith(".properties");
            }
        };
        adjust("", new File(System.getProperty("user.dir") + "/.."), filter, "Copyright (C) 2011-2014  Thiago Santos", "Copyright (C) 2011-2015  Thiago Santos");
    }
    // CHECKSTYLE:ON
}