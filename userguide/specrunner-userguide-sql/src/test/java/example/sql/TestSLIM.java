package example.sql;

import org.junit.runner.RunWith;
import org.specrunner.context.IBlock;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunner;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;

@ExpectedMessages(messages = { "tu encontrado.", "nós encontrado" })
@RunWith(SRRunner.class)
public class TestSLIM {

    private long x;
    private double y;
    private double resultado;

    public int constante() {
        return 2;
    }

    public void x(long x) {
        this.x = x;
    }

    public void y(double y) {
        this.y = y;
    }

    public void z(double y) {
        this.y = y;
    }

    public void operacao(String op) {
        switch (op.charAt(0)) {
        case '+':
            resultado = x + y;
            break;
        case '-':
            resultado = x - y;
            break;
        case '*':
            resultado = x * y;
            break;
        case '/':
            resultado = x / y;
            break;
        case '%':
            resultado = x % y;
            break;
        }
        x = 0;
        y = 0;
    }

    public double resultado() {
        return resultado;
    }

    public double resultado(Object content) {
        System.out.println("CONTEÚDO:" + content);
        return resultado;
    }

    public void erro(IBlock block, IResultSet result) {
        result.addResult(Failure.INSTANCE, block, "eu encontrado.");
        result.addResult(Failure.INSTANCE, block, "tu encontrado.");
        result.addResult(Failure.INSTANCE, block, "ele encontrado.");
    }
}
