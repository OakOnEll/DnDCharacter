package com.oakonell.expression;

import com.oakonell.expression.functions.ExpressionFunction;
import com.oakonell.expression.grammar.ExpressionBaseVisitor;
import com.oakonell.expression.grammar.ExpressionParser;
import com.oakonell.expression.types.BooleanValue;
import com.oakonell.expression.types.NumberValue;
import com.oakonell.expression.types.StringValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/22/2015.
 */
class ExpressionEvaluator extends ExpressionBaseVisitor<ExpressionValue<?>> {
    private final ExpressionContext context;
    private String formula;

    public ExpressionEvaluator(ExpressionContext context) {
        this.context = context;
    }

    @Override
    public ExpressionValue<?> visitRoot(ExpressionParser.RootContext ctx) {
        formula = ctx.start.getInputStream().toString();
        return visit(ctx.genericExpression());
    }


    // Die functions -------------------------------------------------

    @Override
    public ExpressionValue<?> visitExprSingleDie(ExpressionParser.ExprSingleDieContext ctx) {
        NumberValue lhs = (NumberValue) visit(ctx.genericExpression());
        int dieSides = lhs.getValue();
        return dieExpression(1, dieSides);
    }

    @Override
    public NumberValue visitExprDie(ExpressionParser.ExprDieContext ctx) {
        NumberValue lhs = (NumberValue) visit(ctx.genericExpression(0));
        NumberValue rhs = (NumberValue) visit(ctx.genericExpression(1));
        int numRolls = lhs.getValue();
        int dieSides = rhs.getValue();
        return dieExpression(numRolls, dieSides);
    }

    protected NumberValue dieExpression(int numRolls, int dieSides) {
        int result = 0;
        for (int i = 0; i < numRolls; i++) {
            result += context.evaluateDie(dieSides);
        }
        return new NumberValue(result);
    }

    // Math functions --------------------------------------------------
    @Override
    public NumberValue visitMathSum(ExpressionParser.MathSumContext ctx) {
        NumberValue lhs = (NumberValue) visit(ctx.genericExpression(0));
        NumberValue rhs = (NumberValue) visit(ctx.genericExpression(1));
        if (ctx.K_MINUS() != null) {
            return new NumberValue(lhs.getValue() - rhs.getValue());
        }
        return new NumberValue(lhs.getValue() + rhs.getValue());
    }

    @Override
    public NumberValue visitMathProduct(ExpressionParser.MathProductContext ctx) {
        NumberValue lhs = (NumberValue) visit(ctx.genericExpression(0));
        NumberValue rhs = (NumberValue) visit(ctx.genericExpression(1));
        if (ctx.K_STAR() != null) {
            return new NumberValue(lhs.getValue() * rhs.getValue());
        }
        if (ctx.K_SLASH() != null) {
            return new NumberValue(lhs.getValue() / rhs.getValue());
        }
        return new NumberValue(lhs.getValue() % rhs.getValue());
    }

    @Override
    public NumberValue visitMathLiteral(ExpressionParser.MathLiteralContext ctx) {
        return new NumberValue(Integer.parseInt(ctx.NUM().getText()));
    }

    @Override
    public NumberValue visitMathUnary(ExpressionParser.MathUnaryContext ctx) {
        NumberValue value = (NumberValue) visit(ctx.genericExpression());
        if (ctx.K_MINUS() != null) {
            return new NumberValue(-value.getValue());
        }
        return value;
    }

    @Override
    public NumberValue visitMathPower(ExpressionParser.MathPowerContext ctx) {
        NumberValue lhs = (NumberValue) visit(ctx.genericExpression(0));
        NumberValue rhs = (NumberValue) visit(ctx.genericExpression(1));
        return new NumberValue((int) Math.pow(lhs.getValue(), rhs.getValue()));
    }


    // Boolean functions --------------------------------------------------

    @Override
    public BooleanValue visitLogicalAnd(ExpressionParser.LogicalAndContext ctx) {
        // lazy evaluation, if first is false, whole expression is false
        BooleanValue lhs = (BooleanValue) visit(ctx.genericExpression(0));
        if (!lhs.getValue()) return lhs;

        return (BooleanValue) visit(ctx.genericExpression(1));
    }

    @Override
    public BooleanValue visitLogicalOr(ExpressionParser.LogicalOrContext ctx) {
        // lazy evaluation, if first is true, whole expression is true
        BooleanValue lhs = (BooleanValue) visit(ctx.genericExpression(0));
        if (lhs.getValue()) return lhs;

        return (BooleanValue) visit(ctx.genericExpression(1));
    }

    @Override
    public BooleanValue visitLogicalNot(ExpressionParser.LogicalNotContext ctx) {
        BooleanValue lhs = (BooleanValue) visit(ctx.genericExpression());
        if (lhs.getValue()) {
            return BooleanValue.FALSE;
        }
        return BooleanValue.TRUE;
    }

    @Override
    public BooleanValue visitLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx) {
        if (ctx.boolean_literal().K_FALSE() != null) {
            return BooleanValue.FALSE;
        }
        return BooleanValue.TRUE;
    }


    // String functions --------------------------------------------------
    @Override
    public StringValue visitStringLiteral(ExpressionParser.StringLiteralContext ctx) {
        String quotedLiteral = ctx.STRING_LITERAL().getText();
        quotedLiteral = quotedLiteral.replaceAll("''", "'");
        return new StringValue(quotedLiteral.substring(1, quotedLiteral.length() - 1));
    }


    @Override
    public StringValue visitStringConcat(ExpressionParser.StringConcatContext ctx) {
        StringValue lhs = (StringValue) visit(ctx.genericExpression(0));
        StringValue rhs = (StringValue) visit(ctx.genericExpression(1));
        return new StringValue(lhs.getValue() + rhs.getValue());
    }


    // Generic methods ----------------------------------------------------

    @Override
    public ExpressionValue<?> visitExprParen(ExpressionParser.ExprParenContext ctx) {
        return visit(ctx.genericExpression());
    }

    @Override
    public ExpressionValue<?> visitExprConditional(ExpressionParser.ExprConditionalContext ctx) {
        BooleanValue condition = (BooleanValue) visit(ctx.genericExpression(0));
        ExpressionValue<?> trueVal = visit(ctx.genericExpression(1));
        ExpressionValue<?> falseVal = visit(ctx.genericExpression(2));

        return condition.getValue() ? trueVal : falseVal;
    }

    @Override
    public ExpressionValue<?> visitLogicalCompare(ExpressionParser.LogicalCompareContext ctx) {
        ExpressionValue<?> lhs = visit(ctx.genericExpression(0));
        ExpressionValue<?> rhs = visit(ctx.genericExpression(1));
        if (!lhs.getType().equals(rhs.getType())) {
            throw new RuntimeException("Incompatible types [" + lhs.getType() + ", " + rhs.getType() + "] for comparison " + ctx.getText());
        }


        if (ctx.K_EQUALS() != null) {
            return lhs.getValue().equals(rhs.getValue()) ? BooleanValue.TRUE : BooleanValue.FALSE;
        }
        if (ctx.K_NOT_EQUALS() != null) {
            return !lhs.getValue().equals(rhs.getValue()) ? BooleanValue.TRUE : BooleanValue.FALSE;
        }
        if (lhs.getType().supportsInequality()) {
            Comparable lhsValue = (Comparable) lhs.getValue();
            Comparable rhsValue = (Comparable) rhs.getValue();

            int comparison = lhsValue.compareTo(rhsValue);
            if (ctx.K_GT() != null) {
                return comparison > 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
            }
            if (ctx.K_GTE() != null) {
                return comparison >= 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
            }
            if (ctx.K_LT() != null) {
                return comparison < 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
            }
            if (ctx.K_LTE() != null) {
                return comparison <= 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
            }
        }
        throw new RuntimeException("Unexpected " + lhs.getType() + " comparison operation " + ctx.getText());
    }

    @Override
    public ExpressionValue<?> visitFunction(ExpressionParser.FunctionContext ctx) {
        String functionName = ctx.ID().getText();
        List<ExpressionParser.GenericExpressionContext> argumentContexts = ctx.genericExpression();

        return evaluateFunction(functionName, argumentContexts);
    }

    @Override
    public ExpressionValue<?> visitVariable(ExpressionParser.VariableContext ctx) {
        String variableName = ctx.ID().getText();

        return evaluateVariable(variableName);
    }

    private ExpressionValue<?> evaluateFunction(String functionName, List<ExpressionParser.GenericExpressionContext> argumentContexts) {
        ExpressionFunction function = context.getFunction(functionName);
        if (function == null) {
            throw new RuntimeException("No function named '" + functionName + "' exists");
        }

        List<ExpressionValue<?>> arguments = new ArrayList<>(argumentContexts.size());
        for (ExpressionParser.GenericExpressionContext each : argumentContexts) {
            ExpressionValue<?> argument = visit(each);
            arguments.add(argument);
        }
        return function.evaluate(arguments);
    }

    private ExpressionValue<?> evaluateVariable(String name) {
        ExpressionValue<?> variable = context.getVariableValue(name);
        if (variable == null) {
            throw new RuntimeException("No variable named '" + name + "' exists");
        }
        return variable;
    }


}
