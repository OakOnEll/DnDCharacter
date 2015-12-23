package com.oakonell.expression;

import com.oakonell.expression.functions.ExpressionFunction;

/**
 * Created by Rob on 12/23/2015.
 */
public interface ExpressionFunctionContext {
    ExpressionFunction getFunction(String name);

    void add(ExpressionFunction each);
}
