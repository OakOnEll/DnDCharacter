package com.oakonell.expression;

import com.oakonell.expression.functions.ExpressionFunction;
import com.oakonell.expression.functions.RandomDieEvaluator;

/**
 * Created by Rob on 12/23/2015.
 */
public class ExpressionContext {
    private final ExpressionFunctionContext functionContext;
    private final ExpressionVariableContext variableContext;
    private final DieEvaluator dieEvaluator;

    public ExpressionContext(ExpressionFunctionContext functionContext, ExpressionVariableContext variableContext, DieEvaluator dieEvaluator) {
        this.functionContext = functionContext;
        this.variableContext = variableContext;
        if (dieEvaluator == null) {
            this.dieEvaluator = new RandomDieEvaluator();
        } else {
            this.dieEvaluator = dieEvaluator;
        }
    }

    public ExpressionContext(ExpressionFunctionContext functionContext, ExpressionVariableContext variableContext) {
        this(functionContext, variableContext, null);
    }

    public int evaluateDie(int die) {
        return dieEvaluator.evaluate(die);
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
