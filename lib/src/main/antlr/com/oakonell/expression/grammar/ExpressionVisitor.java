// Generated from E:/projects/DnDCharacter/lib/src/main/antlr\Expression.g4 by ANTLR 4.5.1
package com.oakonell.expression.grammar;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExpressionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExpressionVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#root}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoot(ExpressionParser.RootContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprConditional}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprConditional(ExpressionParser.ExprConditionalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathUnary}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathUnary(ExpressionParser.MathUnaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprParen}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprParen(ExpressionParser.ExprParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalCompare}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalCompare(ExpressionParser.LogicalCompareContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringConcat}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringConcat(ExpressionParser.StringConcatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(ExpressionParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathProduct}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathProduct(ExpressionParser.MathProductContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprVariable}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprVariable(ExpressionParser.ExprVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprSingleDie}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprSingleDie(ExpressionParser.ExprSingleDieContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathPower}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathPower(ExpressionParser.MathPowerContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprDie}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprDie(ExpressionParser.ExprDieContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalOr}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOr(ExpressionParser.LogicalOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprFunction}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprFunction(ExpressionParser.ExprFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathLiteral}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathLiteral(ExpressionParser.MathLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalNot}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalNot(ExpressionParser.LogicalNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code logicalAnd}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAnd(ExpressionParser.LogicalAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mathSum}
	 * labeled alternative in {@link ExpressionParser#genericExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathSum(ExpressionParser.MathSumContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(ExpressionParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(ExpressionParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionParser#boolean_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_literal(ExpressionParser.Boolean_literalContext ctx);
}