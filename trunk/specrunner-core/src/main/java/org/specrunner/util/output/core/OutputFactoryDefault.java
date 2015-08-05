package org.specrunner.util.output.core;

import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;

/**
 * Default implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class OutputFactoryDefault implements IOutputFactory {

    /**
     * The current output.
     */
    private IOutput output;

    @Override
    public IOutput newOutput() {
        output = new OutputSysout();
        return output;
    }

    @Override
    public IOutput currentOutput() {
        if (output == null) {
            output = newOutput();
        }
        return output;
    }
}
