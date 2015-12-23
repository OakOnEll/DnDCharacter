package com.oakonell.expression.types;

import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;

/**
 * Created by Rob on 12/23/2015.
 */
public class StringValue extends ExpressionValue<String> {
    public static final StringValue EMPTY_STRING = new StringValue("");
    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    public boolean isString() {
        return false;
    }

    @Override
    public StringType getType() {
        return ExpressionType.STRING_TYPE;
    }

    @Override
    public String getValue() {
        return value;
    }

}
