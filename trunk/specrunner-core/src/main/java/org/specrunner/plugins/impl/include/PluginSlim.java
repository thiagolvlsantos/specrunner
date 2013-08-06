package org.specrunner.plugins.impl.include;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.parameters.IAccess;
import org.specrunner.parameters.IAccessFactory;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.plugins.impl.elements.PluginHtml;
import org.specrunner.plugins.impl.var.PluginBean;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;

public class PluginSlim extends AbstractPluginTable {

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        Object instance = getObjectInstance(context, tableAdapter);
        context.saveStrict(UtilEvaluator.asVariable(PluginBean.BEAN_NAME), instance);

        List<RowAdapter> rows = tableAdapter.getRows();
        if (rows.isEmpty()) {
            throw new PluginException("Header information missing.");
        }
        RowAdapter header = rows.get(0);
        List<String> features = extractFeatures(header);
        IAccessFactory accessFactory = SpecRunnerServices.get(IAccessFactory.class);
        List<IAccess> accesses = new LinkedList<IAccess>();
        for (String f : features) {
            IAccess ac = accessFactory.newAccess(instance, f.replace("?", ""));
            accesses.add(ac);
        }
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter r = rows.get(i);
            if (r.getCellsCount() != features.size()) {
                result.addResult(Failure.INSTANCE, context.peek(), "Number of coluns in line " + i + " is different of headers (" + features.size() + ").");
                continue;
            }
            for (int j = 0; j < features.size(); j++) {
                CellAdapter c = r.getCell(j);
                String v = c.getValue();
                Object value = UtilEvaluator.evaluate(v, context, true);
                String feature = features.get(j);
                IAccess access = accesses.get(j);
                if (feature.endsWith("?")) {
                    Object received = null;
                    CellAdapter hd = header.getCells().get(j);
                    String content = "content";
                    if ((hd.hasAttribute(content) && Boolean.parseBoolean(hd.getAttribute(content))) || (c.hasAttribute(content) && Boolean.parseBoolean(c.getAttribute(content)))) {
                        try {
                            received = access.get(instance, feature, value);
                        } catch (Exception e) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e.getMessage(), e);
                            }
                            result.addResult(Success.INSTANCE, context.newBlock(c.getElement(), this), new PluginException("Could not get(value)."));
                        }
                    } else {
                        try {
                            received = access.get(instance, feature);
                        } catch (Exception e) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e.getMessage(), e);
                            }
                            result.addResult(Success.INSTANCE, context.newBlock(c.getElement(), this), new PluginException("Could not get."));
                        }
                    }
                    UtilPlugin.compare(c.getElement(), result, value, received);
                } else {
                    try {
                        access.set(instance, feature, value);
                    } catch (Exception e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                        result.addResult(Success.INSTANCE, context.newBlock(c.getElement(), this), new PluginException("Could not set value."));
                    }
                }
            }
        }
        return ENext.DEEP;
    }

    /**
     * Get the object instance to be used by plugin actions.
     * 
     * @param context
     *            The context.
     * @param tableAdapter
     *            The adapter.
     * @return An object instance where all action will taken over.
     * @throws PluginException
     *             On creation/lookup errors.
     */
    protected Object getObjectInstance(IContext context, TableAdapter tableAdapter) throws PluginException {
        Object instance = null;
        List<CellAdapter> captions = tableAdapter.getCaptions();
        if (captions.size() > 1) {
            throw new PluginException("Table has '" + captions.size() + "' captions. Please use only one caption tag.");
        }
        if (captions.size() > 0) {
            String className = UtilString.camelCase(captions.get(0).getValue(), true);
            for (String pkg : PluginImport.getPackages(context)) {
                try {
                    instance = newInstance(Class.forName(pkg + "." + className), tableAdapter);
                    break;
                } catch (ClassNotFoundException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        if (instance == null) {
            instance = PluginHtml.getTestInstance();
        }
        return instance;
    }

    /**
     * Create a instance of the object.
     * 
     * @param type
     *            The object type.
     * @param table
     *            The source table.
     * @return An instance of type.
     * @throws PluginException
     *             On creation errors.
     */
    protected Object newInstance(Class<?> type, TableAdapter table) throws PluginException {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException("Could not create type instance.");
        } catch (IllegalAccessException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException("Could not create access type default constructor. Class:'" + type.getName() + "'");
        }
    }

    /**
     * Get the feature names from this list.
     * 
     * @param header
     *            The table rows.
     * @return The feature names based on header line.
     */
    protected List<String> extractFeatures(RowAdapter header) {
        String attribute = "feature";
        List<String> methods = new LinkedList<String>();
        for (CellAdapter h : header.getCells()) {
            String feature = null;
            String value = h.getValue();
            if (h.hasAttribute(attribute)) {
                feature = h.getAttribute(attribute);
                if (value != null && value.trim().endsWith("?")) {
                    feature = feature + "?";
                }
            } else {
                feature = UtilString.camelCase(value);
            }
            methods.add(feature);
        }
        return methods;
    }
}
