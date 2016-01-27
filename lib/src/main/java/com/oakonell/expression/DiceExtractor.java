package com.oakonell.expression;

import com.oakonell.expression.grammar.ExpressionParser;
import com.oakonell.expression.types.NumberValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 12/29/2015.
 */
public class DiceExtractor extends ExpressionEvaluator {
    private final Map<Integer, Integer> results = new HashMap<>();

    public static Map<Integer, Integer> extractDieRolls(Expression<?> expression) {
        DiceExtractor extractor = new DiceExtractor(expression.getContext());
        ExpressionParser parser = expression.getParser();
        parser.reset();
        extractor.visitRoot(parser.root());
        return extractor.results;
    }

    private DiceExtractor(ExpressionContext context) {
        super(context);
    }

    @Override
    protected NumberValue dieExpression(int numRolls, int dieSides) {
        Integer number = results.get(dieSides);
        if (number == null) number = 0;
        results.put(dieSides, number + numRolls);
        return super.dieExpression(numRolls, dieSides);
    }
}
