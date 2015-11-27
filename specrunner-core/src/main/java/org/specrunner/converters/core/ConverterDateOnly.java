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
package org.specrunner.converters.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.specrunner.source.resource.ResourceException;
import org.specrunner.util.UtilIO;

/**
 * Create current date without without hour information.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateOnly extends ConverterDateCurrentTemplate {

    private ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM/dd/yyyy");
        };
    };

    /**
     * Basic data converter.
     * 
     * @throws ResourceException
     *             On load errors.
     */
    public ConverterDateOnly() throws ResourceException {
        super(UtilIO.readLines("sr_converters_date.txt"));
    }

    @Override
    protected Date instance() {
        Calendar c = getCalendar();
        String tmp = formatter.get().format(c.getTime());
        try {
            Date parse = formatter.get().parse(tmp);
            c.setTime(parse);
            return c.getTime();
        } catch (ParseException e) {
            return null;
        }
    }
}
