package example.concordion;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.transformer.ITransformer;

public class TransformerConcordion implements ITransformer {
    @Override
    public void initialize() {
    }

    @Override
    public ISource transform(ISource source) throws SourceException {
        Document doc = source.getDocument();
        Map<String, String> prefixes = new HashMap<String, String>();
        Map<String, XPathContext> namespaces = new HashMap<String, XPathContext>();
        Element root = doc.getRootElement();
        for (int i = 0; i < root.getNamespaceDeclarationCount(); i++) {
            String prefix = root.getNamespacePrefix(i);
            if (prefix.isEmpty()) {
                continue;
            }
            String uri = root.getNamespaceURI(prefix);
            prefixes.put(uri, prefix);
            namespaces.put(uri, new XPathContext(prefix, uri));
        }
        String uri = "http://www.concordion.org/2007/concordion";
        XPathContext concordion = namespaces.get(uri);
        if (concordion == null) {
            return source;
        }
        Map<String, String> variables = new HashMap<String, String>();

        Nodes ns = doc.query("//*[@" + prefixes.get(uri) + ":set]", concordion);
        for (int i = 0; i < ns.size(); i++) {
            Element e = (Element) ns.get(i);
            Attribute att = e.getAttribute("set", uri);
            String value = att.getValue();
            e.removeAttribute(att);
            e.addAttribute(new Attribute("class", "set"));
            String var = cleanVar(value);
            e.addAttribute(new Attribute("name", var));
            variables.put(var, var);
        }
        ns = doc.query("//*[@" + prefixes.get(uri) + ":assertequals]", concordion);
        for (int i = 0; i < ns.size(); i++) {
            Element e = (Element) ns.get(i);
            Attribute att = e.getAttribute("assertequals", uri);
            String value = att.getValue().trim();
            e.removeAttribute(att);
            e.addAttribute(new Attribute("class", "eq"));
            // variables start with '#', in this case $THIS should not be
            // prefixed
            e.addAttribute(new Attribute("value", (value.startsWith("#") ? "" : "$THIS.") + cleanVar(value)));
        }
        ns = doc.query("//*[@" + prefixes.get(uri) + ":execute]", concordion);
        for (int i = 0; i < ns.size(); i++) {
            Element e = (Element) ns.get(i);
            Attribute att = e.getAttribute("execute", uri);
            String value = att.getValue();
            // assignments
            if (value.contains("=")) {
                String[] split = value.split("=");
                e.addAttribute(new Attribute("name", cleanVar(split[0])));
                value = split[1];
            }
            if (!"table".equals(e.getLocalName())) {
                e.addAttribute(new Attribute("class", "execute"));
            } else {
                e.addAttribute(new Attribute("class", "executeRows"));
            }
            e.addAttribute(new Attribute("value", "$THIS." + cleanVar(value)));
            e.removeAttribute(att);
        }
        ns = doc.query("//*[@" + prefixes.get(uri) + ":verifyrows]", concordion);
        for (int i = 0; i < ns.size(); i++) {
            Element e = (Element) ns.get(i);
            Attribute att = e.getAttribute("verifyrows", uri);
            e.addAttribute(new Attribute("class", "verifyRows"));
            String value = att.getValue();
            String[] split = value.split(":");
            e.addAttribute(new Attribute("name", cleanVar(split[0])));
            value = split[1];
            e.addAttribute(new Attribute("value", "$THIS." + cleanVar(value)));
            e.removeAttribute(att);
        }
        return source;
    }

    protected String cleanVar(String value) {
        return value.replace("#TEXT", "$TEXT").replace("#", "").trim();
    }
}