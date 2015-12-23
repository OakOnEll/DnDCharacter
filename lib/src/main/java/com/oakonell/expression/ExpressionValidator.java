package com.oakonell.expression;

import com.oakonell.expression.grammar.ExpressionBaseVisitor;
import com.oakonell.expression.grammar.ExpressionParser;
import com.oakonell.expression.functions.ExpressionFunction;
import com.oakonell.expression.types.BooleanType;
import com.oakonell.expression.types.NumberType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/22/2015.
 */
class ExpressionValidator extends ExpressionBaseVisitor<ExpressionType<?>> {
    private final ExpressionContext context;
    private String formula;


    public ExpressionValidator(ExpressionContext context) {
        this.context = context;
    }


    @Override
    public ExpressionType<?> visitRoot(ExpressionParser.RootContext ctx) {
        formula = ctx.start.getInputStream().toString();
        return super.visitRoot(ctx);
    }

    // Math functions --------------------------------------------------
    @Override
    public ExpressionType<?> visitMathSum(ExpressionParser.MathSumContext ctx) {
        return validateMathTerms(ctx.genericExpression(0), ctx.genericExpression(1), "sum");
    }

    @Override
    public ExpressionType<?> visitMathProduct(ExpressionParser.MathProductContext ctx) {
        return validateMathTerms(ctx.genericExpression(0), ctx.genericExpression(1), "product");
    }

    @Override
    public NumberType visitMathLiteral(ExpressionParser.MathLiteralContext ctx) {
        return ExpressionType.NUMBER_TYPE;
    }

    @Override
    public ExpressionType<?> visitMathUnary(ExpressionParser.MathUnaryContext ctx) {
        ExpressionType<?> lhs = visit(ctx.genericExpression());
        if (!lhs.isNumber()) {
            reportError(ctx.genericExpression(), lhs, "Unary Expression should be a number");
        }
        return lhs;
    }

    @Override
    public ExpressionType<?> visitMathPower(ExpressionParser.MathPowerContext ctx) {
        return validateMathTerms(ctx.genericExpression(0), ctx.genericExpression(1), "power");
    }


    private ExpressionType<?> validateMathTerms(ExpressionParser.GenericExpressionContext lExpr, ExpressionParser.GenericExpressionContext rExpr, String text) {
        ExpressionType<?> lhs = visit(lExpr);
        ExpressionType<?> rhs = visit(rExpr);
        if (!lhs.isNumber()) {
            reportError(lExpr, lhs, "Left Expression in a " + text + " should be a number");
        }
        if (!rhs.isNumber()) {
            reportError(rExpr, rhs, "Right Expression in a " + text + " should be a number");
        }

        return lhs;
    }

    private void reportError(ExpressionParser.GenericExpressionContext lhs, ExpressionType<?> expressionType, String s) {
        // TODO

//        lhs.getStart().
        int index = lhs.getStart().getStartIndex();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < index; i++) {
            builder.append(" ");
        }
        throw new RuntimeException("Error in formula: " + lhs.getText() + "- " + s + "\n" + formula + "\n" + builder.toString() + "^");
    }

    // Boolean functions --------------------------------------------------

    private ExpressionType<?> validateLogicalTerms(ExpressionParser.GenericExpressionContext lExpr, ExpressionParser.GenericExpressionContext rExpr, String text) {
        ExpressionType<?> lhs = visit(lExpr);
        ExpressionType<?> rhs = visit(rExpr);
        if (!lhs.isBoolean()) {
            reportError(lExpr, lhs, "Left Expression in a " + text + " should be a boolean");
        }
        if (!rhs.isBoolean()) {
            reportError(rExpr, rhs, "Right Expression in a " + text + " should be a boolean");
        }

        return lhs;
    }

    @Override
    public ExpressionType<?> visitLogicalAnd(ExpressionParser.LogicalAndContext ctx) {
        return validateLogicalTerms(ctx.genericExpression(0), ctx.genericExpression(1), "and");
    }

    @Override
    public ExpressionType<?> visitLogicalOr(ExpressionParser.LogicalOrContext ctx) {
        return validateLogicalTerms(ctx.genericExpression(0), ctx.genericExpression(1), "or");
    }

    @Override
    public ExpressionType<?> visitLogicalNot(ExpressionParser.LogicalNotContext ctx) {
        ExpressionType<?> lhs = visit(ctx.genericExpression());
        if (!lhs.isNumber()) {
            reportError(ctx.genericExpression(), lhs, "Logical Not expression should be a boolean");
        }
        return lhs;
    }

    @Override
    public ExpressionType<?> visitLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx) {
        return ExpressionType.BOOLEAN_TYPE;
    }


    // String functions --------------------------------------------------

    private ExpressionType<?> validateStringTerms(ExpressionParser.GenericExpressionContext lExpr, ExpressionParser.GenericExpressionContext rExpr, String text) {
        ExpressionType<?> lhs = visit(lExpr);
        ExpressionType<?> rhs = visit(rExpr);
        if (!lhs.isString()) {
            reportError(lExpr, lhs, "Left Expression in a " + text + " should be a string");
        }
        if (!rhs.isString()) {
            reportError(rExpr, rhs, "Right Expression in a " + text + " should be a string");
        }

        return lhs;
    }

    @Override
    public ExpressionType<?> visitStringLiteral(ExpressionParser.StringLiteralContext ctx) {
        return ExpressionType.STRING_TYPE;
    }


    @Override
    public ExpressionType<?> visitStringConcat(ExpressionParser.StringConcatContext ctx) {
        return validateStringTerms(ctx.genericExpression(0), ctx.genericExpression(1), "concatenation");
    }


    // Generic methods ----------------------------------------------------

    @Override
    public ExpressionType<?> visitExprParen(ExpressionParser.ExprParenContext ctx) {
        return (ExpressionType<?>) visit(ctx.genericExpression());
    }

    @Override
    public ExpressionType<?> visitExprConditional(ExpressionParser.ExprConditionalContext ctx) {
        ExpressionType<?> conditionType = visit(ctx.genericExpression(0));
        ExpressionType<?> trueType = visit(ctx.genericExpression(1));
        ExpressionType<?> falseType = visit(ctx.genericExpression(2));

        if (!conditionType.isBoolean()) {
            reportError(ctx.genericExpression(0), conditionType, "Conditional Expression should be a boolean");
        }
        if (!trueType.equals(falseType)) {
            reportError(ctx.genericExpression(2), falseType, "False Conditional Expression should be the same types as the true conditional expression, " + trueType);
        }
        return trueType;
    }

    @Override
    public BooleanType visitLogicalCompare(ExpressionParser.LogicalCompareContext ctx) {
        ExpressionType<?> lhs = visit(ctx.genericExpression(0));
        ExpressionType<?> rhs = visit(ctx.genericExpression(1));

        if (!lhs.equals(rhs)) {
            reportError(ctx.genericExpression(1), rhs, "Right Comparison Expression should be the same types as the left comparison Expression, " + lhs);
        }

        if (ctx.K_EQUALS() != null || ctx.K_NOT_EQUALS() != null) {
            return ExpressionType.BOOLEAN_TYPE;
        }
        if (!lhs.supportsInequality()) {
            reportError(ctx.genericExpression(1), rhs, "Inequality comparison method not supported by type " + lhs);
        }
        return ExpressionType.BOOLEAN_TYPE;

    }

    @Override
    public ExpressionType<?> visitFunction(ExpressionParser.FunctionContext ctx) {
        String functionName = ctx.ID().getText();
        List<ExpressionParser.GenericExpressionContext> argumentContexts = ctx.genericExpression();

        return evaluateFunction(functionName, argumentContexts);
    }

    @Override
    public ExpressionType<?> visitVariable(ExpressionParser.VariableContext ctx) {
        String variableName = ctx.ID().getText();

        return evaluateVariable(variableName);
    }

    private ExpressionType<?> evaluateFunction(String functionName, List<ExpressionParser.GenericExpressionContext> argumentContexts) {
        ExpressionFunction function = context.getFunction(functionName);
        if (function == null) {
            throw new RuntimeException("No function named '" + functionName + "' exists");
        }

        List<ExpressionType<?>> arguments = new ArrayList<>(argumentContexts.size());
        for (ExpressionParser.GenericExpressionContext each : argumentContexts) {
            ExpressionType<?> argument = visit(each);
            arguments.add(argument);
        }
        return function.validate(arguments);
    }

    private ExpressionType<?> evaluateVariable(String name) {
        ExpressionType<?> variable = context.getVariableType(name);
        if (variable == null) {
            throw new RuntimeException("No variable named '" + name + "' exists");
        }
        return variable;
    }


}
