package com.oakonell.expression.functions;

/**
 * Created by Rob on 12/23/2015.
 */
public class MaxFunction extends ComparableArgumentsFunction {
    @Override
    public String getName() {
        return "max";
    }

    protected boolean evaluate(Comparable minVal, Comparable val) {
        return val.compareTo(minVal) > 0;
    }

}
