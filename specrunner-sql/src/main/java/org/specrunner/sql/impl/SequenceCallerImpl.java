package org.specrunner.sql.impl;

import org.specrunner.sql.ISequenceCaller;

public class SequenceCallerImpl implements ISequenceCaller {

    @Override
    public String nextValue(String sequence) {
        return "NEXT VALUE FOR " + sequence;
    }
}
