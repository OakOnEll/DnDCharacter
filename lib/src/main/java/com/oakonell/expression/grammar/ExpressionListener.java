// Generated from C:\Users\Rob\Documents\projects\DnDCharacter\lib\src\main\antlr\Expression.g4 by ANTLR 4.5.3
package com.oakonell.expression.grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpressionParser}.
 */
public interface ExpressionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#root}.
	 * @param ctx the parse tree
	 */
	void enterRoot(ExpressionParser.RootContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#root}.
	 * @param ctx the parse tree
	 */
	void exitRoot(ExpressionParser.RootContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalNot}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalNot(ExpressionParser.LogicalNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalNot}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalNot(ExpressionParser.LogicalNotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprFunction}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterExprFunction(ExpressionParser.ExprFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprFunction}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitExprFunction(ExpressionParser.ExprFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mathSum}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterMathSum(ExpressionParser.MathSumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mathSum}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitMathSum(ExpressionParser.MathSumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mathUnary}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterMathUnary(ExpressionParser.MathUnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mathUnary}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitMathUnary(ExpressionParser.MathUnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mathPower}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterMathPower(ExpressionParser.MathPowerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mathPower}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitMathPower(ExpressionParser.MathPowerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalCompare}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalCompare(ExpressionParser.LogicalCompareContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalCompare}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalCompare(ExpressionParser.LogicalCompareContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalAnd}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAnd(ExpressionParser.LogicalAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalAnd}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAnd(ExpressionParser.LogicalAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mathLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterMathLiteral(ExpressionParser.MathLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mathLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitMathLiteral(ExpressionParser.MathLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterExprParen(ExpressionParser.ExprParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitExprParen(ExpressionParser.ExprParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprDie}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterExprDie(ExpressionParser.ExprDieContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprDie}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitExprDie(ExpressionParser.ExprDieContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mathProduct}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterMathProduct(ExpressionParser.MathProductContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mathProduct}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitMathProduct(ExpressionParser.MathProductContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprConditional}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterExprConditional(ExpressionParser.ExprConditionalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprConditional}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitExprConditional(ExpressionParser.ExprConditionalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprVariable}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterExprVariable(ExpressionParser.ExprVariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprVariable}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitExprVariable(ExpressionParser.ExprVariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprSingleDie}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterExprSingleDie(ExpressionParser.ExprSingleDieContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprSingleDie}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitExprSingleDie(ExpressionParser.ExprSingleDieContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(ExpressionParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(ExpressionParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code logicalOr}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOr(ExpressionParser.LogicalOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code logicalOr}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOr(ExpressionParser.LogicalOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringConcat}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void enterStringConcat(ExpressionParser.StringConcatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringConcat}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 */
	void exitStringConcat(ExpressionParser.StringConcatContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(ExpressionParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(ExpressionParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(ExpressionParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(ExpressionParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#boolean_literal}.
	 * @param ctx the parse tree
	 */
	void enterBoolean_literal(ExpressionParser.Boolean_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#boolean_literal}.
	 * @param ctx the parse tree
	 */
	void exitBoolean_literal(ExpressionParser.Boolean_literalContext ctx);
}