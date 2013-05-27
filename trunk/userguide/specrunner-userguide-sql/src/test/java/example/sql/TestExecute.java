package example.sql;

import nu.xom.Node;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestExecute {

    public void externo(Node node) {
        System.out.println("EXTERNO:" + node.toXML());
    }

    public String antes(int index, Node node, String texto) {
        System.out.println("INTERNO_ANTES(" + index + "):" + node.toXML());
        System.out.println("   TEXT_ANTES(" + index + "):" + texto);
        return texto;
    }

    public String depois(int index, Node node, String texto) {
        System.out.println("INTERNO_DEPOIS(" + index + "):" + node.toXML());
        System.out.println("   TEXT_DEPOIS(" + index + "):" + texto);
        return texto;
    }

    public void subin(String text) {
        System.out.println("\t\t SUB:" + text);
    }
}
