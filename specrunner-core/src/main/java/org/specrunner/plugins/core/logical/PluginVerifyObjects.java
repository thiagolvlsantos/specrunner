/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.plugins.core.logical;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.parameters.IAccessFactory;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.UnstackedPluginException;
import org.specrunner.plugins.core.AbstractPluginTable;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.string.UtilString;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.core.PresentationCompare;
import org.specrunner.util.xom.core.PresentationException;
import org.specrunner.util.xom.node.CellAdapter;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

public class PluginVerifyObjects extends AbstractPluginTable {

    private static final String ATT_ITERABLE = "iterable";

    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        try {
            Object value = tableAdapter.getObject(context, false);
            if (value == null) {
                process(context, result, tableAdapter, Collections.emptyIterator());
            } else {
                Class<? extends Object> clazz = value.getClass();
                if (clazz.isArray() || (value instanceof Iterable)) {
                    tableAdapter.setAttribute(ATT_ITERABLE, "true");
                    Iterable collection = null;
                    if (clazz.isArray()) {
                        collection = objectToList(value);
                    } else {
                        collection = (Iterable) value;
                    }
                    process(context, result, tableAdapter, collection.iterator());
                } else {
                    result.addResult(Failure.INSTANCE, context.newBlock(tableAdapter.getNode(), this), "'value' attribute should be an instance of iterable or an array. Received: " + clazz.getName());
                }
            }
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    /**
     * Convert an object to a list, if it is an array.
     * 
     * @param received
     *            The object.
     * @return The object as list, if array, the corresponding list, if not
     *         null.
     */
    protected List<Object> objectToList(Object received) {
        List<Object> collection = null;
        for (int k = 0; k < Integer.MAX_VALUE; k++) {
            try {
                Object object = Array.get(received, k);
                if (collection == null) {
                    collection = new LinkedList<Object>();
                }
                collection.add(object);
            } catch (ArrayIndexOutOfBoundsException e) { // ended
                break;
            }
        }
        return collection;
    }

    /**
     * Perform recursive comparison.
     * 
     * @param context
     *            A context.
     * @param result
     *            A result.
     * @param tableAdapter
     *            A table adapter.
     * @param iterator
     *            An iterable object.
     * @throws PluginException
     *             On expected value recovery error.
     * @throws ComparatorException
     *             On comparisons processing errors.
     */
    @SuppressWarnings("rawtypes")
    protected void process(IContext context, IResultSet result, TableAdapter tableAdapter, Iterator iterator) throws PluginException, ComparatorException {
        if (tableAdapter.hasAttribute(ATT_ITERABLE)) {
            processIterable(context, result, tableAdapter, iterator);
        } else {
            processTerminal(context, result, tableAdapter, iterator);
        }
    }

    /**
     * Process terminal values.
     * 
     * @param context
     *            A context.
     * @param result
     *            A result.
     * @param tableAdapter
     *            A table adapter.
     * @param iterator
     *            An iterable object.
     * @throws PluginException
     *             On expected value recovery error.
     * @throws ComparatorException
     *             On comparisons processing errors.
     */
    @SuppressWarnings("rawtypes")
    protected void processTerminal(IContext context, IResultSet result, TableAdapter tableAdapter, Iterator iterator) throws PluginException, ComparatorException {
        List<RowAdapter> rows = tableAdapter.getRows();
        for (int i = 0; i < rows.size(); i++) {
            RowAdapter r = rows.get(i);
            if (UtilNode.isIgnore(r.getNode())) {
                continue;
            }
            if (!iterator.hasNext()) {
                missingItems(context, result, tableAdapter, i);
                break;
            }
            Object received = iterator.next();
            CellAdapter c = r.getCell(0);
            if (UtilNode.isIgnore(c.getNode())) {
                continue;
            }
            compare(context, result, false, c.getComparator(), null, c, received);
        }
        return;
    }

    /**
     * Add information of missing object or terminal items.
     * 
     * @param context
     *            A context.
     * @param result
     *            A result.
     * @param tableAdapter
     *            A table adapter.
     * @param start
     *            Starting position of missing items.
     */
    protected void missingItems(IContext context, IResultSet result, TableAdapter tableAdapter, int start) {
        List<RowAdapter> rows = tableAdapter.getRows();
        for (int i = start; i < rows.size(); i++) {
            RowAdapter r = rows.get(i);
            if (UtilNode.isIgnore(r.getNode())) {
                continue;
            }
            result.addResult(Failure.INSTANCE, context.newBlock(r.getCellsCount() == 0 ? r.getNode() : r.getCell(0).getNode(), this), new UnstackedPluginException("Missing row."));
        }
    }

    /**
     * Compare terminal elements.
     * 
     * @param context
     *            A context.
     * @param result
     *            A result.
     * @param comparator
     *            A comparator.
     * @param cellExpected
     *            The expected cell adapter.
     * @param received
     *            The received value.
     * @param eval
     *            true, to perform evaluation on cell content, false, otherwise.
     * @throws PluginException
     *             On expected value recovery error.
     * @throws ComparatorException
     *             On comparisons processing errors.
     */
    protected void compare(IContext context, IResultSet result, boolean collection, IComparator comparator, CellAdapter headerExpected, CellAdapter cellExpected, Object received) throws PluginException, ComparatorException {
        boolean eval = cellExpected.hasAttribute(INodeHolder.ATTRIBUTE_EVALUATION);
        if (eval) {
            eval = Boolean.valueOf(cellExpected.getAttribute(INodeHolder.ATTRIBUTE_EVALUATION, "true"));
        } else {
            eval = headerExpected == null ? true : Boolean.valueOf(headerExpected.getAttribute(INodeHolder.ATTRIBUTE_EVALUATION, "true"));
        }
        if (!eval) {
            cellExpected.setAttribute(INodeHolder.ATTRIBUTE_EVALUATION, String.valueOf(false));
        }
        if (headerExpected != null) {
            if (!cellExpected.hasAttribute(INodeHolder.ATTRIBUTE_CONVERTER) && headerExpected.hasAttribute(INodeHolder.ATTRIBUTE_CONVERTER)) {
                cellExpected.setAttribute(INodeHolder.ATTRIBUTE_CONVERTER, headerExpected.getAttribute(INodeHolder.ATTRIBUTE_CONVERTER));
            }
            int i = 0;
            while (true) {
                if (!cellExpected.hasAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + i) && headerExpected.hasAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + i)) {
                    cellExpected.setAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + i, headerExpected.getAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + i));
                } else {
                    break;
                }
                i++;
            }
            if (!cellExpected.hasAttribute(INodeHolder.ATTRIBUTE_FORMATTER) && headerExpected.hasAttribute(INodeHolder.ATTRIBUTE_FORMATTER)) {
                cellExpected.setAttribute(INodeHolder.ATTRIBUTE_FORMATTER, headerExpected.getAttribute(INodeHolder.ATTRIBUTE_FORMATTER));
            }
            i = 0;
            while (true) {
                if (!cellExpected.hasAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + i) && headerExpected.hasAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + i)) {
                    cellExpected.setAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + i, headerExpected.getAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + i));
                } else {
                    break;
                }
                i++;
            }
        }
        Object expected = cellExpected.getObject(context, true);
        if (collection && "".equals(expected)) {
            expected = null;
        }
        Exception e = verify(context, comparator, expected, received);
        if (e != null) {
            result.addResult(Failure.INSTANCE, context.newBlock(cellExpected.getNode(), this), e);
        } else {
            result.addResult(Success.INSTANCE, context.newBlock(cellExpected.getNode(), this));
        }
    }

    /**
     * Check if expected and received values are equal.
     * 
     * @param context
     *            A context.
     * @param comparator
     *            A comparator.
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     * @return An exception if expected and received do not match.
     * @throws ComparatorException
     *             On comparison errors.
     */
    protected Exception verify(IContext context, IComparator comparator, Object expected, Object received) throws ComparatorException {
        comparator.initialize();
        if (!comparator.match(expected, received)) {
            if (expected instanceof String && received instanceof String) {
                return new DefaultAlignmentException(expected.toString(), received.toString());
            } else {
                return new PresentationException(new PresentationCompare(expected, received));
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    protected void processIterable(IContext context, IResultSet result, TableAdapter tableAdapter, Iterator iterator) throws PluginException, ComparatorException {
        IAccessFactory factory = SRServices.get(IAccessFactory.class);
        List<RowAdapter> rows = tableAdapter.getRows();
        RowAdapter header = rows.get(0);
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter r = rows.get(i);
            if (UtilNode.isIgnore(r.getNode())) {
                continue;
            }
            if (header.getCellsCount() != r.getCellsCount()) {
                result.addResult(Failure.INSTANCE, context.newBlock(r.getCell(0).getNode(), this), new UnstackedPluginException("Number of cells '" + r.getCellsCount() + "' different of headers '" + header.getCellsCount() + "'."));
                continue;
            }
            if (!iterator.hasNext()) {
                missingItems(context, result, tableAdapter, i);
                break;
            }
            Object current = iterator.next();
            for (int j = 0; j < header.getCellsCount(); j++) {
                CellAdapter h = header.getCell(j);
                if (UtilNode.isIgnore(h.getNode())) {
                    continue;
                }
                CellAdapter c = r.getCell(j);
                if (UtilNode.isIgnore(c.getNode())) {
                    continue;
                }
                String str = h.getValue(context);
                String feature = h.getAttribute("feature", h.getAttribute("field", h.getAttribute("property", str)));
                String field = h.hasAttribute("property") ? feature : UtilString.getNormalizer().camelCase(feature);
                Object received = factory.getProperty(current, field);
                Class<?> attType = received != null ? received.getClass() : null;
                if (attType != null && (attType.isArray() || Iterable.class.isAssignableFrom(attType))) {
                    Nodes nodes = c.getNode().query(getIterableXPath());
                    if (nodes.size() == 0) {
                        if (received != null) {
                            result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new Exception("Expected @null, received:" + received));
                        } else {
                            result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                        }
                        continue;
                    } else {
                        Node node = nodes.get(0);
                        TableAdapter subtable = new TableAdapter((Element) node);
                        Iterable collection = null;
                        if (attType.isArray()) {
                            collection = objectToList(received);
                        } else {
                            collection = (Iterable) received;
                        }
                        if (subtable.getRowCount() == 0) {
                            if (collection == null || collection.iterator().hasNext()) {
                                result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new Exception("Expected @empty, received:" + collection));
                            } else {
                                result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                            }
                        } else {
                            if (collection == null) {
                                result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new Exception("Expected not null, received:" + collection));
                            } else {
                                if (h.hasAttribute(ATT_ITERABLE)) {
                                    subtable.setAttribute(ATT_ITERABLE, h.getAttribute(ATT_ITERABLE));
                                }
                                process(context, result, subtable, collection.iterator());
                            }
                        }
                    }
                } else {
                    compare(context, result, true, c.getComparator(h.getComparator()), h, c, received);
                }
            }
        }
        extraItems(context, result, tableAdapter, iterator);
    }

    /**
     * Return the XPath expression for object attributes of type 'iterable'.
     * 
     * @return The xpath to select sublists/tables.
     */
    protected String getIterableXPath() {
        return "child::table";
    }

    /**
     * Add error for extra items in enumeration.
     * 
     * @param context
     *            A context.
     * @param result
     *            A result.
     * @param tableAdapter
     *            A table adapter.
     * @param iterator
     *            An iterator.
     */
    @SuppressWarnings("rawtypes")
    protected void extraItems(IContext context, IResultSet result, TableAdapter tableAdapter, Iterator iterator) {
        if (iterator.hasNext()) {
            int count = 0;
            Element tr = new Element("tr");
            int colspan = 1;
            if (tableAdapter.getRowCount() > 0) {
                colspan = tableAdapter.getRow(0).getCellsCount();
            }
            while (iterator.hasNext()) {
                count++;
                Object current = iterator.next();
                Element td = new Element("td");
                td.addAttribute(new Attribute("colspan", String.valueOf(colspan)));
                tr.appendChild(td);
                td.appendChild(String.valueOf(current));
            }
            tableAdapter.append(tr);
            result.addResult(Failure.INSTANCE, context.newBlock(tableAdapter.getNode(), this), new UnstackedPluginException("Iterable has '" + count + "' unexpected " + (count == 1 ? "item" : "items") + "."));
        }
    }
}