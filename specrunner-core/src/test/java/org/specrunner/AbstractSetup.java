package org.specrunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AbstractSetup {

	public static int serial = 0;

    protected static void adjust(String gap, File file, FileFilter filter, String from, String to) throws IOException {
        if (file.isDirectory()) {
			File[] sub = file.listFiles(filter);
            if (sub != null) {
                for (int i = 0; i < sub.length; i++) {
                    adjust("\t" + gap, sub[i], filter, from, to);
                }
            }
        } else {
            System.out.println(gap + "Replace: " + file);
            File tmp = new File(file.getParentFile(), file.getName() + ".tmp");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
            String input;
            while ((input = reader.readLine()) != null) {
                if (input.trim().contains(from)) {
                    System.out.println(gap + "\tFROM(" + (++serial) + "): " + from + " to " + to);
                    writer.write(input.replace(from, to));
                } else {
                    writer.write(input);
                }
                writer.write('\n');
            }
            writer.close();
            reader.close();
            if (!file.delete()) {
                System.out.println("Not removed " + file);
            }
            if (!tmp.renameTo(file)) {
                System.out.println("Not renamed from " + tmp + " to " + file);
            }
        }
    }
}
