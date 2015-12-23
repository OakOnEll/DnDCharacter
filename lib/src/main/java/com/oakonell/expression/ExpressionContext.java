package com.oakonell.expression;

import com.oakonell.expression.functions.ExpressionFunction;

/**
 * Created by Rob on 12/23/2015.
 */
public class ExpressionContext {
    private final ExpressionFunctionContext functionContext;
    private final ExpressionVariableContext variableContext;

    public ExpressionContext(ExpressionFunctionContext functionContext, ExpressionVariableContext variableContext) {
        this.functionContext = functionContext;
        this.variableContext = variableContext;
    }

    public ExpressionFunction getFunction(String name) {
        return functionContext.getFunction(name);
    }

    public ExpressionType<?> getVariableType(String name) {
        return variableContext.getType(name);
    }

    public ExpressionValue<?> getVariableValue(String name) {
        return variableContext.getValue(name);
    }
}
