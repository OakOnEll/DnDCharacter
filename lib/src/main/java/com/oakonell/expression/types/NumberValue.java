package com.oakonell.expression.types;

import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;

/**
 * Created by Rob on 12/23/2015.
 */
public class NumberValue extends ExpressionValue<Integer> {
    public static NumberValue ZERO = new NumberValue(0);
    private final int value;

    public NumberValue(int value) {
        this.value = value;
    }

    public boolean isNumber() {
        return true;
    }

    @Override
    public NumberType getType() {
        return ExpressionType.NUMBER_TYPE;
    }

    @Override
    public Integer getValue() {
        return value;
    }

}
