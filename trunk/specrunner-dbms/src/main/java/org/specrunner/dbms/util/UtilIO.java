package org.specrunner.dbms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.dbms.BaseComparator;

public class UtilIO {

    @SuppressWarnings("unchecked")
    public static <T> List<T> load(Class<T> type, String file) {
        List<T> result = new LinkedList<T>();
        InputStream in = null;
        BufferedReader br = null;
        InputStreamReader fr = null;
        try {
            File f = new File(file);
            if (!f.exists()) {
                in = BaseComparator.class.getResourceAsStream(file);
                if (in == null) {
                    throw new RuntimeException("Invalid configuration file (" + type + "): " + file);
                }
            } else {
                in = new FileInputStream(f);
            }
            fr = new InputStreamReader(in);
            br = new BufferedReader(fr);
            String input;
            while ((input = br.readLine()) != null) {
                result.add((T) Class.forName(input.trim()).newInstance());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }
}