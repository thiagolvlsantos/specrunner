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
package org.specrunner.dumper.core;

import java.io.File;

/**
 * Placeholders for output files.
 * 
 * @author Thiago Santos
 * 
 */
public final class ConstantsDumperFile {

    /**
     * Default constructor.
     */
    private ConstantsDumperFile() {
        super();
    }

    /**
     * The output directory. The default value is 'target/output'.
     */
    public static final String FEATURE_OUTPUT_DIRECTORY = ConstantsDumperFile.class.getName() + ".outputDirectory";

    /**
     * Default output directory.
     */
    public static final File DEFAULT_OUTPUT_DIRECTORY = new File("target/output");

    /**
     * The output file name. The default name is the same of the input file.
     */
    public static final String FEATURE_OUTPUT_NAME = ConstantsDumperFile.class.getName() + ".outputName";
}