package org.specrunner.source;

import org.junit.Test;

public class TestCustom extends TstPai {

    @Test
    public void rodarTeste() throws Exception {
        runFile("custom.html");
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            new TestCustom().rodarTeste();
        }
    }
}
