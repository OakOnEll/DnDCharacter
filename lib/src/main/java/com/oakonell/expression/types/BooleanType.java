package com.oakonell.expression.types;

import com.oakonell.expression.ExpressionType;

/**
 * Created by Rob on 12/23/2015.
 */
public class BooleanType extends ExpressionType<Boolean> {
    public boolean supportsInequality() {
        return false;
    }

    public boolean isBoolean() {
        return true;
    }
}
