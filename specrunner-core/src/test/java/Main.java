
import org.joda.time.DateTime;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.context.core.ContextImpl;
import org.specrunner.util.expression.IPlaceholder;
import org.specrunner.util.expression.IProcessor;
import org.specrunner.util.expression.ITextAnalyzer;
import org.specrunner.util.expression.core.PlaceholderDefault;
import org.specrunner.util.expression.core.ProcessorDefault;
import org.specrunner.util.expression.core.TextAnalyzerDefault;

public class Main {

    public static void main(String[] args) throws Exception {
        IProcessor e = new ProcessorDefault();
        IPlaceholder t = new PlaceholderDefault();
        ITextAnalyzer te = new TextAnalyzerDefault();
        IContext ctx = new ContextImpl(null, null);
        SRServices.getExpressionFactory().bindClass("data", DateTime.class);
        System.out.println(te.replace("Text simples.", t, e, ctx, true));
        System.out.println(te.replace("Nome: ${data} qualquer valor ${fica} indefinidamente.", t, e, ctx, true));
        System.out.println(te.replace("${data} qualquer \\${data} cache ${data}.", t, e, ctx, true));
        System.out.println("'" + te.evaluate("", t, e, ctx, true) + "'");
        SRServices.getExpressionFactory().bindValue("n", 1);
        System.out.println("'" + te.evaluate("2 + ${n}", t, e, ctx, true) + "'");

        System.out.println("'" + te.evaluate("2 + {n} + 4", t, e, ctx, true) + "'");

        System.out.println("'" + te.evaluate("2 + n} + 4", t, e, ctx, true) + "'");

        System.out.println("'" + te.evaluate("2 + {n + 4", t, e, ctx, true) + "'");
    }
}
