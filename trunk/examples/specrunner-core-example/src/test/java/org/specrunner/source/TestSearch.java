package org.specrunner.source;

import org.junit.Test;

public class TestSearch extends TstPai {

    @Test
    public void rodarTeste() throws Exception {
        runFile("search.html");
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            new TestSearch().rodarTeste();
        }
    }
}
