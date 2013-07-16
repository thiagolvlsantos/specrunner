package example.sql;

import nu.xom.Serializer;

import org.specrunner.SpecRunnerServices;
import org.specrunner.source.IEncoded;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;

public class TestExcel {

    public static void main(String[] args) throws Exception {
        ISourceFactory sf = SpecRunnerServices.get(ISourceFactory.class);
        ISource s = sf.newSource("src/test/java/example/sql/conteudo.xlsx");
        Serializer out = new Serializer(System.out, IEncoded.DEFAULT_ENCODING);
        out.setIndent(4);
        out.write(s.getDocument());
    }
}