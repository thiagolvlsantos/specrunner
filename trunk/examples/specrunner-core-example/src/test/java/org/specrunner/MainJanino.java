package org.specrunner;

import java.io.Reader;
import java.io.StringReader;

public final class MainJanino {

    private MainJanino() {
    }

    public static void main(String[] args) {
        try {
            Reader rd = new StringReader("txtContent.indexOf(\"value\")");
            String[] vars = org.codehaus.janino.ExpressionEvaluator.guessParameterNames(new org.codehaus.janino.Scanner(null, rd));
            for (int i = 0; i < vars.length; i++) {
                System.out.println(i + "." + vars[i]);
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
