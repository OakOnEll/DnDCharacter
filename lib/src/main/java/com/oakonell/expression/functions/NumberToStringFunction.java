package com.oakonell.expression.functions;

import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;
import com.oakonell.expression.types.NumberValue;
import com.oakonell.expression.types.StringValue;

import java.util.List;

/**
 * Created by Rob on 12/12/2016.
 */

public class NumberToStringFunction implements ExpressionFunction {

    @Override
    public String getName() {
        return "numToString";
    }

    @Override
    public ExpressionValue<?> evaluate(List<ExpressionValue<?>> arguments) {
        NumberValue number = (NumberValue) arguments.get(0);
        return new StringValue(number.getValue().toString());
    }


    @Override
    public ExpressionType<?> validate(List<ExpressionType<?>> argumentTypes) {
        if (argumentTypes.size() != 1 || !argumentTypes.get(0).isNumber()) {
            throw new RuntimeException("Function '" + getName() + "' only takes one numerical argument.");
        }
        return ExpressionType.STRING_TYPE;
    }


}
