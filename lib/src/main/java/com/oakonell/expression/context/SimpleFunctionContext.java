package com.oakonell.expression.context;

import com.oakonell.expression.ExpressionFunctionContext;
import com.oakonell.expression.functions.ExpressionFunction;
import com.oakonell.expression.functions.MaxFunction;
import com.oakonell.expression.functions.MinFunction;
import com.oakonell.expression.functions.NumberToStringFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 12/23/2015.
 */
public class SimpleFunctionContext implements ExpressionFunctionContext {
    private static final Set<ExpressionFunction> STANDARD_FUNCTIONS;

    static {
        Set<ExpressionFunction> functions = new HashSet<>();
        functions.add(new MaxFunction());
        functions.add(new MinFunction());
        functions.add(new NumberToStringFunction());
        STANDARD_FUNCTIONS = Collections.unmodifiableSet(functions);
    }

    private final Map<String, ExpressionFunction> functions = new HashMap<>();

    public SimpleFunctionContext() {
        for (ExpressionFunction each : STANDARD_FUNCTIONS) {
            add(each);
        }
    }

    @Override
    public ExpressionFunction getFunction(String name) {
        return functions.get(name);
    }

    @Override
    public void add(ExpressionFunction each) {
        ExpressionFunction old = functions.get(each.getName());
        if (old != null) {
            throw new RuntimeException("Function '" + each.getName() + "' already exists!");
        }
        functions.put(each.getName(), each);
    }
}
