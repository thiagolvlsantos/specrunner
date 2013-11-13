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
package org.specrunner.objects;

import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;
import org.specrunner.util.impl.CellAdapter;
import org.specrunner.util.impl.RowAdapter;
import org.specrunner.util.impl.TableAdapter;

/**
 * Generic object plugin. To write object plugins override method
 * <code>isMapped()</code> and <code>action(...)</code>. i.e.
 * SpecRunner-Hibernate3 extends the object manipulation to save data using
 * Hibernate3 infra-structure.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginObject extends AbstractPluginTable {

    protected String type;
    protected Class<?> typeInstance;
    protected String creator;
    protected IObjectCreator creatorInstance;
    protected String reference;
    protected String separator;
    protected List<Field> fields = new LinkedList<Field>();
    protected Map<String, String> keysBefore = new HashMap<String, String>();
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
    public void setReference(String reference) {
        this.reference = reference;
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

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
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
    }

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter table) throws PluginException {
        if (isMapped()) {
            PluginObjectManager.get().bind(this);
        }
        for (int i = 0; i < table.getRowCount(); i++) {
            if (i == 0) {
                try {
                    loadFields(context, table.getRow(i));
                    result.addResult(Status.SUCCESS, context.newBlock(table.getRow(i).getElement(), this));
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    result.addResult(Status.FAILURE, context.newBlock(table.getRow(i).getElement(), this), e);
                    break;
                }
            } else {
                try {
                    processLine(context, table.getRow(i), result);
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    result.addResult(Status.FAILURE, context.newBlock(table.getRow(i).getElement(), this), e);
                }
            }
        }
    }

    /**
     * Load fields based on <code>th</code> tags.
     * 
     * @param row
     * @throws Exception
     */
    protected void loadFields(IContext context, RowAdapter row) throws Exception {
        int index = 0;
        for (CellAdapter cell : row.getCells()) {
            String name;
            if (cell.hasAttribute("field")) {
                name = cell.getAttribute("field");
            } else {
                name = normalize(cell.getValue());
            }
            StringTokenizer st = new StringTokenizer(name, ".");
            String[] names = new String[st.countTokens()];
            for (int i = 0; i < names.length; i++) {
                names[i] = st.nextToken();
            }
            Class<?>[] types = new Class<?>[names.length];
            Class<?> currentType = typeInstance;
            for (int i = 0; i < types.length; i++) {
                Method m = null;
                try {
                    m = currentType.getMethod("get" + Character.toUpperCase(names[i].charAt(0)) + names[i].substring(1));
                } catch (Exception e) {
                    m = currentType.getMethod("is" + Character.toUpperCase(names[i].charAt(0)) + names[i].substring(1));
                }
                if (m == null) {
                    throw new PluginException("Getter method for " + names[i] + " not found.");
                }
                types[i] = m.getReturnType();
                currentType = types[i];
            }

            String def = null;
            if (cell.hasAttribute("default")) {
                def = cell.getAttribute("default");
            }
            String converter = cell.hasAttribute("converter") ? cell.getAttribute("converter") : null;
            int i = 0;
            List<Object> args = new LinkedList<Object>();
            while (cell.hasAttribute("arg" + i)) {
                args.add(UtilEvaluator.evaluate(cell.getAttribute("arg" + i), context));
                i++;
            }
            String comparator = cell.hasAttribute("comparator") ? cell.getAttribute("comparator") : null;
            Field f = new Field(index++, names, def, types, converter != null ? converter.split(",") : new String[0], args.toArray(new String[args.size()]), comparator);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("FIELD>" + f);
            }
            fields.add(f);
        }
    }

    /**
     * Normalize attributes.
     * 
     * @param text
     *            The text to be normalized.
     * @return The normalized string.
     */
    public static String normalize(String text) {
        StringTokenizer st = new StringTokenizer(text, " \t\r\n");
        String str = st.nextToken().toLowerCase();
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            str += Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
        return clean(str);
    }

    /**
     * Remove all Latin characters.
     * 
     * @param str
     *            The string to be normalized.
     * @return The cleaned string.
     */
    public static String clean(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Hold column information.
     * 
     * @author Thiago Santos
     * 
     */
    public static class Field {
        private int index;
        private String[] names;
        private Class<?>[] types;
        private String def;
        private String[] converters;
        private String[] args;
        private String comparator;

        public Field(int index, String[] names, String def, Class<?>[] type, String[] converters, String[] args, String comparator) {
            this.index = index;
            this.names = names;
            this.def = def;
            this.types = type;
            this.converters = converters;
            this.args = args;
            this.comparator = comparator;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

        public Class<?>[] getTypes() {
            return types;
        }

        public void setTypes(Class<?>[] types) {
            this.types = types;
        }

        public String getDef() {
            return def;
        }

        public void setDef(String def) {
            this.def = def;
        }

        public String[] getConverters() {
            return converters;
        }

        public void setConverters(String[] converters) {
            this.converters = converters;
        }

        public String[] getArgs() {
            return args;
        }

        public void setArgs(String[] args) {
            this.args = args;
        }

        public String getComparator() {
            return comparator;
        }

        public void setComparator(String comparator) {
            this.comparator = comparator;
        }

        public String getFullName() {
            String strNames = "";
            for (int i = 0; i < names.length; i++) {
                strNames += (i == 0 ? "" : ".") + names[i];
            }
            return strNames;
        }

        public Class<?> getSpecificType() {
            return types[types.length - 1];
        }

        @Override
        public String toString() {
            String strNames = "";
            for (int i = 0; i < names.length; i++) {
                strNames += (i == 0 ? "" : ",") + names[i];
            }
            String strTypes = "";
            for (int i = 0; i < types.length; i++) {
                strTypes += (i == 0 ? "" : ",") + types[i];
            }
            String strConvs = "";
            for (int i = 0; i < converters.length; i++) {
                strConvs += (i == 0 ? "" : ",") + converters[i];
            }
            String strArgs = "";
            for (int i = 0; i < args.length; i++) {
                strArgs += (i == 0 ? "" : ",") + args[i];
            }
            return index + "," + strNames + "( default '" + def + "')," + strTypes + ",[" + strConvs + "],[" + strArgs + "],(" + comparator + ")";
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
                result.addResult(Status.FAILURE, context.newBlock(row.getElement(), this), new PluginException("Key '" + keyBefore + "' already used by :" + obj));
                for (CellAdapter cell : row.getCells()) {
                    String title = cell.getAttribute("title");
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
                    result.addResult(Status.SUCCESS, context.newBlock(row.getElement(), this));
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
     *            The tes context.
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
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("ON>" + f.getFullName());
                }
                String text = cell.getElement().getValue();
                if (text.isEmpty() && f.def != null) {
                    text = f.def;
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("USING_DEFAULT>" + text);
                    }
                }
                Object value = text;
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
                result.addResult(Status.FAILURE, context.newBlock(cell.getElement(), this), e);
                ok = false;
            }
        }
        if (!ok) {
            result.addResult(Status.WARNING, context.newBlock(row.getElement(), this), "Could not create instance.");
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
        if (f.getSpecificType() == boolean.class || f.getSpecificType() == Boolean.class) {
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

    protected void setBoolean(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Boolean.valueOf(String.valueOf(value)));
    }

    protected void setChar(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Character.valueOf(String.valueOf(value).charAt(0)));
    }

    protected void setShort(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Short.valueOf(String.valueOf(value)));
    }

    protected void setInteger(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Integer.valueOf(String.valueOf(value)));
    }

    protected void setLong(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Long.valueOf(String.valueOf(value)));
    }

    protected void setFloat(Object instance, Field f, Object value) throws Exception {
        PropertyUtils.setProperty(instance, f.getFullName(), Float.valueOf(String.valueOf(value)));
    }

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

    protected void setObject(Object instance, Field f, Object value) throws Exception {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("OBJECT(" + f.getSpecificType() + ")");
        }
        PropertyUtils.setProperty(instance, f.getFullName(), value);
    }

    /**
     * This method can be and should be overridden to perform save, comparation,
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
     *             On key generation erros.
     */
    public String makeKey(Object instance) throws Exception {
        String str = "";
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("KEYS>" + reference);
        }
        String[] keyFields = reference.split(",");
        for (int i = 0; i < keyFields.length; i++) {
            str += (i == 0 ? "" : separator) + PropertyUtils.getProperty(instance, keyFields[i]);
        }
        return str;
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
