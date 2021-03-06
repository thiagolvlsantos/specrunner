/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.readers.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.context.IContext;
import org.specrunner.dumper.core.ConstantsDumperFile;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.include.IResolver;
import org.specrunner.plugins.core.include.PluginInclude;
import org.specrunner.plugins.core.include.ResolverDefault;
import org.specrunner.readers.IReader;
import org.specrunner.readers.ReaderException;
import org.specrunner.source.ISource;
import org.specrunner.util.UtilIO;
import org.specrunner.util.xom.node.INodeHolder;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/**
 * Reader of content of a specified file.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ReaderFile implements IReader {

    /**
     * Relative URI resolver.
     */
    protected IResolver resolver = new ResolverDefault();

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
    public void initialize() {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(PluginInclude.FEATURE_RESOLVER, this);
    }

    @Override
    public String read(IContext context, Object obj, Object[] args) throws ReaderException {
        Node node = null;
        if (obj instanceof Node) {
            node = (Node) obj;
        }
        if (obj instanceof INodeHolder) {
            node = ((INodeHolder) obj).getNode();
        }
        StringBuilder sb = new StringBuilder();
        Nodes ns = node.query("descendant::a[@href]");
        for (int i = 0; i < ns.size(); i++) {
            Element element = (Element) ns.get(i);
            String file = element.getAttribute("href").getValue();
            ISource source = context.getCurrentSource();
            try {
                URI uri = source.getURI();
                URI newHref = uri.resolve(file);
                updateHref(element, newHref);
                getURI(sb, newHref);
            } catch (Exception e) {
                throw new ReaderException(e);
            }
        }
        return sb.toString();
    }

    /**
     * Update href link of included files.
     * 
     * @param element
     *            Href container.
     * @param newHref
     *            The href source.
     * @throws PluginException
     *             On calculation error.
     */
    protected void updateHref(Element element, URI newHref) throws PluginException {
        IConfiguration cfg = SRServices.getFeatureManager().getConfiguration();
        File f = new File((File) cfg.get(ConstantsDumperFile.FEATURE_OUTPUT_DIRECTORY), (String) cfg.get(ConstantsDumperFile.FEATURE_OUTPUT_NAME));
        URI link = resolver.resolve(f.toURI(), newHref);
        element.addAttribute(new Attribute("href", link.toString()));
    }

    /**
     * Get URI data.
     * 
     * @param sb
     *            String information.
     * @param newHref
     *            The reference.
     * @throws IOException
     *             On reading errors.
     */
    protected void getURI(StringBuilder sb, URI newHref) throws IOException {
        String href = newHref.getPath();
        File file = new File(href);
        if (!file.exists()) {
            href = href.substring(1);
            file = new File(href);
        }
        sb.append(getContent(file));
    }

    /**
     * Get contents of a file.
     * 
     * @param file
     *            A file.
     * @return String representation of a file.
     * @throws IOException
     *             On reading errors.
     */
    protected String getContent(File file) throws IOException {
        return UtilIO.readFile(file);
    }

    @Override
    public boolean isReplacer() {
        return false;
    }
}
