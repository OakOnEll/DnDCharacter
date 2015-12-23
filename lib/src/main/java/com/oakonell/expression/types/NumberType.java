package com.oakonell.expression.types;

import com.oakonell.expression.ExpressionType;

/**
 * Created by Rob on 12/23/2015.
 */
public class NumberType extends ExpressionType<Integer> {
    public boolean isNumber() {
        return true;
    }

}
