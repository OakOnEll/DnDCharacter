// Generated from E:\projects\DnDCharacter\lib\src\main\antlr\Expression.g4 by ANTLR 4.0
package com.oakonell.expression.grammar;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface ExpressionListener extends ParseTreeListener {
	void enterExprConditional(ExpressionParser.ExprConditionalContext ctx);
	void exitExprConditional(ExpressionParser.ExprConditionalContext ctx);

	void enterMathUnary(ExpressionParser.MathUnaryContext ctx);
	void exitMathUnary(ExpressionParser.MathUnaryContext ctx);

	void enterBoolean_literal(ExpressionParser.Boolean_literalContext ctx);
	void exitBoolean_literal(ExpressionParser.Boolean_literalContext ctx);

	void enterExprParen(ExpressionParser.ExprParenContext ctx);
	void exitExprParen(ExpressionParser.ExprParenContext ctx);

	void enterRoot(ExpressionParser.RootContext ctx);
	void exitRoot(ExpressionParser.RootContext ctx);

	void enterLogicalCompare(ExpressionParser.LogicalCompareContext ctx);
	void exitLogicalCompare(ExpressionParser.LogicalCompareContext ctx);

	void enterStringConcat(ExpressionParser.StringConcatContext ctx);
	void exitStringConcat(ExpressionParser.StringConcatContext ctx);

	void enterStringLiteral(ExpressionParser.StringLiteralContext ctx);
	void exitStringLiteral(ExpressionParser.StringLiteralContext ctx);

	void enterLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx);
	void exitLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx);

	void enterMathProduct(ExpressionParser.MathProductContext ctx);
	void exitMathProduct(ExpressionParser.MathProductContext ctx);

	void enterFunction(ExpressionParser.FunctionContext ctx);
	void exitFunction(ExpressionParser.FunctionContext ctx);

	void enterExprVariable(ExpressionParser.ExprVariableContext ctx);
	void exitExprVariable(ExpressionParser.ExprVariableContext ctx);

	void enterMathPower(ExpressionParser.MathPowerContext ctx);
	void exitMathPower(ExpressionParser.MathPowerContext ctx);

	void enterLogicalOr(ExpressionParser.LogicalOrContext ctx);
	void exitLogicalOr(ExpressionParser.LogicalOrContext ctx);

	void enterExprFunction(ExpressionParser.ExprFunctionContext ctx);
	void exitExprFunction(ExpressionParser.ExprFunctionContext ctx);

	void enterMathLiteral(ExpressionParser.MathLiteralContext ctx);
	void exitMathLiteral(ExpressionParser.MathLiteralContext ctx);

	void enterVariable(ExpressionParser.VariableContext ctx);
	void exitVariable(ExpressionParser.VariableContext ctx);

	void enterLogicalNot(ExpressionParser.LogicalNotContext ctx);
	void exitLogicalNot(ExpressionParser.LogicalNotContext ctx);

	void enterLogicalAnd(ExpressionParser.LogicalAndContext ctx);
	void exitLogicalAnd(ExpressionParser.LogicalAndContext ctx);

	void enterMathSum(ExpressionParser.MathSumContext ctx);
	void exitMathSum(ExpressionParser.MathSumContext ctx);
}