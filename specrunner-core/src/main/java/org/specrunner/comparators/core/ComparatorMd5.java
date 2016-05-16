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
package org.specrunner.comparators.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import org.specrunner.source.core.UtilEncoding;
import org.specrunner.util.UtilIO;

/**
 * Compare two objects using MD5 value.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ComparatorMd5 extends ComparatorString {

    private MessageDigest digester;

    @Override
    public Class<?> getType() {
        return InputStream.class;
    }

    @Override
    protected String toString(Object obj) {
        if (digester == null) {
            try {
                digester = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Could not generate MD5 for value.", e);
            }
        }
        digester.reset();
        byte[] bytes = null;
        if (byte[].class.isInstance(obj)) {
            bytes = (byte[]) obj;
        } else if (obj instanceof InputStream) {
            InputStream in = (InputStream) obj;
            try {
                bytes = UtilIO.getBytes(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (obj instanceof Blob) {
            Blob blob = (Blob) obj;
            try {
                bytes = UtilIO.getBytes(blob.getBinaryStream());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (obj instanceof Reader) {
            Reader in = (Reader) obj;
            try {
                bytes = UtilIO.getBytes(in, Charset.forName(UtilEncoding.getEncoding()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            try {
                bytes = UtilIO.getBytes(clob.getCharacterStream(), Charset.forName(UtilEncoding.getEncoding()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String tmp = String.valueOf(obj);
            bytes = tmp != null ? tmp.getBytes() : null;
        }
        if (bytes == null) {
            return "null";
        }
        digester.update(bytes);
        return String.valueOf(new BigInteger(1, digester.digest()));
    }
}
