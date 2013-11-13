/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.plugins.impl.include;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Warning;
import org.specrunner.runner.RunnerException;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.transformer.ITransformer;
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
     * Style added to content file.
     */
    public static final String CSS_INCLUDED_CONTENT = "included_content";

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

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.set(FEATURE_DEPTH, this);
        if (expanded == null) {
            fh.set(FEATURE_EXPANDED, this);
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        ParentNode parent = node.getParent();
        try {
            String path = normalizeAddParameters(context);
            URI originalHref = new URI(path);
            URI newHref = originalHref;
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("HREF>" + newHref);
            }
            ISource current = context.getSources().peek();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("CURRENT>" + current);
            }
            URI source = current.getURI();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("SRC>" + source);
            }
            newHref = source.resolve(newHref);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("URI_RESOLVED>" + newHref);
            }
            ISource newSource = null;
            try {
                newSource = SpecRunnerServices.get(ISourceFactory.class).newSource(newHref.toString());
            } catch (SourceException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
            try {
                newSource = SpecRunnerServices.get(ITransformer.class).transform(newSource);
            } catch (SourceException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
            Document document = null;
            try {
                document = newSource.getDocument();
            } catch (SourceException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
            // index to insert table
            int nodeIndex = parent.indexOf(node) + 1;

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
            trFile.appendChild(thFile);
            thFile.appendChild(originalHref.toString());

            // content
            Element trContent = new Element("tr");
            table.appendChild(trContent);

            int failCount = result.countStatus(Failure.INSTANCE);

            Element tdContent = new Element("td");
            UtilNode.appendCss(tdContent, CSS_INCLUDED_CONTENT);
            trContent.appendChild(tdContent);
            if (context.getSources().size() > depth) {
                tdContent.addAttribute(new Attribute("class", Warning.INSTANCE.getCssName()));
                tdContent.appendChild(new Text("Max depth of '" + depth + "' reached."));
                result.addResult(Warning.INSTANCE, context.newBlock(node, this), "Max depth of '" + depth + "' reached>" + toString(context) + "\n on run " + newSource);
            } else {
                if (!context.getSources().contains(newSource)) {
                    try {
                        Node root = document.getRootElement().copy();
                        tdContent.appendChild(root);
                        context.getSources().push(newSource);
                        try {
                            context.getRunner().run(root, context, result);
                        } finally {
                            IResourceManager importedResources = context.getSources().peek().getManager();
                            context.getSources().pop();
                            IResourceManager resources = context.getSources().peek().getManager();
                            resources.merge(importedResources);
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

            failCount = result.countStatus(Failure.INSTANCE) - failCount;
            if (failCount > 0) {
                UtilNode.appendCss(ele, "expanded");
            } else {
                if (expanded == null || !expanded) {
                    UtilNode.appendCss(ele, "collapse");
                }
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
     * Normalize path removing parameters which are set as arguments.
     * 
     * @param context
     *            The context.
     * @return The path to file normalized.
     * @throws PluginException
     *             On plugin errors.
     */
    protected String normalizeAddParameters(IContext context) throws PluginException {
        String path = href;
        String query = null;
        int indexParameters = href.indexOf('?');
        if (indexParameters > 0) {
            path = href.substring(0, indexParameters);
            query = href.substring(indexParameters + 1);
        }
        if (query != null) {
            try {
                StringTokenizer st = new StringTokenizer(query, "&=");
                if (st.countTokens() % 2 != 0) {
                    throw new PluginException("The parameters are not valid. Current value:" + query);
                }
                while (st.hasMoreTokens()) {
                    String key = URLDecoder.decode(st.nextToken(), "UTF-8");
                    String value = URLDecoder.decode(st.nextToken(), "UTF-8");
                    try {
                        context.saveLocal(UtilEvaluator.asVariable(key), UtilEvaluator.evaluate(value, context));
                    } catch (Exception e) {
                        throw new PluginException("Invalid parameter (" + key + "," + value + ")", e);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                throw new PluginException("Unable do decode query:" + query, e);
            }
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