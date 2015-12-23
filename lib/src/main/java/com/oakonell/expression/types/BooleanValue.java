package com.oakonell.expression.types;

import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;

/**
 * Created by Rob on 12/23/2015.
 */
public class BooleanValue extends ExpressionValue<Boolean> {
    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);

    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean isBoolean() {
        return true;
    }

    @Override
    public BooleanType getType() {
        return ExpressionType.BOOLEAN_TYPE;
    }

    @Override
    public Boolean getValue() {
        return value;
    }
}
