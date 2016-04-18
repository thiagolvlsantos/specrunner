/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.plugins.core.objects;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.parameters.DontEval;
import org.specrunner.parameters.core.UtilParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginTable;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.result.status.Warning;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.expression.UtilExpression;
import org.specrunner.util.string.UtilString;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.CellAdapter;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;
import org.specrunner.util.xom.node.UtilTable;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/**
 * Generic object plugin. To write object plugins override method
 * <code>isMapped()</code> and <code>action(...)</code>. i.e.
 * SpecRunner-Hibernate3 extends the object manipulation to save/update/delete
 * data using Hibernate3 infra-structure.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginObject extends AbstractPluginTable {

    /**
     * Super type of object.
     */
    protected String supermapping;
    /**
     * Object class name.
     */
    protected String type;
    /**
     * Object class.
     */
    protected Class<?> typeInstance;
    /**
     * Object creator name.
     */
    protected String creator;
    /**
     * Object creator instance.
     */
    protected IObjectCreator creatorInstance;
    /**
     * The identification fields.
     */
    protected String reference;
    /**
     * The identification fields list.
     */
    protected List<String> references = new LinkedList<String>();
    /**
     * Separator of identification attributes.
     */
    protected String separator;
    /**
     * The map of generic fields to be used as reference.
     */
    protected String mapping;
    /**
     * List of header definition fields.
     */
    protected Map<String, Field> headerToFields = new HashMap<String, Field>();
    /**
     * List of field definition fields.
     */
    protected Map<String, Field> fieldToFields = new HashMap<String, Field>();
    /**
     * List of fields.
     */
    protected List<Field> fields = new LinkedList<Field>();
    /**
     * Flag if the created objects are expected to be mapped in memory by
     * default.
     */
    protected boolean mapped = false;
    /**
     * Mapping of key before processing to the ones after processing.
     */
    protected Map<String, String> keysBefore = new HashMap<String, String>();
    /**
     * Mapping of identifiers to object instances.
     */
    protected Map<String, Object> instances = new HashMap<String, Object>();

    /**
     * Get super class mapping.
     * 
     * @return A super class mapping.
     */
    public String getSupermapping() {
        return supermapping;
    }

    /**
     * Set mapping for superclass object.
     * 
     * @param supermapping
     *            A super mapping.
     */
    public void setSupermapping(String supermapping) {
        this.supermapping = supermapping;
    }

    /**
     * The object type of the plugin. i.e.
     * <code>type='system.entity.Person'</code>.
     * 
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type.
     * 
     * @param type
     *            A new type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the corresponding class to type.
     * 
     * @return The class.
     */
    public Class<?> getTypeInstance() {
        return typeInstance;
    }

    /**
     * Sets the instance type.
     * 
     * @param typeInstance
     *            A new type.
     */
    public void setTypeInstance(Class<?> typeInstance) {
        this.typeInstance = typeInstance;
    }

    /**
     * Returns the object creator of embeddable objects.
     * 
     * @return The creator class name.
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the creator class.
     * 
     * @param creator
     *            A new creator class name.
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Object creator type.
     * 
     * @return The creator instance.
     */
    public IObjectCreator getCreatorInstance() {
        return creatorInstance;
    }

    /**
     * Sets the creator.
     * 
     * @param creatorInstance
     *            A new creator instance.
     */
    public void setCreatorInstance(IObjectCreator creatorInstance) {
        this.creatorInstance = creatorInstance;
    }

    /**
     * Sets the attribute which represents the keys of the entity. These fields
     * are used to create the instance key. i.e. if
     * <code>reference='id,name'</code> is used, the 'id' attribute and 'name'
     * attribute are used as the object key, separated by 'separator' field.
     * 
     * @return The list of attribute references in the expected order.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the references.
     * 
     * @param reference
     *            A new reference list.
     */
    @DontEval
    public void setReference(String reference) {
        this.reference = reference;
        if (reference != null) {
            references.clear();
            String[] refs = reference.split(",");
            for (String s : refs) {
                references.add(s.trim());
            }
        }
    }

    /**
     * Gets the string used to separate fields of reference. i.e. if
     * <code>reference='id,name'</code> and <code>separator='/'</code>, the
     * object instance corresponding to a given line will be 'id/name'.
     * 
     * @return The keys separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Sets identifier separator.
     * 
     * @param separator
     *            The separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Return the map of field information. For example, if there is a mapping
     * for object City already defined in a file name '/city.html' or
     * '/city.xml', just add map='/cities.html' to the tag.
     * 
     * @return The mapping.
     */

    public String getMapping() {
        return mapping;
    }

    /**
     * Sets the object mapping.
     * 
     * @param mapping
     *            The mapping.
     */
    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    @Override
    public void initialize(IContext context, TableAdapter table) throws PluginException {
        super.initialize(context, table);
        if (mapping != null) {
            loadMapping(context, table, mapping);
        } else {
            setObjectInformation();
        }
    }

    /**
     * Load mapping with predefined values.
     * 
     * @param context
     *            The context.
     * @param table
     *            The table.
     * @throws PluginException
     *             On mapping errors.
     */
    protected void loadMapping(IContext context, TableAdapter table, String map) throws PluginException {
        try {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Loading object mapping>" + map);
            }
            URL file = AbstractPluginObject.class.getResource(map);
            if (file == null) {
                throw new PluginException("The object mapping file '" + map + "' not found.");
            }
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Loading object mapping file>" + file);
            }
            ISource source = SRServices.get(ISourceFactoryManager.class).newSource(file.toString());
            Document doc = source.getDocument();
            Nodes ns = doc.query("//table");
            if (ns.size() == 0) {
                throw new PluginException("The mapping file must have a table element with fields information.");
            }
            Element n = (Element) ns.get(0);
            TableAdapter ta = UtilTable.newTable(n);
            if (ta.getRowCount() == 0) {
                throw new PluginException("The mapping file might have at least one row (usually a header) with the generic field information.");
            }
            RowAdapter information = ta.getRow(0);

            supermapping = null;

            // set table properties such as type/separator/id/etc.
            UtilParametrized.setProperties(context, this, n);

            if (supermapping != null) {
                loadMapping(context, table, supermapping);

                // set table properties such as type/separator/id/etc.
                UtilParametrized.setProperties(context, this, n);
            }

            // to replace specific settings back.
            UtilParametrized.setProperties(context, this, (Element) table.getNode());

            // set type objects.
            setObjectInformation();

            if (isSpecial()) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Primitive types, enums and String are not to be loaded by fields. RECEIVED:" + typeInstance);
                }
                return;
            }
            // load fields.
            List<Field> general = new LinkedList<Field>();
            loadFields(context, information, general);
            for (Field field : general) {
                headerToFields.put(field.getFieldName(), field);
                fieldToFields.put(field.getFullName(), field);
            }
        } catch (SourceException e) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info(e.getMessage(), e);
            }
            throw new PluginException("Fail on loading mapping information: '" + mapping + "'.", e);
        }
    }

    /**
     * Set object and/or creator information.
     * 
     * @throws PluginException
     *             On setting errors.
     */
    protected void setObjectInformation() throws PluginException {
        try {
            if (type != null) {
                typeInstance = Class.forName(type);
            }
            if (creator != null) {
                creatorInstance = (IObjectCreator) Class.forName(creator).newInstance();
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
        if (typeInstance == null) {
            throw new PluginException("Set 'type' with object class name.");
        }
    }

    /**
     * Checks if the instance is primitive, enum or string.
     * 
     * @return true, if yes, false, otherwise.
     */
    protected boolean isSpecial() {
        return typeInstance.isPrimitive() || typeInstance.isEnum() || typeInstance == String.class;
    }

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter table) throws PluginException {
        if (isMapped()) {
            SRServices.getObjectManager().bind(this);
        }
        if (!isSpecial()) {
            readHeader(context, result, table);
        }
        readData(context, result, table);
    }

    /**
     * Read header information.
     * 
     * @param context
     *            A context.
     * @param result
     *            A result.
     * @param table
     *            The table.
     * @throws PluginException
     *             On read errors.
     */
    protected void readHeader(IContext context, IResultSet result, TableAdapter table) throws PluginException {
        if (table.getRowCount() < 1) {
            throw new PluginException("Table should have at least header information.");
        }
        RowAdapter row = table.getRow(0);
        try {
            loadFields(context, row, fields);
            result.addResult(Success.INSTANCE, context.newBlock(row.getNode(), this));
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), this), e);
        }
    }

    /**
     * Read data information.
     * 
     * @param context
     *            A context.
     * @param result
     *            A result.
     * @param table
     *            The table.
     * @throws PluginException
     *             On read errors.
     */
    protected void readData(IContext context, IResultSet result, TableAdapter table) throws PluginException {
        for (int i = (isSpecial() ? 0 : 1); i < table.getRowCount(); i++) {
            RowAdapter row = table.getRow(i);
            try {
                processLine(context, table, row, result);
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), this), e);
            }
        }
    }

    /**
     * Load fields based on <code>th</code> tags.
     * 
     * @param context
     *            The context.
     * @param row
     *            The row.
     * @param list
     *            List of fields.
     * @throws PluginException
     *             On load errors.
     */
    protected void loadFields(IContext context, RowAdapter row, List<Field> list) throws PluginException {
        int index = 0;
        for (CellAdapter cell : row.getCells()) {
            boolean ignore = Boolean.parseBoolean(cell.getAttribute(UtilNode.IGNORE, "false"));

            String field = cell.hasAttribute("field") ? cell.getAttribute("field") : null;
            String fieldName = UtilString.getNormalizer().camelCase(cell.getValue(context));
            String name = field != null ? (field.endsWith(".") ? field + fieldName : field) : fieldName;
            Field f = headerToFields.get(fieldName);
            if (f == null) {
                f = fieldToFields.get(name);
                if (f == null) {
                    f = new Field();
                    f.setFieldName(fieldName);
                } else {
                    name = f.getFullName();
                }
            } else {
                name = f.getFullName();
            }
            f.setIndex(index++);

            f.setReference(references.contains(name));

            f.setIgnore(ignore);
            if (!ignore) {
                StringTokenizer st = new StringTokenizer(name, ".");
                String[] names = new String[st.countTokens()];
                for (int i = 0; i < names.length; i++) {
                    names[i] = st.nextToken();
                }
                if (names.length > 0) {
                    f.setNames(names);
                }

                Class<?>[] types = new Class<?>[names.length];
                Class<?> currentType = typeInstance;
                for (int i = 0; currentType != null && i < types.length; i++) {
                    Method m = null;
                    try {
                        m = currentType.getMethod("get" + Character.toUpperCase(names[i].charAt(0)) + names[i].substring(1));
                    } catch (Exception e) {
                        try {
                            m = currentType.getMethod("is" + Character.toUpperCase(names[i].charAt(0)) + names[i].substring(1));
                        } catch (Exception e1) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e1.getMessage(), e1);
                            }
                        }
                    }
                    if (m == null) {
                        throw new PluginException("Getter method for " + names[i] + " not found for type '" + currentType + "'.");
                    }
                    types[i] = m.getReturnType();
                    currentType = types[i];
                }
                if (types.length > 0) {
                    f.setTypes(types);
                }

                String def = cell.getAttribute("default", null);
                if (def != null) {
                    f.setDef(def);
                }

                String converter = cell.getAttribute(INodeHolder.ATTRIBUTE_CONVERTER, null);
                if (converter != null) {
                    f.setConverter(converter);
                }
                int i = 0;
                List<Object> args = new LinkedList<Object>();
                while (cell.hasAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + i)) {
                    args.add(UtilExpression.evaluate(cell.getAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + i), context, true));
                    i++;
                }
                if (f.getArgs() == null || !args.isEmpty()) {
                    f.setArgs(args.toArray(new String[args.size()]));
                }

                String formatter = cell.getAttribute(INodeHolder.ATTRIBUTE_FORMATTER, null);
                if (formatter != null) {
                    f.setFormatter(formatter);
                }

                int j = 0;
                List<Object> formArgs = new LinkedList<Object>();
                while (cell.hasAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + j)) {
                    formArgs.add(UtilExpression.evaluate(cell.getAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + j), context, true));
                    j++;
                }
                if (f.getFormattersArgs() == null || !formArgs.isEmpty()) {
                    f.setFormattersArgs(formArgs.toArray(new String[formArgs.size()]));
                }

                String comparator = cell.getAttribute(INodeHolder.ATTRIBUTE_COMPARATOR, null);
                if (comparator != null) {
                    f.setComparator(comparator);
                }
            }

            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("FIELD>" + f);
            }
            list.add(f);
        }
    }

    /**
     * Hold column information.
     * 
     * @author Thiago Santos
     * 
     */
    public static class Field {
        /**
         * Field index.
         */
        private int index;
        /**
         * Ignore column field.
         */
        private boolean ignore;
        /**
         * Is reference column.
         */
        private boolean reference;
        /**
         * The column name of the field.
         */
        private String fieldName;
        /**
         * Field name. Many-level.
         */
        private String[] names;
        /**
         * Field types. Many-level.
         */
        private Class<?>[] types;
        /**
         * Default value.
         */
        private String def;
        /**
         * Converters.
         */
        private String converter;
        /**
         * Arguments for converters.
         */
        private String[] args;
        /**
         * Formatters.
         */
        private String formatter;
        /**
         * Arguments for formatters.
         */
        private String[] formattersArgs;
        /**
         * Comparator name.
         */
        private String comparator;

        /**
         * Get index.
         * 
         * @return The index.
         */
        public int getIndex() {
            return index;
        }

        /**
         * Sets the index.
         * 
         * @param index
         *            The index.
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * Ignore the column with this mark.
         * 
         * @return true, to ignore, false, otherwise.
         */
        public boolean isIgnore() {
            return ignore;
        }

        /**
         * Sets to ignore a column.
         * 
         * @param ignore
         *            The ignore mark.
         */
        public void setIgnore(boolean ignore) {
            this.ignore = ignore;
        }

        /**
         * Get if the field is a key value.
         * 
         * @return true, if is part of key, false, otherwise.
         */
        public boolean isReference() {
            return reference;
        }

        /**
         * Set if the field is a key part.
         * 
         * @param reference
         *            The reference status.
         */
        public void setReference(boolean reference) {
            this.reference = reference;
        }

        /**
         * Gets the field column name.
         * 
         * @return The field column name.
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * The column name.
         * 
         * @param fieldName
         *            The column name.
         */
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * Gets names.
         * 
         * @return The names.
         */
        public String[] getNames() {
            return names;
        }

        /**
         * Sets the name.
         * 
         * @param names
         *            The names.
         */
        public void setNames(String[] names) {
            this.names = names == null ? null : Arrays.copyOf(names, names.length);
        }

        /**
         * Get types.
         * 
         * @return The types.
         */
        public Class<?>[] getTypes() {
            return types;
        }

        /**
         * Set types.
         * 
         * @param types
         *            The types.
         */
        public void setTypes(Class<?>[] types) {
            this.types = types == null ? null : Arrays.copyOf(types, types.length);
        }

        /**
         * Get default value.
         * 
         * @return The default.
         */
        public String getDef() {
            return def;
        }

        /**
         * Sets the default.
         * 
         * @param def
         *            The default.
         */
        public void setDef(String def) {
            this.def = def;
        }

        /**
         * Get converters.
         * 
         * @return The converters.
         */
        public String getConverter() {
            return converter;
        }

        /**
         * Set converter.
         * 
         * @param converter
         *            The converter.
         */
        public void setConverter(String converter) {
            this.converter = converter;
        }

        /**
         * The converter arguments.
         * 
         * @return The arguments.
         */
        public String[] getArgs() {
            return args;
        }

        /**
         * Set converter arguments.
         * 
         * @param args
         *            The arguments.
         */
        public void setArgs(String[] args) {
            this.args = args == null ? null : Arrays.copyOf(args, args.length);
        }

        /**
         * Get formatter.
         * 
         * @return The formatter.
         */
        public String getFormatter() {
            return formatter;
        }

        /**
         * Set formatter.
         * 
         * @param formatter
         *            The formatter.
         */
        public void setFormatter(String formatter) {
            this.formatter = formatter;
        }

        /**
         * The formatter arguments.
         * 
         * @return The arguments.
         */
        public String[] getFormattersArgs() {
            return formattersArgs;
        }

        /**
         * Set formatter arguments.
         * 
         * @param args
         *            The arguments.
         */
        public void setFormattersArgs(String[] args) {
            this.formattersArgs = args == null ? null : Arrays.copyOf(args, args.length);
        }

        /**
         * Gets the comparator.
         * 
         * @return The comparator.
         */
        public String getComparator() {
            return comparator;
        }

        /**
         * Sets the comparator.
         * 
         * @param comparator
         *            The comparator.
         */
        public void setComparator(String comparator) {
            this.comparator = comparator;
        }

        /**
         * Get full name of field.
         * 
         * @return The name.
         */
        public String getFullName() {
            StringBuilder strNames = new StringBuilder("");
            for (int i = 0; i < names.length; i++) {
                strNames.append(i == 0 ? "" : ".");
                strNames.append(names[i]);
            }
            return strNames.toString();
        }

        /**
         * Get the type of field.
         * 
         * @return The type.
         */
        public Class<?> getSpecificType() {
            return types[types.length - 1];
        }

        /**
         * Check if an attribute is a list.
         * 
         * @return true, if yes, false, otherwise.
         */
        public boolean isList() {
            return List.class.isAssignableFrom(getSpecificType());
        }

        @Override
        public String toString() {
            StringBuilder strNames = new StringBuilder("");
            for (int i = 0; names != null && i < names.length; i++) {
                strNames.append((i == 0 ? "" : ",") + names[i]);
            }
            StringBuilder strTypes = new StringBuilder("");
            for (int i = 0; types != null && i < types.length; i++) {
                strTypes.append((i == 0 ? "" : ",") + types[i]);
            }
            StringBuilder strConvs = new StringBuilder(converter);
            StringBuilder strArgs = new StringBuilder("");
            for (int i = 0; args != null && i < args.length; i++) {
                strArgs.append((i == 0 ? "" : ",") + args[i]);
            }
            StringBuilder strForm = new StringBuilder(formatter);
            StringBuilder strFormArgs = new StringBuilder("");
            for (int i = 0; formattersArgs != null && i < formattersArgs.length; i++) {
                strFormArgs.append((i == 0 ? "" : ",") + formattersArgs[i]);
            }
            return index + ",'" + fieldName + "'(" + ignore + ")," + strNames + "( default '" + def + "')," + strTypes + ",[" + strConvs + "],[" + strArgs + "],[" + strForm + "],[" + strFormArgs + "],(" + comparator + ")";
        }
    }

    /**
     * Flag is an object is mapped or not.
     * 
     * @param mapped
     *            true, to be mapped, false, otherwise.
     */
    public void setMapped(boolean mapped) {
        this.mapped = mapped;
    }

    /**
     * Says if the instance show be mapped or not.
     * 
     * @return true, to map, false, otherwise.
     */
    public boolean isMapped() {
        return mapped;
    }

    /**
     * Process a given row.
     * 
     * @param context
     *            The context.
     * @param table
     *            The table.
     * @param row
     *            The row.
     * @param result
     *            The result set.
     * @return Object instance created.
     * @throws Exception
     *             On processing errors.
     */
    protected Object processLine(IContext context, TableAdapter table, RowAdapter row, IResultSet result) throws Exception {
        Object instance = create(context, table, row);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("CREATE:" + instance);
        }
        if (isSpecial()) {
            action(context, instance, row, result);
            result.addResult(Success.INSTANCE, context.newBlock(row.getNode(), this));
            return instance;
        }
        if (populate(context, table, row, result, instance)) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("POPULATE:" + instance);
            }
            String keyBefore = makeKey(instance);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("KEYBEFORE:" + keyBefore);
            }
            Object obj = instances.get(keysBefore.get(keyBefore));
            if (obj != null && isMapped()) {
                result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), this), new PluginException("Key '" + keyBefore + "' already used by :" + obj));
                for (CellAdapter cell : row.getCells()) {
                    String title = cell.getAttribute("title", "|");
                    String old = title.substring(0, title.lastIndexOf('|'));
                    if (old.isEmpty()) {
                        cell.removeAttribute("title");
                    } else {
                        cell.setAttribute("title", old);
                    }
                }
            } else {
                action(context, instance, row, result);
                if (isMapped()) {
                    String keyAfter = makeKey(instance);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("KEYAFTER:" + keyAfter);
                    }
                    mapObject(instance, keyBefore, keyAfter);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("SAVE:" + keysBefore);
                        UtilLog.LOG.debug("INST:" + instances);
                    }
                }
                result.addResult(Success.INSTANCE, context.newBlock(row.getNode(), this));
            }
        }
        return instance;
    }

    /**
     * Create the object based on a row.
     * 
     * @param context
     *            The test context.
     * @param table
     *            The table.
     * @param row
     *            The row.
     * @return The object corresponding to the row.
     * @throws Exception
     *             On creation errors.
     */
    protected Object create(IContext context, TableAdapter table, RowAdapter row) throws Exception {
        Object result = null;
        if (creator != null) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("CREATOR_ROW(" + row + ")");
            }
            result = creatorInstance.create(context, table, row, typeInstance);
        }
        if (result == null) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("NEW_INSTANCE_ROW(" + row + ")");
            }
            if (isSpecial()) {
                CellAdapter cell = row.getCell(0);
                if (typeInstance.isEnum()) {
                    result = cell.getObject(context, true);
                } else {
                    Constructor<?> constructor = typeInstance.getConstructor(String.class);
                    Object tmp = cell.getObject(context, true);
                    result = constructor.newInstance(tmp);
                }
            } else {
                result = typeInstance.newInstance();
            }
        }
        return result;
    }

    /**
     * Populate the object instance with row information.
     * 
     * @param instance
     *            The object instance.
     * @param context
     *            The test context.
     * @param header
     *            The table header.
     * @param row
     *            The row with attribute informations.
     * @param result
     *            The result set.
     * @return The object filled with row information.
     * @throws Exception
     *             On population errors.
     */
    protected boolean populate(IContext context, TableAdapter table, RowAdapter row, IResultSet result, Object instance) throws Exception {
        boolean ok = true;
        for (int i = 0; i < fields.size(); i++) {
            if (row.getCellsCount() != fields.size()) {
                result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), this), "Number of cells(" + row.getCellsCount() + ") is different of headers(" + fields.size() + ")");
                ok = false;
                break;
            }
            CellAdapter cell = row.getCell(i);
            try {
                Field f = fields.get(i);
                if (f.isIgnore()) {
                    continue;
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("ON>" + f.getFullName());
                }
                String text = null;
                Object value = null;
                if (f.isList()) {
                    Node node = cell.getNode();
                    Nodes childs = node.query("descendant::table");
                    INodeHolder holder = SRServices.get(INodeHolderFactory.class).newHolder(childs.get(0));
                    value = context.getByName(holder.getAttribute("collection"));
                } else {
                    if (!cell.hasAttribute(INodeHolder.ATTRIBUTE_CONVERTER)) {
                        String converter = f.converter;
                        if (converter != null) {
                            cell.setAttribute(INodeHolder.ATTRIBUTE_CONVERTER, converter);
                            for (int j = 0; f.args != null && j < f.args.length; j++) {
                                if (!cell.hasAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + j)) {
                                    cell.setAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_CONVERTER_PREFIX + j, f.args[j]);
                                }
                            }
                        }
                    }
                    if (!cell.hasAttribute(INodeHolder.ATTRIBUTE_FORMATTER)) {
                        String formatter = f.formatter;
                        if (formatter != null) {
                            cell.setAttribute(INodeHolder.ATTRIBUTE_FORMATTER, formatter);
                            for (int j = 0; f.formattersArgs != null && j < f.formattersArgs.length; j++) {
                                if (!cell.hasAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + j)) {
                                    cell.setAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + j, f.formattersArgs[j]);
                                }
                            }
                        }
                    }
                    value = cell.getObject(context, true);
                    text = String.valueOf(value);
                    if (text.isEmpty()) {
                        value = UtilExpression.evaluate(f.def, context, true);
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("USING_DEFAULT>" + value);
                        }
                    }
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("VALUE>" + value);
                    }
                }
                setValue(context, table, row, instance, f, value);
                String out = String.valueOf(value);
                if (text != null && !text.equals(out)) {
                    cell.append("{" + out + "}");
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    Element ele = new Element("span");
                    ele.addAttribute(new Attribute("class", "object"));
                    ele.appendChild("(" + (value != null ? value.getClass().getName() : "null") + ")");
                    cell.append(ele);
                }
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.newBlock(cell.getNode(), this), e);
                ok = false;
            }
        }
        if (!ok) {
            result.addResult(Warning.INSTANCE, context.newBlock(row.getNode(), this), "Could not create instance.");
        }
        return ok;
    }

    /**
     * Set the field with the given value.
     * 
     * @param context
     *            The test context.
     * @param table
     *            The table.
     * @param row
     *            The row corresponding to the object.
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set in instance for field 'f'.
     * @throws Exception
     *             On set value errors.
     */
    protected void setValue(IContext context, TableAdapter table, RowAdapter row, Object instance, Field f, Object value) throws Exception {
        // populate inner objects
        Object current = instance;
        for (int i = 0; i < f.names.length - 1; i++) {
            Object tmp = PropertyUtils.getProperty(current, f.names[i]);
            if (tmp == null) {
                if (creator != null) {
                    tmp = creatorInstance.create(context, table, row, f.types[i]);
                }
                if (tmp == null) {
                    if (f.types[i] == List.class) {
                        tmp = new LinkedList<Object>();
                    } else {
                        tmp = f.types[i].newInstance();
                    }
                }
                PropertyUtils.setProperty(current, f.names[i], tmp);
            }
            current = tmp;
        }
        if (value == null) {
            setObject(instance, f, null);
        } else {
            Class<?> st = f.getSpecificType();
            if (st == boolean.class || st == Boolean.class) {
                setBoolean(instance, f, value);
            } else if (st == char.class || st == Character.class) {
                setChar(instance, f, value);
            } else if (st == short.class || st == Short.class) {
                setShort(instance, f, value);
            } else if (st == int.class || st == Integer.class) {
                setInteger(instance, f, value);
            } else if (st == long.class || st == Long.class) {
                setLong(instance, f, value);
            } else if (st == float.class || st == Float.class) {
                setFloat(instance, f, value);
            } else if (st == double.class || st == Double.class) {
                setDouble(instance, f, value);
            } else if (SRServices.getObjectManager().isBound(st)) {
                setEntity(instance, f, value);
            } else {
                setObject(instance, f, value);
            }
        }
    }

    /**
     * Sets a boolean to a field.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On setting errors.
     */
    protected void setBoolean(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Boolean.valueOf(String.valueOf(value)));
    }

    /**
     * Sets a char to a field.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On setting errors.
     */
    protected void setChar(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Character.valueOf(String.valueOf(value).charAt(0)));
    }

    /**
     * Sets a short to a field.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On setting errors.
     */
    protected void setShort(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Short.valueOf(String.valueOf(value)));
    }

    /**
     * Sets an integer to a field.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On setting errors.
     */
    protected void setInteger(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Integer.valueOf(String.valueOf(value)));
    }

    /**
     * Sets a long to a field.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On setting errors.
     */
    protected void setLong(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Long.valueOf(String.valueOf(value)));
    }

    /**
     * Sets a float to a field.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On setting errors.
     */
    protected void setFloat(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Float.valueOf(String.valueOf(value)));
    }

    /**
     * Sets a double to a field.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On setting errors.
     */
    protected void setDouble(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Double.valueOf(String.valueOf(value)));
    }

    /**
     * If there is some plugin of the object type specified in field, use the
     * plugin object version.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field metadata.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On set errors.
     */
    protected void setEntity(Object instance, Field f, Object value) throws Exception {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("LOOKUP(" + f.getSpecificType() + ")");
        }
        Object obj = SRServices.getObjectManager().get(f.getSpecificType(), String.valueOf(value));
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("FOUND(" + obj + ")");
        }
        PropertyUtils.setProperty(instance, f.getFullName(), obj);
    }

    /**
     * Sets an object to a field.
     * 
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set.
     * @throws Exception
     *             On setting errors.
     */
    protected void setObject(Object instance, Field f, Object value) throws Exception {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("OBJECT(" + f.getSpecificType() + ")");
        }
        PropertyUtils.setProperty(instance, f.getFullName(), value);
    }

    /**
     * This method can be and should be overridden to perform save, comparison,
     * etc for updates.
     * 
     * @param context
     *            Test context.
     * @param instance
     *            The object instance.
     * @param row
     *            The row adapter.
     * @param result
     *            The result set.
     * @throws Exception
     *             On action errors.
     */
    protected abstract void action(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception;

    /**
     * Creates the key to be used for storing/recovering object information.
     * 
     * @param instance
     *            The object instance.
     * @return The key corresponding to the object.
     * @throws Exception
     *             On key generation errors.
     */
    public String makeKey(Object instance) throws Exception {
        StringBuilder str = new StringBuilder("");
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("KEYS>" + reference);
        }
        for (int i = 0; i < references.size(); i++) {
            str.append((i == 0 ? "" : separator) + PropertyUtils.getProperty(instance, references.get(i)));
        }
        if (references.isEmpty()) {
            String key = "fake_" + System.currentTimeMillis() + "_" + System.nanoTime();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("FAKE_KEY>" + key);
            }
            str.append(key);
        }
        return str.toString();
    }

    /**
     * Maps the object using the keys before and after Hibernate saving.
     * 
     * @param instance
     *            The object instance.
     * @param keyBefore
     *            The key before saving.
     * @param keyAfter
     *            The key after saving.
     */
    public void mapObject(Object instance, String keyBefore, String keyAfter) {
        keysBefore.put(keyBefore, keyAfter);
        instances.put(keyAfter, instance);
    }

    /**
     * Merge to equivalent plugins.
     * 
     * @param old
     *            Source of copy.
     * @return The merged plugin.
     */
    public AbstractPluginObject merge(AbstractPluginObject old) {
        keysBefore.putAll(old.keysBefore);
        instances.putAll(old.instances);
        return this;
    }

    /**
     * Given a key, returns the corresponding object instance.
     * 
     * @param key
     *            The object key.
     * @return The object.
     * @throws PluginException
     *             If object with the given key is not present in plugin.
     */
    public Object getObject(String key) throws PluginException {
        Object result = instances.get(keysBefore.get(key));
        if (result == null) {
            throw new PluginException("Instance '" + key + "' of '" + typeInstance.getName() + "' not found.");
        }
        return result;
    }

    /**
     * Set of handled objects.
     * 
     * @return A collection of object under control.
     */
    public List<Object> getObjects() {
        return new LinkedList<Object>(instances.values());
    }

    /**
     * Remove an object from mapping.
     * 
     * @param key
     *            The key.
     * @return The object removed.
     * @throws PluginException
     *             On lookup errors.
     */
    public Object removeObject(String key) throws PluginException {
        Object old = keysBefore.get(key);
        keysBefore.remove(key);
        if (old == null) {
            throw new PluginException("Instance '" + key + "' of '" + typeInstance.getName() + "' not found.");
        }
        old = instances.get(old);
        instances.remove(old);
        if (old == null) {
            throw new PluginException("Instance '" + key + "' of '" + typeInstance.getName() + "' not found.");
        }
        return old;
    }
}
