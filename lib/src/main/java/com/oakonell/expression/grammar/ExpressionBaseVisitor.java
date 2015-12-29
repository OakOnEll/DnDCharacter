// Generated from E:\projects\DnDCharacter\lib\src\main\antlr\Expression.g4 by ANTLR 4.0
package com.oakonell.expression.grammar;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements ExpressionVisitor<T> {
	@Override public T visitExprConditional(ExpressionParser.ExprConditionalContext ctx) { return visitChildren(ctx); }

	@Override public T visitMathUnary(ExpressionParser.MathUnaryContext ctx) { return visitChildren(ctx); }

	@Override public T visitBoolean_literal(ExpressionParser.Boolean_literalContext ctx) { return visitChildren(ctx); }

	@Override public T visitExprParen(ExpressionParser.ExprParenContext ctx) { return visitChildren(ctx); }

	@Override public T visitRoot(ExpressionParser.RootContext ctx) { return visitChildren(ctx); }

	@Override public T visitLogicalCompare(ExpressionParser.LogicalCompareContext ctx) { return visitChildren(ctx); }

	@Override public T visitStringConcat(ExpressionParser.StringConcatContext ctx) { return visitChildren(ctx); }

	@Override public T visitStringLiteral(ExpressionParser.StringLiteralContext ctx) { return visitChildren(ctx); }

	@Override public T visitLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx) { return visitChildren(ctx); }

	@Override public T visitMathProduct(ExpressionParser.MathProductContext ctx) { return visitChildren(ctx); }

	@Override public T visitFunction(ExpressionParser.FunctionContext ctx) { return visitChildren(ctx); }

	@Override public T visitExprVariable(ExpressionParser.ExprVariableContext ctx) { return visitChildren(ctx); }

	@Override public T visitExprSingleDie(ExpressionParser.ExprSingleDieContext ctx) { return visitChildren(ctx); }

	@Override public T visitMathPower(ExpressionParser.MathPowerContext ctx) { return visitChildren(ctx); }

	@Override public T visitExprDie(ExpressionParser.ExprDieContext ctx) { return visitChildren(ctx); }

	@Override public T visitLogicalOr(ExpressionParser.LogicalOrContext ctx) { return visitChildren(ctx); }

	@Override public T visitExprFunction(ExpressionParser.ExprFunctionContext ctx) { return visitChildren(ctx); }

	@Override public T visitMathLiteral(ExpressionParser.MathLiteralContext ctx) { return visitChildren(ctx); }

	@Override public T visitVariable(ExpressionParser.VariableContext ctx) { return visitChildren(ctx); }

	@Override public T visitLogicalNot(ExpressionParser.LogicalNotContext ctx) { return visitChildren(ctx); }

	@Override public T visitLogicalAnd(ExpressionParser.LogicalAndContext ctx) { return visitChildren(ctx); }

	@Override public T visitMathSum(ExpressionParser.MathSumContext ctx) { return visitChildren(ctx); }
}