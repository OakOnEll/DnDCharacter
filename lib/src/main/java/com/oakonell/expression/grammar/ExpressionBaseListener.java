// Generated from E:\projects\DnDCharacter\lib\src\main\antlr\Expression.g4 by ANTLR 4.0
package com.oakonell.expression.grammar;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

public class ExpressionBaseListener implements ExpressionListener {
	@Override public void enterExprConditional(ExpressionParser.ExprConditionalContext ctx) { }
	@Override public void exitExprConditional(ExpressionParser.ExprConditionalContext ctx) { }

	@Override public void enterMathUnary(ExpressionParser.MathUnaryContext ctx) { }
	@Override public void exitMathUnary(ExpressionParser.MathUnaryContext ctx) { }

	@Override public void enterBoolean_literal(ExpressionParser.Boolean_literalContext ctx) { }
	@Override public void exitBoolean_literal(ExpressionParser.Boolean_literalContext ctx) { }

	@Override public void enterExprParen(ExpressionParser.ExprParenContext ctx) { }
	@Override public void exitExprParen(ExpressionParser.ExprParenContext ctx) { }

	@Override public void enterRoot(ExpressionParser.RootContext ctx) { }
	@Override public void exitRoot(ExpressionParser.RootContext ctx) { }

	@Override public void enterLogicalCompare(ExpressionParser.LogicalCompareContext ctx) { }
	@Override public void exitLogicalCompare(ExpressionParser.LogicalCompareContext ctx) { }

	@Override public void enterStringConcat(ExpressionParser.StringConcatContext ctx) { }
	@Override public void exitStringConcat(ExpressionParser.StringConcatContext ctx) { }

	@Override public void enterStringLiteral(ExpressionParser.StringLiteralContext ctx) { }
	@Override public void exitStringLiteral(ExpressionParser.StringLiteralContext ctx) { }

	@Override public void enterLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx) { }
	@Override public void exitLogicalLiteral(ExpressionParser.LogicalLiteralContext ctx) { }

	@Override public void enterMathProduct(ExpressionParser.MathProductContext ctx) { }
	@Override public void exitMathProduct(ExpressionParser.MathProductContext ctx) { }

	@Override public void enterFunction(ExpressionParser.FunctionContext ctx) { }
	@Override public void exitFunction(ExpressionParser.FunctionContext ctx) { }

	@Override public void enterExprVariable(ExpressionParser.ExprVariableContext ctx) { }
	@Override public void exitExprVariable(ExpressionParser.ExprVariableContext ctx) { }

	@Override public void enterMathPower(ExpressionParser.MathPowerContext ctx) { }
	@Override public void exitMathPower(ExpressionParser.MathPowerContext ctx) { }

	@Override public void enterLogicalOr(ExpressionParser.LogicalOrContext ctx) { }
	@Override public void exitLogicalOr(ExpressionParser.LogicalOrContext ctx) { }

	@Override public void enterExprFunction(ExpressionParser.ExprFunctionContext ctx) { }
	@Override public void exitExprFunction(ExpressionParser.ExprFunctionContext ctx) { }

	@Override public void enterMathLiteral(ExpressionParser.MathLiteralContext ctx) { }
	@Override public void exitMathLiteral(ExpressionParser.MathLiteralContext ctx) { }

	@Override public void enterVariable(ExpressionParser.VariableContext ctx) { }
	@Override public void exitVariable(ExpressionParser.VariableContext ctx) { }

	@Override public void enterLogicalNot(ExpressionParser.LogicalNotContext ctx) { }
	@Override public void exitLogicalNot(ExpressionParser.LogicalNotContext ctx) { }

	@Override public void enterLogicalAnd(ExpressionParser.LogicalAndContext ctx) { }
	@Override public void exitLogicalAnd(ExpressionParser.LogicalAndContext ctx) { }

	@Override public void enterMathSum(ExpressionParser.MathSumContext ctx) { }
	@Override public void exitMathSum(ExpressionParser.MathSumContext ctx) { }

	@Override public void enterEveryRule(ParserRuleContext ctx) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }
	@Override public void visitTerminal(TerminalNode node) { }
	@Override public void visitErrorNode(ErrorNode node) { }
}