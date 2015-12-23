package com.oakonell.expression;


/**
 * Created by Rob on 12/23/2015.
 */
public interface ExpressionVariableContext {
    ExpressionType<?> getType(String name);

    ExpressionValue<?> getValue(String name);
}
