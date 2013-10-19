package org.specrunner.source.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;

/**
 * Convert a reference to an object in mapped resources.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDataTable implements IConverter {

    @Override
    public void initialize() {
    }

    @Override
    public boolean accept(Object value) {
        return true;
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (!(value instanceof Node)) {
            throw new ConverterException("Invalid data table: " + value + ", args=" + Arrays.toString(args));
        }
        Node node = (Node) value;
        DataTable result = new DataTable();
        Nodes rows = node.query("descendant::tr");
        Node row = rows.get(0);
        Nodes ths = row.query("child::th");
        for (int i = 0; i < ths.size(); i++) {
            result.add(ths.get(i).getValue());
        }
        for (int i = 1; i < rows.size(); i++) {
            List<String> example = new LinkedList<String>();
            row = rows.get(i);
            Nodes tds = row.query("child::td");
            for (int j = 0; j < tds.size(); j++) {
                example.add(tds.get(j).getValue());
            }
            result.add(example);
        }
        return result;
    }
}