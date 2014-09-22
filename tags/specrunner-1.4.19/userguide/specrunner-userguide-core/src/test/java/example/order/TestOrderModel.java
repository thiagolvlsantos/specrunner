package example.order;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.expressions.IExpression;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.expressions.IExpressionItem;
import org.specrunner.expressions.core.ExpressionItemModel;
import org.specrunner.expressions.core.ExpressionItemVar;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestOrderModel {

    @Configuration
    public void configure(IConfiguration cfg) {
        cfg.add(IExpression.FEATURE_PRECEDENCE, new IExpressionItem[] { ExpressionItemModel.get(), ExpressionItemVar.get() });
    }

    @Before
    public void before() {
        IExpressionFactory factory = SRServices.getExpressionFactory();
        factory.bindModel("var", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return "model";
            }
        });
    }
}