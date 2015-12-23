package com.oakonell.expression;

import com.oakonell.expression.Expression;
import com.oakonell.expression.ExpressionContext;
import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.context.SimpleFunctionContext;
import com.oakonell.expression.context.SimpleVariableContext;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Rob on 12/22/2015.
 */
public class ExpressionTest {
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
}
