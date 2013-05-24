package org.specrunner.source;

import org.junit.Test;

public class TestString extends TstPai {

    @Test
    public void rodarTeste() throws Exception {
        runFile("string.html");
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            new TestSearch().rodarTeste();
        }
    }
}
