/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.objects;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.parameters.DontEval;
import org.specrunner.parameters.impl.UtilParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.result.status.Warning;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;
import org.specrunner.util.xom.UtilNode;

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
     * List of generic definition fields.
     */
    protected Map<String, Field> generic = new HashMap<String, Field>();
    /**
     * List of fields.
     */
    protected List<Field> fields = new LinkedList<Field>();
    /**
     * Mapping of key before processing to the ones after processing.
     */
    protected Map<String, String> keysBefore = new HashMap<String, String>();
    /**
     * Mapping of identifiers to object instances.
     */
    protected Map<String, Object> instances = new HashMap<String, Object>();

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
                references.add(s);
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
            loadMapping(context, table);
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
    protected void loadMapping(IContext context, TableAdapter table) throws PluginException {
        try {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Loading object mapping>" + mapping);
            }
            URL file = getClass().getResource(mapping);
            if (file == null) {
                throw new PluginException("The object mapping file '" + mapping + "' not found.");
            }
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Loading object mapping file>" + file);
            }
            ISource source = SpecRunnerServices.get(ISourceFactory.class).newSource(file.toString());
            Document doc = source.getDocument();
            Nodes ns = doc.query("//table");
            if (ns.size() == 0) {
                throw new PluginException("The mapping file must have a table element with the field information.");
            }
            Element n = (Element) ns.get(0);
            TableAdapter ta = UtilNode.newTableAdapter(n);
            if (ta.getRowCount() == 0) {
                throw new PluginException("The mapping file might have at least one row (usually a header) with the generic field information.");
            }
            RowAdapter information = ta.getRow(0);

            // set table properties such as type/separator/id/etc.
            UtilParametrized.setProperties(context, this, n);

            // to replace specific settings back.
            UtilParametrized.setProperties(context, this, table.getElement());

            // set type objects.
            setObjectInformation();

            // load fields.
            List<Field> general = new LinkedList<Field>();
            loadFields(context, information, general);
            for (Field field : general) {
                generic.put(field.getFieldName(), field);
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

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter table) throws PluginException {
        if (isMapped()) {
            PluginObjectManager.get().bind(this);
        }
        for (int i = 0; i < table.getRowCount(); i++) {
            if (i == 0) {
                try {
                    loadFields(context, table.getRow(i), fields);
                    result.addResult(Success.INSTANCE, context.newBlock(table.getRow(i).getElement(), this));
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    result.addResult(Failure.INSTANCE, context.newBlock(table.getRow(i).getElement(), this), e);
                    break;
                }
            } else {
                try {
                    processLine(context, table.getRow(i), result);
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    result.addResult(Failure.INSTANCE, context.newBlock(table.getRow(i).getElement(), this), e);
                }
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
            boolean ignore = false;
            if (cell.hasAttribute("ignore")) {
                ignore = Boolean.parseBoolean(cell.getAttribute("ignore"));
            }

            String fieldName = cell.getValue().trim();
            String name;
            if (cell.hasAttribute("field")) {
                name = cell.getAttribute("field");
            } else {
                name = UtilString.camelCase(fieldName);
            }

            Field f = generic.get(fieldName);
            if (f == null) {
                f = new Field();
                f.setFieldName(fieldName);
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

                String def = null;
                if (cell.hasAttribute("default")) {
                    def = cell.getAttribute("default");
                }
                if (def != null) {
                    f.setDef(def);
                }

                String converter = cell.hasAttribute("converter") ? cell.getAttribute("converter") : null;
                String[] converters = converter != null ? converter.split(",") : new String[0];
                if (f.getConverters() == null || converters.length > 0) {
                    f.setConverters(converters);
                }

                int i = 0;
                List<Object> args = new LinkedList<Object>();
                while (cell.hasAttribute("arg" + i)) {
                    args.add(UtilEvaluator.evaluate(cell.getAttribute("arg" + i), context, true));
                    i++;
                }
                if (f.getArgs() == null || !args.isEmpty()) {
                    f.setArgs(args.toArray(new String[args.size()]));
                }

                String comparator = cell.hasAttribute("comparator") ? cell.getAttribute("comparator") : null;
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
        private String[] converters;
        /**
         * Arguments for converters.
         */
        private String[] args;
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
            this.names = names;
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
            this.types = types;
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
        public String[] getConverters() {
            return converters;
        }

        /**
         * Set converters.
         * 
         * @param converters
         *            The converters.
         */
        public void setConverters(String[] converters) {
            this.converters = converters;
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
            this.args = args;
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
            StringBuilder strConvs = new StringBuilder("");
            for (int i = 0; converters != null && i < converters.length; i++) {
                strConvs.append((i == 0 ? "" : ",") + converters[i]);
            }
            StringBuilder strArgs = new StringBuilder("");
            for (int i = 0; args != null && i < args.length; i++) {
                strArgs.append((i == 0 ? "" : ",") + args[i]);
            }
            return index + ",'" + fieldName + "'(" + ignore + ")," + strNames + "( default '" + def + "')," + strTypes + ",[" + strConvs + "],[" + strArgs + "],(" + comparator + ")";
        }
    }

    /**
     * Says if the instance show be mapped or not.
     * 
     * @return true, to map, false, otherwise.
     */
    protected abstract boolean isMapped();

    /**
     * Process a given row.
     * 
     * @param context
     *            The context.
     * @param row
     *            The row.
     * @param result
     *            The result set.
     * @throws Exception
     *             On processing errors.
     */
    protected void processLine(IContext context, RowAdapter row, IResultSet result) throws Exception {
        Object instance = create(row);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("CREATE:" + instance);
        }
        if (populate(instance, context, row, result)) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("POPULATE:" + instance);
            }
            String keyBefore = makeKey(instance);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("KEYBEFORE:" + keyBefore);
            }
            Object obj = instances.get(keysBefore.get(keyBefore));
            if (obj != null && isMapped()) {
                result.addResult(Failure.INSTANCE, context.newBlock(row.getElement(), this), new PluginException("Key '" + keyBefore + "' already used by :" + obj));
                for (CellAdapter cell : row.getCells()) {
                    String title = cell.hasAttribute("title") ? cell.getAttribute("title") : "|";
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
                    result.addResult(Success.INSTANCE, context.newBlock(row.getElement(), this));
                }
            }
        }
    }

    /**
     * Create the object based on a row.
     * 
     * @param row
     *            The row.
     * @return The object corresponding to the row.
     * @throws Exception
     *             On creation errors.
     */
    protected Object create(RowAdapter row) throws Exception {
        Object result = null;
        if (creator != null) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("CREATOR_ROW(" + row + ")");
            }
            result = creatorInstance.create(typeInstance, row);
        }
        if (result == null) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("NEW_INSTANCE_ROW(" + row + ")");
            }
            result = typeInstance.newInstance();
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
     * @param row
     *            The row with attribute informations.
     * @param result
     *            The result set.
     * @return The object filled with row information.
     * @throws Exception
     *             On population errors.
     */
    protected boolean populate(Object instance, IContext context, RowAdapter row, IResultSet result) throws Exception {
        boolean ok = true;
        for (int i = 0; i < fields.size(); i++) {
            CellAdapter cell = row.getCell(i);
            try {
                Field f = fields.get(i);
                if (f.isIgnore()) {
                    continue;
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("ON>" + f.getFullName());
                }
                String text = cell.getElement().getValue();
                Object value = text;
                if (text.isEmpty()) {
                    value = UtilEvaluator.evaluate(f.def, context, true);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("USING_DEFAULT>" + value);
                    }
                }
                Class<?> t = f.getTypes()[f.getTypes().length - 1];
                if (!t.isInstance(value)) {
                    String[] convs = f.converters;
                    if (cell.hasAttribute("converter")) {
                        convs = cell.getAttribute("converter").split(",");
                    }
                    for (int j = 0; j < convs.length; j++) {
                        IConverter con = SpecRunnerServices.get(IConverterManager.class).get(convs[j]);
                        if (con != null) {
                            value = con.convert(value, f.args);
                        } else {
                            throw new ConverterException("Converter named '" + convs[j] + "' not found.");
                        }
                    }
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("VALUE>" + value);
                }
                setValue(row, instance, f, value);
                String title = cell.hasAttribute("title") ? cell.getAttribute("title") : "";
                cell.setAttribute("title", title + "|toString()=" + PropertyUtils.getProperty(instance, f.getFullName()));
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.newBlock(cell.getElement(), this), e);
                ok = false;
            }
        }
        if (!ok) {
            result.addResult(Warning.INSTANCE, context.newBlock(row.getElement(), this), "Could not create instance.");
        }
        return ok;
    }

    /**
     * Set the field with the given value.
     * 
     * @param row
     *            The row corresponding to the object.
     * @param instance
     *            The object instance.
     * @param f
     *            The field information.
     * @param value
     *            The value to be set in instance for field 'f'.
     * @throws Exception
     *             On set value erros.
     */
    protected void setValue(RowAdapter row, Object instance, Field f, Object value) throws Exception {
        // populate inner objects
        Object current = instance;
        for (int i = 0; i < f.names.length - 1; i++) {
            Object tmp = PropertyUtils.getProperty(current, f.names[i]);
            if (tmp == null) {
                if (creator != null) {
                    tmp = creatorInstance.create(f.types[i], row);
                }
                if (tmp == null) {
                    tmp = f.types[i].newInstance();
                }
                PropertyUtils.setProperty(current, f.names[i], tmp);
            }
            current = tmp;
        }
        if (value == null) {
            setObject(instance, f, null);
        } else if (f.getSpecificType() == boolean.class || f.getSpecificType() == Boolean.class) {
            setBoolean(instance, f, value);
        } else if (f.getSpecificType() == char.class || f.getSpecificType() == Character.class) {
            setChar(instance, f, value);
        } else if (f.getSpecificType() == short.class || f.getSpecificType() == Short.class) {
            setShort(instance, f, value);
        } else if (f.getSpecificType() == int.class || f.getSpecificType() == Integer.class) {
            setInteger(instance, f, value);
        } else if (f.getSpecificType() == long.class || f.getSpecificType() == Long.class) {
            setLong(instance, f, value);
        } else if (f.getSpecificType() == float.class || f.getSpecificType() == Float.class) {
            setFloat(instance, f, value);
        } else if (f.getSpecificType() == double.class || f.getSpecificType() == Double.class) {
            setDouble(instance, f, value);
        } else if (PluginObjectManager.get().isBound(f.getSpecificType())) {
            setEntity(instance, f, value);
        } else {
            setObject(instance, f, value);
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
        Object obj = PluginObjectManager.get().lookup(f.getSpecificType(), String.valueOf(value));
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
}
