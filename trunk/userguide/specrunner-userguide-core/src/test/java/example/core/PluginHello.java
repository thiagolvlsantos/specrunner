package example.core;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

public class PluginHello extends AbstractPlugin {

	private int times;

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	@Override
	public void initialize(IContext context) throws PluginException {
		super.initialize(context);
	}

	@Override
	public ENext doStart(IContext context, IResultSet result)
			throws PluginException {
		result.addResult(Status.SUCCESS, context.peek());
		return ENext.DEEP;
	}

	@Override
	public void doEnd(IContext context, IResultSet result)
			throws PluginException {
		super.doEnd(context, result);
	}
}
