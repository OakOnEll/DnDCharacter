package com.oakonell.expression.context;

import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;
import com.oakonell.expression.ExpressionVariableContext;
import com.oakonell.expression.types.BooleanValue;
import com.oakonell.expression.types.NumberValue;
import com.oakonell.expression.types.StringValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 12/23/2015.
 */
public class SimpleVariableContext implements ExpressionVariableContext {
    final Map<String, ExpressionValue<?>> variables = new HashMap<>();

    @Override
    public ExpressionType<?> getType(String name) {
        ExpressionValue<?> value = variables.get(name);
        if (value == null) return null;
        return value.getType();
    }

    @Override
    public ExpressionValue<?> getValue(String name) {
        return variables.get(name);
    }

    public void registerString(String name) {
        if (variables.containsKey(name)) return;
        variables.put(name, StringValue.EMPTY_STRING);
    }

    public void registerNumber(String name) {
        if (variables.containsKey(name)) return;
        variables.put(name, NumberValue.ZERO);
    }

    public void registerBoolean(String name) {
        if (variables.containsKey(name)) return;
        variables.put(name, BooleanValue.FALSE);
    }

    public void setNumber(String name, int value) {
        ExpressionValue<?> existing = variables.get(name);
        if (existing != null) {
            if (!existing.isNumber()) {
                throw new RuntimeException("Can't change type of variable '" + name + "' from " + existing + " to number");
            }
        }
        variables.put(name, new NumberValue(value));
    }

    public void setBoolean(String name, boolean value) {
        ExpressionValue<?> existing = variables.get(name);
        if (existing != null) {
            if (!existing.isBoolean()) {
                throw new RuntimeException("Can't change type of variable '" + name + "' from " + existing + " to boolean");
            }
        }
        variables.put(name, new BooleanValue(value));
    }

    public void setString(String name, String value) {
        ExpressionValue<?> existing = variables.get(name);
        if (existing != null) {
            if (!existing.isString()) {
                throw new RuntimeException("Can't change type of variable '" + name + "' from " + existing + " to string");
            }
        }
        variables.put(name, new StringValue(value));
    }
}
