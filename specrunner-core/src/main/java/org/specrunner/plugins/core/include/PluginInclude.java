/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.plugins.core.include;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.StringTokenizer;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.listeners.ISourceListener;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.result.status.Warning;
import org.specrunner.runner.RunnerException;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.transformer.ITransformer;
import org.specrunner.transformer.ITransformerManager;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.UtilNode;

/**
 * Allow a file inclusion.
 * <p>
 * Example: <blockquote> Perform plugin as in <a href="#">login.html</a>.
 * </blockquote>
 * <p>
 * The CSS annotated version: <blockquote>
 * 
 * <pre>
 * Perform plugin as in &lt;a href="#" class="include"&gt;login.html&lt;/a&gt;.
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Thiago Santos
 * 
 */
public class PluginInclude extends AbstractPlugin {

    /**
     * Style added to included file.
     */
    public static final String CSS_INCLUDED = "included";
    /**
     * Style added to the header file.
     */
    public static final String CSS_INCLUDED_FILE = "included_file";
    /**
     * Style added to args.
     */
    public static final String CSS_INCLUDED_ARGS = "included_file_args";
    /**
     * Style added to content file.
     */
    public static final String CSS_INCLUDED_CONTENT = "included_content";

    /**
     * Input directory file.
     */
    private String dir;

    /**
     * Link to file.
     */
    protected String href;

    /**
     * Max deep of includes.
     */
    public static final String FEATURE_DEPTH = PluginInclude.class.getName() + ".depth";
    /**
     * Default value.
     */
    public static final Integer DEFAULT_DEPTH = Integer.MAX_VALUE;
    /**
     * Max depth of inclusion.
     */
    protected Integer depth = DEFAULT_DEPTH;

    /**
     * Injected expanded mode for features.
     */
    public static final String FEATURE_INJECTED = PluginInclude.class.getName() + ".injected";
    /**
     * Default injected state.
     */
    public static final Boolean DEFAULT_INJECTED = Boolean.TRUE;
    /**
     * Injected state.
     */
    protected Boolean injected = DEFAULT_INJECTED;

    /**
     * Enable expanded mode for features.
     */
    public static final String FEATURE_EXPANDED = PluginInclude.class.getName() + ".expanded";
    /**
     * Default include expanded state.
     */
    public static final Boolean DEFAULT_EXPANDED = Boolean.FALSE;
    /**
     * Expanded state.
     */
    protected Boolean expanded = null;

    /**
     * Transformer type as feature.
     */
    public static final String FEATURE_TRANSFORMER = PluginInclude.class.getName() + ".transformer";
    /**
     * Transformer to be used after loading in addiction to the general
     * transformer.
     */
    protected String transformer;

    /**
     * Resolver of URIs.
     */
    public static final String FEATURE_RESOLVER = PluginInclude.class.getName() + ".resolver";
    /**
     * Relative URI resolver.
     */
    protected IResolver resolver = new ResolverDefault();

    /**
     * The input directory.
     * 
     * @return The directory.
     */
    public String getDir() {
        return dir;
    }

    /**
     * Output directory file.
     * 
     * @param dir
     *            The directory.
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * The link reference.
     * 
     * @return The link.
     */
    public String getHref() {
        return href;
    }

    /**
     * Set link reference.
     * 
     * @param href
     *            The reference.
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * The max depth allowed for inclusion files.
     * 
     * @return The max depth on inclusion macro expansion. i.e. To perform only
     *         Google web page set depth='1', otherwise all web be be macro
     *         expanded.
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * Set max depth.
     * 
     * @param depth
     *            The depth.
     */
    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    /**
     * Get the injected information.
     * 
     * @return true, to inject HTML, false, otherwise.
     */
    public Boolean getInjected() {
        return injected;
    }

    /**
     * Set injected state.
     * 
     * @param injected
     *            true, to inject HTML, false, otherwise.
     */
    public void setInjected(Boolean injected) {
        this.injected = injected;
    }

    /**
     * Gets the status expected for inclusion.
     * 
     * @return true, if node might be shown expanded after inclusion, false, if
     *         collapsed. IMPORTANT: on errors in included files the expended
     *         mode will be overridden to expanded=true.
     */
    public Boolean getExpanded() {
        return expanded;
    }

    /**
     * Set expanded mode.
     * 
     * @param expanded
     *            The expanded value.
     */
    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    /**
     * Get the transformer field.
     * 
     * @return The transformer.
     */
    public String getTransformer() {
        return transformer;
    }

    /**
     * Set the transformer.
     * 
     * @param transformer
     *            The transformer.
     */
    public void setTransformer(String transformer) {
        this.transformer = transformer;
    }

    /**
     * Resolver of relative links.
     * 
     * @return The current resolver.
     */
    public IResolver getResolver() {
        return resolver;
    }

    /**
     * Change the resolver.
     * 
     * @param resolver
     *            A resolver.
     */
    public void setResolver(IResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_DEPTH, this);
        fm.set(FEATURE_INJECTED, this);
        if (expanded == null) {
            fm.set(FEATURE_EXPANDED, this);
        }
        if (transformer == null) {
            fm.set(FEATURE_TRANSFORMER, this);
        }
        fm.set(FEATURE_RESOLVER, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        UtilNode.appendCss(node, "include");
        ParentNode parent = node.getParent();
        boolean isTd = UtilNode.isElement(node, "td");
        if (isTd) {
            parent = (ParentNode) node;
        }
        try {
            String path = getPath(context);
            Node args = bindParameters(context);
            URI originalHref = new URI(path);
            URI newHref = originalHref;
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("HREF>" + newHref);
            }
            ISource current = context.getCurrentSource();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("CURRENT>" + current);
            }
            URI source = current.getURI();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("SRC>" + source);
            }
            newHref = dir == null ? resolver.resolve(source, newHref) : newHref;
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("URI_RESOLVED>" + newHref);
            }
            ISource newSource = getSource(newHref);
            newSource = transform(newSource);
            Document document = getDocument(newSource);

            // index to insert table
            int nodeIndex = isTd ? parent.getChildCount() : parent.indexOf(node) + 1;

            // perform link content
            UtilPlugin.performChildren(node, context, result);

            // id
            String id = "include_" + System.currentTimeMillis() + "_" + System.nanoTime();

            // handle event
            Element ele = (Element) node;
            ele.addAttribute(new Attribute("id", id));

            // result table
            Element resultTable = new Element("div");
            resultTable.addAttribute(new Attribute("id", id + "_inc"));
            UtilNode.setIgnore(resultTable);
            parent.insertChild(resultTable, nodeIndex);

            Element table = new Element("table");
            UtilNode.appendCss(table, CSS_INCLUDED);
            resultTable.appendChild(table);

            // file
            Element trFile = new Element("tr");
            table.appendChild(trFile);

            Element thFile = new Element("th");
            UtilNode.appendCss(thFile, CSS_INCLUDED_FILE);
            UtilNode.setIgnore(thFile);
            trFile.appendChild(thFile);

            Element link = new Element("a");
            link.addAttribute(new Attribute("href", String.valueOf(newHref)));
            link.appendChild(originalHref.toString());
            thFile.appendChild(link);

            thFile = new Element("th");
            UtilNode.appendCss(thFile, CSS_INCLUDED_FILE);
            UtilNode.setIgnore(thFile);
            trFile.appendChild(thFile);
            thFile.appendChild(args);

            // content
            Element trContent = new Element("tr");
            table.appendChild(trContent);
            int failtCount = result.size();

            Element tdContent = new Element("td");
            tdContent.addAttribute(new Attribute("colspan", "2"));
            UtilNode.appendCss(tdContent, CSS_INCLUDED_CONTENT);
            trContent.appendChild(tdContent);
            if (context.getSources().size() > depth) {
                tdContent.addAttribute(new Attribute("class", Warning.INSTANCE.getCssName()));
                tdContent.appendChild(new Text("Max depth of '" + depth + "' reached."));
                result.addResult(Warning.INSTANCE, context.newBlock(node, this), "Max depth of '" + depth + "' reached>" + toString(context) + "\n on run " + newSource);
            } else {
                if (!context.getSources().contains(newSource)) {
                    try {
                        int count = result.size();
                        Node root = document.getRootElement().copy();
                        tdContent.appendChild(root);
                        context.getSources().push(newSource);
                        List<ISourceListener> listeners = SRServices.get(IListenerManager.class).filterByType(ISourceListener.class);
                        // perform before listeners
                        for (ISourceListener sl : listeners) {
                            sl.onBefore(newSource, context, result);
                        }
                        try {
                            context.getRunner().run(root, context, result);
                        } finally {
                            IResourceManager importedResources = context.getSources().peek().getManager();
                            context.getSources().pop();
                            // perform after listeners
                            for (ISourceListener sl : listeners) {
                                sl.onAfter(newSource, context, result);
                            }
                            failtCount = result.countErrors(failtCount);
                            if (!injected && failtCount == 0) {
                                // remove status added
                                while (result.size() > count) {
                                    result.remove(result.size() - 1);
                                }
                                root.detach();
                                tdContent.appendChild("All actions/assertions suceeded. ");
                            }
                            if (injected || failtCount != 0) {
                                IResourceManager resources = context.getSources().peek().getManager();
                                resources.merge(importedResources);
                            }
                        }
                    } catch (RunnerException e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                        throw new PluginException(e);
                    }
                } else {
                    tdContent.addAttribute(new Attribute("class", Warning.INSTANCE.getCssName()));
                    tdContent.appendChild(new Text("Cyclic dependency."));
                    result.addResult(Warning.INSTANCE, context.newBlock(node, this), "Cyclic dependency>" + toString(context) + "\n on run " + newSource);
                }
            }

            if (failtCount > 0) {
                UtilNode.appendCss(ele, "expanded");
                result.addResult(Warning.INSTANCE, context.newBlock(node, this), "Included file failed: " + path);
            } else {
                if (expanded == null || !expanded) {
                    UtilNode.appendCss(ele, "collapse");
                }
                result.addResult(Success.INSTANCE, context.newBlock(node, this));
            }
        } catch (URISyntaxException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            result.addResult(Failure.INSTANCE, context.newBlock(node, this), e);
        }
        return ENext.SKIP;
    }

    /**
     * Get source from a URI.
     * 
     * @param newHref
     *            The URI.
     * @return The corresponding source.
     * @throws PluginException
     *             On reading errors.
     */
    protected ISource getSource(URI newHref) throws PluginException {
        ISource newSource = null;
        try {
            newSource = SRServices.get(ISourceFactoryManager.class).newSource(newHref.toString());
        } catch (SourceException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        return newSource;
    }

    /**
     * Get document from source.
     * 
     * @param newSource
     *            The source.
     * @return The corresponding document.
     * @throws PluginException
     *             On load errors.
     */
    protected Document getDocument(ISource newSource) throws PluginException {
        Document document = null;
        try {
            document = newSource.getDocument();
        } catch (SourceException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        return document;
    }

    /**
     * Perform source transformation.
     * 
     * @param newSource
     *            Source to be transformed.
     * @return Transformed source, if applied, the original source, otherwise.
     * @throws PluginException
     *             On transform errors.
     */
    protected ISource transform(ISource newSource) throws PluginException {
        try {
            // common transformer
            newSource = SRServices.get(ITransformerManager.class).getDefault().transform(newSource);
        } catch (SourceException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        if (transformer != null) {
            ITransformerManager itm = SRServices.get(ITransformerManager.class);
            ITransformer t = itm.get(transformer);
            if (t == null) {
                try {
                    t = (ITransformer) Class.forName(transformer).newInstance();
                    itm.bind(transformer, t);
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new PluginException(e);
                }
            }
            t.initialize();
            try {
                newSource = t.transform(newSource);
            } catch (SourceException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
        }
        return newSource;
    }

    /**
     * Normalize path removing parameters which are set as arguments.
     * 
     * @param context
     *            The context.
     * @return The path to file normalized.
     * @throws PluginException
     *             On plugin errors.
     */
    protected Node bindParameters(IContext context) throws PluginException {
        int indexParameters = href.indexOf('?');
        if (indexParameters > 0) {
            String query = href.substring(indexParameters + 1);
            try {
                StringTokenizer st = new StringTokenizer(query, "&=");
                if (st.countTokens() % 2 != 0) {
                    throw new PluginException("The parameters are not valid. Current value:" + query);
                }
                Element args = new Element("table");
                UtilNode.appendCss(args, CSS_INCLUDED_ARGS);
                UtilNode.setIgnore(args);
                while (st.hasMoreTokens()) {
                    String key = URLDecoder.decode(st.nextToken(), "UTF-8");
                    String value = URLDecoder.decode(st.nextToken(), "UTF-8");
                    try {
                        String var = key;
                        Object obj = UtilEvaluator.evaluate(value, context, true);
                        context.saveLocal(var, obj);
                        Element tr = new Element("tr");
                        args.appendChild(tr);
                        Element td = new Element("td");
                        args.appendChild(td);
                        td.appendChild(var);
                        td = new Element("td");
                        UtilNode.setIgnore(td);
                        args.appendChild(td);
                        td.appendChild(String.valueOf(obj));
                    } catch (Exception e) {
                        throw new PluginException("Invalid parameter (" + key + "," + value + ")", e);
                    }
                }
                return args;
            } catch (UnsupportedEncodingException e) {
                throw new PluginException("Unable do decode query:" + query, e);
            }
        }
        return new Text("");
    }

    /**
     * Normalize path removing parameters.
     * 
     * @param context
     *            The context.
     * @return The path to file normalized.
     * @throws PluginException
     *             On plugin errors.
     */
    protected String getPath(IContext context) throws PluginException {
        String path = href;
        int indexParameters = href.indexOf('?');
        if (indexParameters > 0) {
            path = href.substring(0, indexParameters);
        }
        if (dir != null) {
            path = dir + "/" + path;
        }
        return path;
    }

    /**
     * Cycle as string.
     * 
     * @param context
     *            The context.
     * @return The path to cycle.
     */
    protected StringBuilder toString(IContext context) {
        StringBuilder sb = new StringBuilder();
        for (ISource i : context.getSources()) {
            sb.append("\n" + i);
        }
        return sb;
    }
}