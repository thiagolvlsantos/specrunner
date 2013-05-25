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
package org.specrunner.expressions.impl;

import java.util.Arrays;

/**
 * Expression key.
 */
public class ExpressionKey {

    /**
     * A cache key instance by thread.
     */
    private static ThreadLocal<ExpressionKey> instance = new ThreadLocal<ExpressionKey>() {
        @Override
        protected ExpressionKey initialValue() {
            return new ExpressionKey();
        };
    };

    /**
     * The expression.
     */
    private String expression;
    /**
     * The return type.
     */
    private Class<?> returnType;
    /**
     * The argument names.
     */
    private String[] arrayArgs;

    /**
     * The argument types.
     */
    private Class<?>[] arrayTypes;

    /**
     * Basic constructor.
     */
    public ExpressionKey() {
    }

    /**
     * Basic constructor.
     * 
     * @param expression
     *            The expression.
     * @param returnType
     *            The return type.
     * @param arrayArgs
     *            Argument names.
     * @param arrayTypes
     *            Argument types.
     */
    public ExpressionKey(String expression, Class<Object> returnType, String[] arrayArgs, Class<?>[] arrayTypes) {
        this.expression = expression;
        this.returnType = returnType;
        this.arrayArgs = Arrays.copyOf(arrayArgs, arrayArgs.length);
        this.arrayTypes = Arrays.copyOf(arrayTypes, arrayTypes.length);
    }

    /**
     * Assign information to a unique instance.
     * 
     * @param expression
     *            The expression.
     * @param returnType
     *            The return type.
     * @param arrayArgs
     *            The arguments names.
     * @param arrayTypes
     *            The parameter types.
     * @return The expression key.
     */
    public static ExpressionKey unique(String expression, Class<?> returnType, String[] arrayArgs, Class<?>[] arrayTypes) {
        ExpressionKey key = instance.get();
        key.expression = expression;
        key.returnType = returnType;
        key.arrayArgs = arrayArgs;
        key.arrayTypes = arrayTypes;
        return key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expression == null) ? 0 : expression.hashCode());
        result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
        result = prime * result + Arrays.hashCode(arrayArgs);
        result = prime * result + Arrays.hashCode(arrayTypes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExpressionKey other = (ExpressionKey) obj;
        if (expression == null) {
            if (other.expression != null) {
                return false;
            }
        } else if (!expression.equals(other.expression)) {
            return false;
        }
        if (returnType == null) {
            if (other.returnType != null) {
                return false;
            }
        } else if (!returnType.equals(other.returnType)) {
            return false;
        }
        if (!Arrays.equals(arrayArgs, other.arrayArgs)) {
            return false;
        }
        if (!Arrays.equals(arrayTypes, other.arrayTypes)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return expression + "," + returnType + ",(" + Arrays.toString(arrayArgs) + "),(" + Arrays.toString(arrayTypes) + ")";
    }
}