package com.oakonell.expression;

import com.oakonell.expression.types.BooleanType;
import com.oakonell.expression.types.NumberType;
import com.oakonell.expression.types.StringType;

/**
 * Created by Rob on 12/23/2015.
 */
public class ExpressionType<T> {
    public static final NumberType NUMBER_TYPE = new NumberType();
    public static final BooleanType BOOLEAN_TYPE = new BooleanType();
    public static final StringType STRING_TYPE = new StringType();

    public boolean supportsInequality() {
        return true;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isString() {
        return false;
    }}
