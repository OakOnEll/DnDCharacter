package com.oakonell.expression.functions;

import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;

import java.util.List;

/**
 * Created by Rob on 12/23/2015.
 */
public interface ExpressionFunction {
    String getName();

    ExpressionValue<?> evaluate(List<ExpressionValue<?>> arguments);

    ExpressionType<?> validate(List<ExpressionType<?>> argumentTypes);
}
