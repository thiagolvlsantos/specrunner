package org.specrunner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

//CHECKSTYLE:OFF
public class VersionSetup extends AbstractSetup {

    public static void main(String[] args) throws IOException {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getAbsolutePath().contains("target" + File.separator)) {
                    return false;
                }
                return pathname.isDirectory() || pathname.getName().equalsIgnoreCase("pom.xml");
            }
        };
        adjust("", new File(System.getProperty("user.dir") + "/.."), filter, "version>1.4.53</", "version>1.4.54</");
    }
    // CHECKSTYLE:ON
}
