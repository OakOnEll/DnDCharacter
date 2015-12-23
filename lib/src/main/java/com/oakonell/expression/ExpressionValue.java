package com.oakonell.expression;

/**
 * Created by Rob on 12/23/2015.
 */
public abstract class ExpressionValue<T> {
    public abstract ExpressionType<T> getType();

    public abstract T getValue();

    public boolean isNumber() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isString() {
        return false;
    }

}
