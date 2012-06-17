package org.specrunner.source;

import org.junit.Test;

public class TestList extends TstPai {

    @Test
    public void rodarTeste() throws Exception {
        runFile("list.html");
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            new TestList().rodarTeste();
        }
    }
}
