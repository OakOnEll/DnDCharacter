// Generated from E:\projects\DnDCharacter\lib\src\main\antlr\Expression.g4 by ANTLR 4.0
package com.oakonell.expression.grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExpressionParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STRING_LITERAL=1, K_DOUBLE_PIPE=2, K_COLON=3, K_QUESTION_MARK=4, K_COMMA=5, 
		K_L_PAREN=6, K_R_PAREN=7, K_NOT_EQUALS=8, K_EQUALS=9, K_NOT=10, K_OR=11, 
		K_AND=12, K_LTE=13, K_LT=14, K_GTE=15, K_GT=16, K_TRUE=17, K_FALSE=18, 
		K_CARET=19, K_STAR=20, K_SLASH=21, K_MOD=22, K_PLUS=23, K_MINUS=24, ID=25, 
		NUM=26, WS=27;
	public static final String[] tokenNames = {
		"<INVALID>", "STRING_LITERAL", "'||'", "':'", "'?'", "','", "'('", "')'", 
		"'!='", "'='", "'NOT'", "'OR'", "'AND'", "'<='", "'<'", "'>='", "'>'", 
		"'true'", "'false'", "'^'", "'*'", "'/'", "'%'", "'+'", "'-'", "ID", "NUM", 
		"WS"
	};
	public static final int
		RULE_root = 0, RULE_genericExpression = 1, RULE_function = 2, RULE_variable = 3, 
		RULE_boolean_literal = 4;
	public static final String[] ruleNames = {
		"root", "genericExpression", "function", "variable", "boolean_literal"
	};

	@Override
	public String getGrammarFileName() { return "Expression.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public ExpressionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class RootContext extends ParserRuleContext {
		public GenericExpressionContext genericExpression() {
			return getRuleContext(GenericExpressionContext.class,0);
		}
		public RootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_root; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitRoot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitRoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RootContext root() throws RecognitionException {
		RootContext _localctx = new RootContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_root);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10); genericExpression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GenericExpressionContext extends ParserRuleContext {
		public int _p;
		public GenericExpressionContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public GenericExpressionContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_genericExpression; }
	 
		public GenericExpressionContext() { }
		public void copyFrom(GenericExpressionContext ctx) {
			super.copyFrom(ctx);
			this._p = ctx._p;
		}
	}
	public static class ExprConditionalContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_COLON() { return getToken(ExpressionParser.K_COLON, 0); }
		public TerminalNode K_QUESTION_MARK() { return getToken(ExpressionParser.K_QUESTION_MARK, 0); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public ExprConditionalContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterExprConditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitExprConditional(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitExprConditional(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MathUnaryContext extends GenericExpressionContext {
		public GenericExpressionContext genericExpression() {
			return getRuleContext(GenericExpressionContext.class,0);
		}
		public TerminalNode K_MINUS() { return getToken(ExpressionParser.K_MINUS, 0); }
		public TerminalNode K_PLUS() { return getToken(ExpressionParser.K_PLUS, 0); }
		public MathUnaryContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterMathUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitMathUnary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitMathUnary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprParenContext extends GenericExpressionContext {
		public TerminalNode K_R_PAREN() { return getToken(ExpressionParser.K_R_PAREN, 0); }
		public GenericExpressionContext genericExpression() {
			return getRuleContext(GenericExpressionContext.class,0);
		}
		public TerminalNode K_L_PAREN() { return getToken(ExpressionParser.K_L_PAREN, 0); }
		public ExprParenContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterExprParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitExprParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitExprParen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalCompareContext extends GenericExpressionContext {
		public TerminalNode K_GT() { return getToken(ExpressionParser.K_GT, 0); }
		public TerminalNode K_LTE() { return getToken(ExpressionParser.K_LTE, 0); }
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_LT() { return getToken(ExpressionParser.K_LT, 0); }
		public TerminalNode K_EQUALS() { return getToken(ExpressionParser.K_EQUALS, 0); }
		public TerminalNode K_NOT_EQUALS() { return getToken(ExpressionParser.K_NOT_EQUALS, 0); }
		public TerminalNode K_GTE() { return getToken(ExpressionParser.K_GTE, 0); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public LogicalCompareContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterLogicalCompare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitLogicalCompare(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitLogicalCompare(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringConcatContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_DOUBLE_PIPE() { return getToken(ExpressionParser.K_DOUBLE_PIPE, 0); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public StringConcatContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterStringConcat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitStringConcat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitStringConcat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringLiteralContext extends GenericExpressionContext {
		public TerminalNode STRING_LITERAL() { return getToken(ExpressionParser.STRING_LITERAL, 0); }
		public StringLiteralContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalLiteralContext extends GenericExpressionContext {
		public Boolean_literalContext boolean_literal() {
			return getRuleContext(Boolean_literalContext.class,0);
		}
		public LogicalLiteralContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterLogicalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitLogicalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitLogicalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MathProductContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_STAR() { return getToken(ExpressionParser.K_STAR, 0); }
		public TerminalNode K_SLASH() { return getToken(ExpressionParser.K_SLASH, 0); }
		public TerminalNode K_MOD() { return getToken(ExpressionParser.K_MOD, 0); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public MathProductContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterMathProduct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitMathProduct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitMathProduct(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprVariableContext extends GenericExpressionContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public ExprVariableContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterExprVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitExprVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitExprVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MathPowerContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_CARET() { return getToken(ExpressionParser.K_CARET, 0); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public MathPowerContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterMathPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitMathPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitMathPower(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalOrContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_OR() { return getToken(ExpressionParser.K_OR, 0); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public LogicalOrContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterLogicalOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitLogicalOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitLogicalOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprFunctionContext extends GenericExpressionContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ExprFunctionContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterExprFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitExprFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitExprFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MathLiteralContext extends GenericExpressionContext {
		public TerminalNode NUM() { return getToken(ExpressionParser.NUM, 0); }
		public MathLiteralContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterMathLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitMathLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitMathLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalNotContext extends GenericExpressionContext {
		public GenericExpressionContext genericExpression() {
			return getRuleContext(GenericExpressionContext.class,0);
		}
		public TerminalNode K_NOT() { return getToken(ExpressionParser.K_NOT, 0); }
		public LogicalNotContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterLogicalNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitLogicalNot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitLogicalNot(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalAndContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_AND() { return getToken(ExpressionParser.K_AND, 0); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public LogicalAndContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterLogicalAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitLogicalAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitLogicalAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MathSumContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_MINUS() { return getToken(ExpressionParser.K_MINUS, 0); }
		public TerminalNode K_PLUS() { return getToken(ExpressionParser.K_PLUS, 0); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public MathSumContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterMathSum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitMathSum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitMathSum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericExpressionContext genericExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		GenericExpressionContext _localctx = new GenericExpressionContext(_ctx, _parentState, _p);
		GenericExpressionContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, RULE_genericExpression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				_localctx = new MathUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(13);
				_la = _input.LA(1);
				if ( !(_la==K_PLUS || _la==K_MINUS) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(14); genericExpression(11);
				}
				break;

			case 2:
				{
				_localctx = new LogicalNotContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(15); match(K_NOT);
				setState(16); genericExpression(6);
				}
				break;

			case 3:
				{
				_localctx = new ExprParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(17); match(K_L_PAREN);
				setState(18); genericExpression(0);
				setState(19); match(K_R_PAREN);
				}
				break;

			case 4:
				{
				_localctx = new ExprFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(21); function();
				}
				break;

			case 5:
				{
				_localctx = new ExprVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(22); variable();
				}
				break;

			case 6:
				{
				_localctx = new StringLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(23); match(STRING_LITERAL);
				}
				break;

			case 7:
				{
				_localctx = new MathLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(24); match(NUM);
				}
				break;

			case 8:
				{
				_localctx = new LogicalLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(25); boolean_literal();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(57);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(55);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new StringConcatContext(new GenericExpressionContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(28);
						if (!(13 >= _localctx._p)) throw new FailedPredicateException(this, "13 >= $_p");
						setState(29); match(K_DOUBLE_PIPE);
						setState(30); genericExpression(14);
						}
						break;

					case 2:
						{
						_localctx = new MathPowerContext(new GenericExpressionContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(31);
						if (!(10 >= _localctx._p)) throw new FailedPredicateException(this, "10 >= $_p");
						setState(32); match(K_CARET);
						setState(33); genericExpression(11);
						}
						break;

					case 3:
						{
						_localctx = new MathProductContext(new GenericExpressionContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(34);
						if (!(9 >= _localctx._p)) throw new FailedPredicateException(this, "9 >= $_p");
						setState(35);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_STAR) | (1L << K_SLASH) | (1L << K_MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						consume();
						setState(36); genericExpression(10);
						}
						break;

					case 4:
						{
						_localctx = new MathSumContext(new GenericExpressionContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(37);
						if (!(8 >= _localctx._p)) throw new FailedPredicateException(this, "8 >= $_p");
						setState(38);
						_la = _input.LA(1);
						if ( !(_la==K_PLUS || _la==K_MINUS) ) {
						_errHandler.recoverInline(this);
						}
						consume();
						setState(39); genericExpression(9);
						}
						break;

					case 5:
						{
						_localctx = new LogicalAndContext(new GenericExpressionContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(40);
						if (!(5 >= _localctx._p)) throw new FailedPredicateException(this, "5 >= $_p");
						setState(41); match(K_AND);
						setState(42); genericExpression(6);
						}
						break;

					case 6:
						{
						_localctx = new LogicalOrContext(new GenericExpressionContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(43);
						if (!(4 >= _localctx._p)) throw new FailedPredicateException(this, "4 >= $_p");
						setState(44); match(K_OR);
						setState(45); genericExpression(5);
						}
						break;

					case 7:
						{
						_localctx = new LogicalCompareContext(new GenericExpressionContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(46);
						if (!(3 >= _localctx._p)) throw new FailedPredicateException(this, "3 >= $_p");
						setState(47);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_NOT_EQUALS) | (1L << K_EQUALS) | (1L << K_LTE) | (1L << K_LT) | (1L << K_GTE) | (1L << K_GT))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						consume();
						setState(48); genericExpression(4);
						}
						break;

					case 8:
						{
						_localctx = new ExprConditionalContext(new GenericExpressionContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(49);
						if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
						setState(50); match(K_QUESTION_MARK);
						setState(51); genericExpression(0);
						setState(52); match(K_COLON);
						setState(53); genericExpression(2);
						}
						break;
					}
					} 
				}
				setState(59);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode K_R_PAREN() { return getToken(ExpressionParser.K_R_PAREN, 0); }
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public TerminalNode K_L_PAREN() { return getToken(ExpressionParser.K_L_PAREN, 0); }
		public TerminalNode ID() { return getToken(ExpressionParser.ID, 0); }
		public TerminalNode K_COMMA(int i) {
			return getToken(ExpressionParser.K_COMMA, i);
		}
		public List<TerminalNode> K_COMMA() { return getTokens(ExpressionParser.K_COMMA); }
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60); match(ID);
			setState(61); match(K_L_PAREN);
			setState(62); genericExpression(0);
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==K_COMMA) {
				{
				{
				setState(63); match(K_COMMA);
				setState(64); genericExpression(0);
				}
				}
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(70); match(K_R_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(ExpressionParser.ID, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Boolean_literalContext extends ParserRuleContext {
		public TerminalNode K_FALSE() { return getToken(ExpressionParser.K_FALSE, 0); }
		public TerminalNode K_TRUE() { return getToken(ExpressionParser.K_TRUE, 0); }
		public Boolean_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterBoolean_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitBoolean_literal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitBoolean_literal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Boolean_literalContext boolean_literal() throws RecognitionException {
		Boolean_literalContext _localctx = new Boolean_literalContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_boolean_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			_la = _input.LA(1);
			if ( !(_la==K_TRUE || _la==K_FALSE) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1: return genericExpression_sempred((GenericExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean genericExpression_sempred(GenericExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 13 >= _localctx._p;

		case 1: return 10 >= _localctx._p;

		case 2: return 9 >= _localctx._p;

		case 3: return 8 >= _localctx._p;

		case 4: return 5 >= _localctx._p;

		case 5: return 4 >= _localctx._p;

		case 6: return 3 >= _localctx._p;

		case 7: return 1 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\2\3\35O\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\35\n\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\7\3:\n\3\f\3\16\3=\13\3\3\4\3\4\3\4\3\4\3\4\7\4"+
		"D\n\4\f\4\16\4G\13\4\3\4\3\4\3\5\3\5\3\6\3\6\3\6\2\7\2\4\6\b\n\2\7\3\31"+
		"\32\3\26\30\3\31\32\4\n\13\17\22\3\23\24Y\2\f\3\2\2\2\4\34\3\2\2\2\6>"+
		"\3\2\2\2\bJ\3\2\2\2\nL\3\2\2\2\f\r\5\4\3\2\r\3\3\2\2\2\16\17\b\3\1\2\17"+
		"\20\t\2\2\2\20\35\5\4\3\2\21\22\7\f\2\2\22\35\5\4\3\2\23\24\7\b\2\2\24"+
		"\25\5\4\3\2\25\26\7\t\2\2\26\35\3\2\2\2\27\35\5\6\4\2\30\35\5\b\5\2\31"+
		"\35\7\3\2\2\32\35\7\34\2\2\33\35\5\n\6\2\34\16\3\2\2\2\34\21\3\2\2\2\34"+
		"\23\3\2\2\2\34\27\3\2\2\2\34\30\3\2\2\2\34\31\3\2\2\2\34\32\3\2\2\2\34"+
		"\33\3\2\2\2\35;\3\2\2\2\36\37\6\3\2\3\37 \7\4\2\2 :\5\4\3\2!\"\6\3\3\3"+
		"\"#\7\25\2\2#:\5\4\3\2$%\6\3\4\3%&\t\3\2\2&:\5\4\3\2\'(\6\3\5\3()\t\4"+
		"\2\2):\5\4\3\2*+\6\3\6\3+,\7\16\2\2,:\5\4\3\2-.\6\3\7\3./\7\r\2\2/:\5"+
		"\4\3\2\60\61\6\3\b\3\61\62\t\5\2\2\62:\5\4\3\2\63\64\6\3\t\3\64\65\7\6"+
		"\2\2\65\66\5\4\3\2\66\67\7\5\2\2\678\5\4\3\28:\3\2\2\29\36\3\2\2\29!\3"+
		"\2\2\29$\3\2\2\29\'\3\2\2\29*\3\2\2\29-\3\2\2\29\60\3\2\2\29\63\3\2\2"+
		"\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<\5\3\2\2\2=;\3\2\2\2>?\7\33\2\2?@\7\b"+
		"\2\2@E\5\4\3\2AB\7\7\2\2BD\5\4\3\2CA\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2"+
		"\2\2FH\3\2\2\2GE\3\2\2\2HI\7\t\2\2I\7\3\2\2\2JK\7\33\2\2K\t\3\2\2\2LM"+
		"\t\6\2\2M\13\3\2\2\2\6\349;E";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}