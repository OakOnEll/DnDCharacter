// Generated from E:\projects\DnDCharacter\lib\src\main\antlr\Expression.g4 by ANTLR 4.0
package com.oakonell.expression.grammar;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface ExpressionVisitor<T> extends ParseTreeVisitor<T> {
	T visitExprConditional(ExpressionParser.ExprConditionalContext ctx);

	T visitMathUnary(ExpressionParser.MathUnaryContext ctx);

	T visitBoolean_literal(ExpressionParser.Boolean_literalContext ctx);

	T visitExprParen(ExpressionParser.ExprParenContext ctx);

	T visitRoot(ExpressionParser.RootContext ctx);

	T visitLogicalCompare(ExpressionParser.LogicalCompareContext ctx);

	T visitStringConcat(ExpressionParser.StringConcatContext ctx);

	T visitStringLiteral(ExpressionParser.StringLiteralContext ctx);

	T visitLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx);

	T visitMathProduct(ExpressionParser.MathProductContext ctx);

	T visitFunction(ExpressionParser.FunctionContext ctx);

	T visitExprVariable(ExpressionParser.ExprVariableContext ctx);

	T visitMathPower(ExpressionParser.MathPowerContext ctx);

	T visitLogicalOr(ExpressionParser.LogicalOrContext ctx);

	T visitExprFunction(ExpressionParser.ExprFunctionContext ctx);

	T visitMathLiteral(ExpressionParser.MathLiteralContext ctx);

	T visitVariable(ExpressionParser.VariableContext ctx);

	T visitLogicalNot(ExpressionParser.LogicalNotContext ctx);

	T visitLogicalAnd(ExpressionParser.LogicalAndContext ctx);

	T visitMathSum(ExpressionParser.MathSumContext ctx);
}