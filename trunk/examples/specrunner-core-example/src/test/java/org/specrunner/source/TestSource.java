package org.specrunner.source;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.specrunner.SpecRunnerException;
import org.specrunner.source.core.SourceFactoryHtml;

public class TestSource {

    private ISourceFactory factory;
    private static final String FILE = "src/test/resources/sources/source.html";
    private static final String FILE_REL = "../other.html";
    private static final String URI = "http://www.globo.com";
    private ISource sourceFile;
    private ISource sourceUri;

    @Before
    public void before() throws SpecRunnerException {
        factory = new SourceFactoryHtml();
        sourceFile = factory.newSource(FILE);
        sourceUri = factory.newSource(URI);
    }

    @Test
    public void testFile() throws SpecRunnerException {
        File file = new File(FILE).getAbsoluteFile();
        URI uri = file.toURI();
        Assert.assertEquals(new File(FILE).toURI().toString(), sourceFile.getString());
        Assert.assertEquals(file.getAbsolutePath(), sourceFile.getFile().getAbsolutePath());
        Assert.assertEquals(uri, sourceFile.getURI());
    }

    @Test
    public void testSourceXML() throws SpecRunnerException {
        // Document doc = sourceFile.getDocument();
        // Assert.assertEquals(doc.toXML(),
        // "<?xml version=\"1.0\"?>\n<html><head xmlns=\"http://www.w3.org/1999/xhtml\" /><body xmlns=\"http://www.w3.org/1999/xhtml\">Conteúdo.</body></html>\n");
    }

    @Test
    public void testURI() throws SpecRunnerException, URISyntaxException {
        Assert.assertEquals(URI, sourceUri.getString());
        Assert.assertEquals(null, sourceUri.getFile());
        Assert.assertEquals(new URI(URI), sourceUri.getURI());
    }

    @Test
    public void testREL() throws SpecRunnerException {
        ISource rel = factory.newSource(FILE_REL);
        ISource resolved = sourceFile.resolve(rel);
        Assert.assertEquals(new File(new File(FILE).getParentFile().getParentFile(), new File(FILE_REL).getName()).toURI().toString(), resolved.getString());
    }

    @Test
    public void testEquals() throws SpecRunnerException {
        ISource outro = factory.newSource(FILE);
        Assert.assertTrue(outro.equals(sourceFile));
    }

    @Test
    public void testNotEquals() {
        Assert.assertFalse(sourceUri.equals(sourceFile));
    }

    public static void main(String[] args) throws Exception {
        URI u = new URI(FILE);
        System.out.println(u);
        System.out.println(u.resolve(new URI(FILE_REL)));
        System.out.println(u.relativize(new URI(FILE + "ok/other.html")));

        u = new URI(URI);
        System.out.println(u);
        System.out.println(u.resolve(new URI(FILE_REL)));
        System.out.println(u.relativize(new URI(FILE + "ok/other.html")));

    }
}
