package com.oakonell.expression;

import com.oakonell.expression.context.SimpleFunctionContext;
import com.oakonell.expression.context.SimpleVariableContext;
import com.oakonell.expression.functions.RandomDieEvaluator;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Rob on 12/22/2015.
 */
public class ExpressionTest {
    @Test
    public void testDieExtraction() {
        SimpleVariableContext variableContext = new SimpleVariableContext();
        variableContext.registerNumber("x");

        DieEvaluator dieEvaluator = new RandomDieEvaluator(1);
        // d4 results = 3,1

        Expression<Integer> expr = Expression.parse("x + 1d4", ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext, dieEvaluator));

        Map<Integer, Integer> dice = DiceExtractor.extractDieRolls(expr);
        assertEquals(1, dice.size());
        assertEquals(1, (int) dice.get(4));


        expr = Expression.parse("x + d4 + 2d6 + 2d4", ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext, dieEvaluator));

        dice = DiceExtractor.extractDieRolls(expr);
        assertEquals(2, dice.size());
        assertEquals(3, (int) dice.get(4));
        assertEquals(2, (int) dice.get(6));
    }

    @Test
    public void testDie() {
        SimpleVariableContext variableContext = new SimpleVariableContext();
        variableContext.registerNumber("x");

        DieEvaluator dieEvaluator = new RandomDieEvaluator(1);
        // d4 results = 3,1

        Expression<Integer> expr = Expression.parse("x + 1d4", ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext, dieEvaluator));
        assertEquals(3, expr.evaluate().intValue());
        variableContext.setNumber("x", 5);
        assertEquals(6, expr.evaluate().intValue());

        // d8 results 4,4
        expr = Expression.parse("x + 1d8", ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext, dieEvaluator));
        assertEquals(9, expr.evaluate().intValue());
        variableContext.setNumber("x", 6);
        assertEquals(10, expr.evaluate().intValue());

    }

    @Test
    public void testDieParseFail() {
        SimpleVariableContext variableContext = new SimpleVariableContext();
        variableContext.registerNumber("x");

        DieEvaluator dieEvaluator = new RandomDieEvaluator(1);
        // d4 results = 3,1,2,2

        try {
            Expression<Integer> expr = Expression.parse("x + 'z'd4", ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext, dieEvaluator));
            fail("should fail");
        } catch (Exception e) {
            // expected
            assertTrue("Exception is wrong: " + e.getMessage(), e.getMessage().contains("Number of die"));
            assertTrue("Exception is wrong: " + e.getMessage(), e.getMessage().contains("should be a number"));
        }

    }

    @Test
    public void testVariables() throws IOException {
        SimpleVariableContext variableContext = new SimpleVariableContext();
        variableContext.registerNumber("x");

        Expression<Integer> expr = Expression.parse("x + 5", ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertEquals(5, expr.evaluate().intValue());
        variableContext.setNumber("x", 5);
        assertEquals(10, expr.evaluate().intValue());
    }

    @Test
    public void testMissingVariable() {
        try {
            Expression<Integer> expr = Expression.parse("x + 5", ExpressionType.NUMBER_TYPE, emptyContext());
        } catch (Exception e) {
            assertTrue("Exception is wrong: " + e.getMessage(), e.getMessage().contains("No variable named"));
        }

    }

    @Test
    public void testValidationFail() throws IOException {
        try {
            validate("5 + 'abc'", ExpressionType.NUMBER_TYPE);
            fail("Should fail");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Error in formula"));
            assertTrue(e.getMessage().contains("should be a number"));
        }

        try {
            validate("5 > 'abc'", ExpressionType.NUMBER_TYPE);
            fail("Should fail");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Error in formula"));
            assertTrue("Exception is wrong: " + e.getMessage(), e.getMessage().contains("the same types"));
        }

        try {
            validate("min( 5 , 'abc')", ExpressionType.NUMBER_TYPE);
            fail("Should fail");
        } catch (Exception e) {
            assertTrue("Exception is wrong: " + e.getMessage(), e.getMessage().contains("Unmatched types"));
        }

        try {
            validate("min( 'xyz' , 'abc')", ExpressionType.NUMBER_TYPE);
            fail("Should fail");
        } catch (Exception e) {
            assertTrue("Exception is wrong: " + e.getMessage(), e.getMessage().contains("Expression "));
            assertTrue("Exception is wrong: " + e.getMessage(), e.getMessage().contains("results in "));
            assertTrue("Exception is wrong: " + e.getMessage(), e.getMessage().contains("but is expecting a"));
        }


    }

    private void validate(String formula, ExpressionType<?> type) throws IOException {
        Expression.parse(formula, type, emptyContext());
    }

    private ExpressionContext emptyContext() {
        return new ExpressionContext(new SimpleFunctionContext(), new SimpleVariableContext());
    }

    @Test
    public void testConditional() throws IOException {
        assertEquals(1, evaluateMath("5>1 ? 1 : 2"));
        assertEquals(5, evaluateMath("5>1 ? 3+ 2 : 2"));
        assertEquals(2, evaluateMath("5<1 ? 1 : 2"));

        assertEquals("true", evaluateString("5>1 ? 'true' : 'false'"));

    }

    @Test
    public void testExpression() throws IOException {
        assertEquals(-12, evaluateMath("3 + 2 + 4 - 21"));
        assertEquals(10, evaluateMath("3 * 2 + 4 "));
        assertEquals(7, evaluateMath("6/  2 + 4 "));
        assertEquals(1, evaluateMath("5%2"));
        assertEquals(2, evaluateMath("5%3"));
        assertEquals(2, evaluateMath("+2"));
        assertEquals(4, evaluateMath("+2 + +2"));
        assertEquals(0, evaluateMath("+2 + -2"));
        assertEquals(4, evaluateMath("min(5,4)"));
        assertEquals(5, evaluateMath("max(5,4)"));
    }

    @Test
    public void testBoolean() throws IOException {
        assertEquals(true, evaluateLogical("true AND true"));
        assertEquals(false, evaluateLogical("true AND false"));
        assertEquals(false, evaluateLogical("false AND true"));
        assertEquals(false, evaluateLogical("false AND false"));

        assertEquals(true, evaluateLogical("true OR true"));
        assertEquals(true, evaluateLogical("true OR false"));
        assertEquals(true, evaluateLogical("false OR true"));
        assertEquals(false, evaluateLogical("false OR false"));

        assertEquals(true, evaluateLogical("5 = 5"));
        assertEquals(true, evaluateLogical("5 != 1"));
        assertEquals(true, evaluateLogical("5 = 4 + 1"));
        assertEquals(true, evaluateLogical("5 != 2-1"));

        assertEquals(false, evaluateLogical("5 = 3"));
        assertEquals(false, evaluateLogical("5 != 5"));
        assertEquals(false, evaluateLogical("5 = 3 + 1"));
        assertEquals(false, evaluateLogical("5 != 3+2"));
    }

    @Test
    public void testString() throws IOException {
        assertEquals("Literal", evaluateString("'Literal'"));
        assertEquals("LiteralABC", evaluateString("'Literal' || 'ABC'"));
        assertEquals("ABC", evaluateString("min('Literal' , 'ABC')"));

        assertEquals("can't", evaluateString("'can''t'"));
    }

    private int evaluateMath(String formula) throws IOException {
        Expression<Integer> expression = Expression.parse(formula, ExpressionType.NUMBER_TYPE, emptyContext());
        return expression.evaluate();
    }

    private Boolean evaluateLogical(String formula) throws IOException {
        Expression<Boolean> expression = Expression.parse(formula, ExpressionType.BOOLEAN_TYPE, emptyContext());
        return expression.evaluate();
    }

    private String evaluateString(String formula) throws IOException {
        Expression<String> expression = Expression.parse(formula, ExpressionType.STRING_TYPE, emptyContext());
        return expression.evaluate();
    }

    @Test
    public void testInequalityAndLogical() {
        SimpleVariableContext variableContext = new SimpleVariableContext();
        variableContext.setNumber("strength", 13);
        variableContext.setNumber("dexterity", 8);
        Expression<Boolean> expression = Expression.parse("strength>=13 AND dexterity >=13", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertFalse(expression.evaluate());

        variableContext.setNumber("strength", 8);
        variableContext.setNumber("dexterity", 13);
        assertFalse(expression.evaluate());

        variableContext.setNumber("strength", 13);
        variableContext.setNumber("dexterity", 13);
        assertTrue(expression.evaluate());
    }

    @Test
    public void testLogicalNotWithAnd() {
        SimpleVariableContext variableContext = new SimpleVariableContext();
        // single
        Expression<Boolean> expression = Expression.parse("NOT false", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertTrue(expression.evaluate());

        expression = Expression.parse("NOT true", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertFalse(expression.evaluate());

        // or'd
        expression = Expression.parse("NOT false OR NOT false", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertTrue(expression.evaluate());

        expression = Expression.parse("NOT false OR NOT true", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertTrue(expression.evaluate());

        expression = Expression.parse("NOT true OR NOT false", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertTrue(expression.evaluate());

        expression = Expression.parse("NOT true OR NOT true", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertFalse(expression.evaluate());

        // and'd
        expression = Expression.parse("NOT false AND NOT false", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertTrue(expression.evaluate());

        expression = Expression.parse("NOT true AND NOT false", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertFalse(expression.evaluate());

        expression = Expression.parse("NOT false AND NOT true", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertFalse(expression.evaluate());

        expression = Expression.parse("NOT true AND NOT true", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertFalse(expression.evaluate());

    }


    @Test
    public void testInequalityOrLogical() {
        SimpleVariableContext variableContext = new SimpleVariableContext();
        variableContext.setNumber("strength", 8);
        variableContext.setNumber("dexterity", 8);
        Expression<Boolean> expression = Expression.parse("strength>=13 OR dexterity >=13", ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        assertFalse(expression.evaluate());

        variableContext.setNumber("strength", 8);
        variableContext.setNumber("dexterity", 13);
        assertTrue(expression.evaluate());

        variableContext.setNumber("strength", 13);
        variableContext.setNumber("dexterity", 8);
        assertTrue(expression.evaluate());
    }

}
