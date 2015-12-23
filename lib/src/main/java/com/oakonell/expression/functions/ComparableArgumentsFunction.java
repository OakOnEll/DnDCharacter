package com.oakonell.expression.functions;

import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;

import java.util.List;

/**
 * Created by Rob on 12/23/2015.
 */
public abstract class ComparableArgumentsFunction implements ExpressionFunction {

    @Override
    public ExpressionValue<?> evaluate(List<ExpressionValue<?>> arguments) {
        ExpressionValue<?> min = arguments.get(0);
        ExpressionType<?> type = min.getType();
        Comparable minVal = (Comparable<?>) min.getValue();
        int size = arguments.size();
        for (int i = 1; i < size; i++) {
            ExpressionValue<?> each = arguments.get(i);
            if (!each.getType().equals(type)) {
                throw new RuntimeException("Unmatched types in arguments to '" + getName() + "' function. First was " + type + ", index=" + i + " was " + each.getType());
            }
            Comparable val = (Comparable<?>) each.getValue();
            if (evaluate(minVal, val)) {
                min = each;
                minVal = val;
            }
        }
        return min;
    }

    abstract protected boolean evaluate(Comparable minVal, Comparable val) ;

    @Override
    public ExpressionType<?> validate(List<ExpressionType<?>> argumentTypes) {
        ExpressionType<?> type = argumentTypes.get(0);
        if (!type.supportsInequality()) {
            throw new RuntimeException("Function '" + getName() + "' only takes types supporting inequality operations. First argument type " + type + " does not.");
        }
        int size = argumentTypes.size();
        for (int i = 1; i < size; i++) {
            ExpressionType<?> eachType = argumentTypes.get(i);
            if (!eachType.equals(type)) {
                throw new RuntimeException("Unmatched types in arguments to '" + getName() + "' function. First was " + type + ", index=" + i + " was " + eachType);
            }
        }
        return type;
    }

}
