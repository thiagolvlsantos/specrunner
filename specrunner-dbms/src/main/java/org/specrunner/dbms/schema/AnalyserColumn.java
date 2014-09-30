package org.specrunner.dbms.schema;

import java.util.Iterator;

import org.specrunner.dbms.IAnalyserManager;
import org.specrunner.dbms.IPairListenerManager;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.AbstractAnalyser;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public class AnalyserColumn extends AbstractAnalyser<Table, Column> {

    @Override
    public String analyse(Iterable<Pair<Table, Column>> data, IAnalyserManager analysers, IPairListenerManager manager) {
        StringBuilder sb = new StringBuilder();
        Iterator<Pair<Table, Column>> iter = data.iterator();
        while (iter.hasNext()) {
            sb.append(fireProcess(iter.next()));
        }
        return sb.toString();
    }
}