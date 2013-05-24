package org.specrunner.source;

import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.Document;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;

public final class MainParser {

    private MainParser() {
    }

    public static void main(String[] args) throws Exception {
        HTMLConfiguration config = new HTMLConfiguration();
        // config.setFeature("http://xml.org/sax/features/namespaces", false);
        AbstractSAXParser neko = new AbstractSAXParser(config) {
        };
        neko.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        neko.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
        neko.setProperty("http://cyberneko.org/html/properties/default-encoding", "ISO-8859-1");
        Builder builder = new Builder(neko, true);

        Document d = builder.build(new StringReader("<li>aqui</li>"));
        System.out.println(d.toXML());
    }

}
