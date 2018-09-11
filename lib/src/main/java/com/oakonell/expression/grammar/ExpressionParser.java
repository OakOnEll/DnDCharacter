// Generated from C:\Users\Rob\Documents\projects\DnDCharacter\lib\src\main\antlr\Expression.g4 by ANTLR 4.5.3
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
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STRING_LITERAL=1, K_D=2, K_DOUBLE_PIPE=3, K_COLON=4, K_QUESTION_MARK=5, 
		K_COMMA=6, K_L_PAREN=7, K_R_PAREN=8, K_NOT_EQUALS=9, K_EQUALS=10, K_NOT=11, 
		K_OR=12, K_AND=13, K_LTE=14, K_LT=15, K_GTE=16, K_GT=17, K_TRUE=18, K_FALSE=19, 
		K_CARET=20, K_STAR=21, K_SLASH=22, K_MOD=23, K_PLUS=24, K_MINUS=25, ID=26, 
		NUM=27, WS=28;
	public static final int
		RULE_root = 0, RULE_genericExpression = 1, RULE_function = 2, RULE_variable = 3, 
		RULE_boolean_literal = 4;
	public static final String[] ruleNames = {
		"root", "genericExpression", "function", "variable", "boolean_literal"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'d'", "'||'", "':'", "'?'", "','", "'('", "')'", "'!='", 
		"'='", "'NOT'", "'OR'", "'AND'", "'<='", "'<'", "'>='", "'>'", "'true'", 
		"'false'", "'^'", "'*'", "'/'", "'%'", "'+'", "'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "STRING_LITERAL", "K_D", "K_DOUBLE_PIPE", "K_COLON", "K_QUESTION_MARK", 
		"K_COMMA", "K_L_PAREN", "K_R_PAREN", "K_NOT_EQUALS", "K_EQUALS", "K_NOT", 
		"K_OR", "K_AND", "K_LTE", "K_LT", "K_GTE", "K_GT", "K_TRUE", "K_FALSE", 
		"K_CARET", "K_STAR", "K_SLASH", "K_MOD", "K_PLUS", "K_MINUS", "ID", "NUM", 
		"WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Expression.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

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
		public TerminalNode EOF() { return getToken(ExpressionParser.EOF, 0); }
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
			setState(10);
			genericExpression(0);
			setState(11);
			match(EOF);
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
		public GenericExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericExpression; }
	 
		public GenericExpressionContext() { }
		public void copyFrom(GenericExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LogicalNotContext extends GenericExpressionContext {
		public TerminalNode K_NOT() { return getToken(ExpressionParser.K_NOT, 0); }
		public GenericExpressionContext genericExpression() {
			return getRuleContext(GenericExpressionContext.class,0);
		}
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
	public static class MathSumContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_PLUS() { return getToken(ExpressionParser.K_PLUS, 0); }
		public TerminalNode K_MINUS() { return getToken(ExpressionParser.K_MINUS, 0); }
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
	public static class MathPowerContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_CARET() { return getToken(ExpressionParser.K_CARET, 0); }
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
	public static class LogicalCompareContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_EQUALS() { return getToken(ExpressionParser.K_EQUALS, 0); }
		public TerminalNode K_NOT_EQUALS() { return getToken(ExpressionParser.K_NOT_EQUALS, 0); }
		public TerminalNode K_GT() { return getToken(ExpressionParser.K_GT, 0); }
		public TerminalNode K_GTE() { return getToken(ExpressionParser.K_GTE, 0); }
		public TerminalNode K_LT() { return getToken(ExpressionParser.K_LT, 0); }
		public TerminalNode K_LTE() { return getToken(ExpressionParser.K_LTE, 0); }
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
	public static class LogicalAndContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_AND() { return getToken(ExpressionParser.K_AND, 0); }
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
	public static class ExprParenContext extends GenericExpressionContext {
		public TerminalNode K_L_PAREN() { return getToken(ExpressionParser.K_L_PAREN, 0); }
		public GenericExpressionContext genericExpression() {
			return getRuleContext(GenericExpressionContext.class,0);
		}
		public TerminalNode K_R_PAREN() { return getToken(ExpressionParser.K_R_PAREN, 0); }
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
	public static class ExprDieContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_D() { return getToken(ExpressionParser.K_D, 0); }
		public ExprDieContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterExprDie(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitExprDie(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitExprDie(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MathProductContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_STAR() { return getToken(ExpressionParser.K_STAR, 0); }
		public TerminalNode K_SLASH() { return getToken(ExpressionParser.K_SLASH, 0); }
		public TerminalNode K_MOD() { return getToken(ExpressionParser.K_MOD, 0); }
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
	public static class ExprConditionalContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_QUESTION_MARK() { return getToken(ExpressionParser.K_QUESTION_MARK, 0); }
		public TerminalNode K_COLON() { return getToken(ExpressionParser.K_COLON, 0); }
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
	public static class ExprSingleDieContext extends GenericExpressionContext {
		public TerminalNode K_D() { return getToken(ExpressionParser.K_D, 0); }
		public GenericExpressionContext genericExpression() {
			return getRuleContext(GenericExpressionContext.class,0);
		}
		public ExprSingleDieContext(GenericExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).enterExprSingleDie(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionListener ) ((ExpressionListener)listener).exitExprSingleDie(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionVisitor ) return ((ExpressionVisitor<? extends T>)visitor).visitExprSingleDie(this);
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
	public static class LogicalOrContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_OR() { return getToken(ExpressionParser.K_OR, 0); }
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
	public static class StringConcatContext extends GenericExpressionContext {
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_DOUBLE_PIPE() { return getToken(ExpressionParser.K_DOUBLE_PIPE, 0); }
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

	public final GenericExpressionContext genericExpression() throws RecognitionException {
		return genericExpression(0);
	}

	private GenericExpressionContext genericExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		GenericExpressionContext _localctx = new GenericExpressionContext(_ctx, _parentState);
		GenericExpressionContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_genericExpression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				_localctx = new ExprParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(14);
				match(K_L_PAREN);
				setState(15);
				genericExpression(0);
				setState(16);
				match(K_R_PAREN);
				}
				break;
			case 2:
				{
				_localctx = new ExprFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(18);
				function();
				}
				break;
			case 3:
				{
				_localctx = new ExprVariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(19);
				variable();
				}
				break;
			case 4:
				{
				_localctx = new ExprSingleDieContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(20);
				match(K_D);
				setState(21);
				genericExpression(15);
				}
				break;
			case 5:
				{
				_localctx = new StringLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(22);
				match(STRING_LITERAL);
				}
				break;
			case 6:
				{
				_localctx = new MathUnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(23);
				_la = _input.LA(1);
				if ( !(_la==K_PLUS || _la==K_MINUS) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(24);
				genericExpression(11);
				}
				break;
			case 7:
				{
				_localctx = new MathLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(25);
				match(NUM);
				}
				break;
			case 8:
				{
				_localctx = new LogicalNotContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(26);
				match(K_NOT);
				setState(27);
				genericExpression(6);
				}
				break;
			case 9:
				{
				_localctx = new LogicalLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(28);
				boolean_literal();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(63);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(61);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new ExprDieContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(31);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(32);
						match(K_D);
						setState(33);
						genericExpression(15);
						}
						break;
					case 2:
						{
						_localctx = new StringConcatContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(34);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(35);
						match(K_DOUBLE_PIPE);
						setState(36);
						genericExpression(14);
						}
						break;
					case 3:
						{
						_localctx = new MathPowerContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(37);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(38);
						match(K_CARET);
						setState(39);
						genericExpression(11);
						}
						break;
					case 4:
						{
						_localctx = new MathProductContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(40);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(41);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_STAR) | (1L << K_SLASH) | (1L << K_MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(42);
						genericExpression(10);
						}
						break;
					case 5:
						{
						_localctx = new MathSumContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(43);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(44);
						_la = _input.LA(1);
						if ( !(_la==K_PLUS || _la==K_MINUS) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(45);
						genericExpression(9);
						}
						break;
					case 6:
						{
						_localctx = new LogicalCompareContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(46);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(47);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_NOT_EQUALS) | (1L << K_EQUALS) | (1L << K_LTE) | (1L << K_LT) | (1L << K_GTE) | (1L << K_GT))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(48);
						genericExpression(6);
						}
						break;
					case 7:
						{
						_localctx = new LogicalAndContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(49);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(50);
						match(K_AND);
						setState(51);
						genericExpression(5);
						}
						break;
					case 8:
						{
						_localctx = new LogicalOrContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(52);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(53);
						match(K_OR);
						setState(54);
						genericExpression(4);
						}
						break;
					case 9:
						{
						_localctx = new ExprConditionalContext(new GenericExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_genericExpression);
						setState(55);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(56);
						match(K_QUESTION_MARK);
						setState(57);
						genericExpression(0);
						setState(58);
						match(K_COLON);
						setState(59);
						genericExpression(2);
						}
						break;
					}
					} 
				}
				setState(65);
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
		public TerminalNode ID() { return getToken(ExpressionParser.ID, 0); }
		public TerminalNode K_L_PAREN() { return getToken(ExpressionParser.K_L_PAREN, 0); }
		public List<GenericExpressionContext> genericExpression() {
			return getRuleContexts(GenericExpressionContext.class);
		}
		public GenericExpressionContext genericExpression(int i) {
			return getRuleContext(GenericExpressionContext.class,i);
		}
		public TerminalNode K_R_PAREN() { return getToken(ExpressionParser.K_R_PAREN, 0); }
		public List<TerminalNode> K_COMMA() { return getTokens(ExpressionParser.K_COMMA); }
		public TerminalNode K_COMMA(int i) {
			return getToken(ExpressionParser.K_COMMA, i);
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
			setState(66);
			match(ID);
			setState(67);
			match(K_L_PAREN);
			setState(68);
			genericExpression(0);
			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==K_COMMA) {
				{
				{
				setState(69);
				match(K_COMMA);
				setState(70);
				genericExpression(0);
				}
				}
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(76);
			match(K_R_PAREN);
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
			setState(78);
			match(ID);
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
		public TerminalNode K_TRUE() { return getToken(ExpressionParser.K_TRUE, 0); }
		public TerminalNode K_FALSE() { return getToken(ExpressionParser.K_FALSE, 0); }
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
			setState(80);
			_la = _input.LA(1);
			if ( !(_la==K_TRUE || _la==K_FALSE) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
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
		case 1:
			return genericExpression_sempred((GenericExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean genericExpression_sempred(GenericExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 14);
		case 1:
			return precpred(_ctx, 13);
		case 2:
			return precpred(_ctx, 10);
		case 3:
			return precpred(_ctx, 9);
		case 4:
			return precpred(_ctx, 8);
		case 5:
			return precpred(_ctx, 5);
		case 6:
			return precpred(_ctx, 4);
		case 7:
			return precpred(_ctx, 3);
		case 8:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\36U\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3 \n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\7\3@\n\3\f\3\16\3C\13\3\3\4\3\4\3\4\3\4\3\4\7\4"+
		"J\n\4\f\4\16\4M\13\4\3\4\3\4\3\5\3\5\3\6\3\6\3\6\2\3\4\7\2\4\6\b\n\2\6"+
		"\3\2\32\33\3\2\27\31\4\2\13\f\20\23\3\2\24\25a\2\f\3\2\2\2\4\37\3\2\2"+
		"\2\6D\3\2\2\2\bP\3\2\2\2\nR\3\2\2\2\f\r\5\4\3\2\r\16\7\2\2\3\16\3\3\2"+
		"\2\2\17\20\b\3\1\2\20\21\7\t\2\2\21\22\5\4\3\2\22\23\7\n\2\2\23 \3\2\2"+
		"\2\24 \5\6\4\2\25 \5\b\5\2\26\27\7\4\2\2\27 \5\4\3\21\30 \7\3\2\2\31\32"+
		"\t\2\2\2\32 \5\4\3\r\33 \7\35\2\2\34\35\7\r\2\2\35 \5\4\3\b\36 \5\n\6"+
		"\2\37\17\3\2\2\2\37\24\3\2\2\2\37\25\3\2\2\2\37\26\3\2\2\2\37\30\3\2\2"+
		"\2\37\31\3\2\2\2\37\33\3\2\2\2\37\34\3\2\2\2\37\36\3\2\2\2 A\3\2\2\2!"+
		"\"\f\20\2\2\"#\7\4\2\2#@\5\4\3\21$%\f\17\2\2%&\7\5\2\2&@\5\4\3\20\'(\f"+
		"\f\2\2()\7\26\2\2)@\5\4\3\r*+\f\13\2\2+,\t\3\2\2,@\5\4\3\f-.\f\n\2\2."+
		"/\t\2\2\2/@\5\4\3\13\60\61\f\7\2\2\61\62\t\4\2\2\62@\5\4\3\b\63\64\f\6"+
		"\2\2\64\65\7\17\2\2\65@\5\4\3\7\66\67\f\5\2\2\678\7\16\2\28@\5\4\3\69"+
		":\f\3\2\2:;\7\7\2\2;<\5\4\3\2<=\7\6\2\2=>\5\4\3\4>@\3\2\2\2?!\3\2\2\2"+
		"?$\3\2\2\2?\'\3\2\2\2?*\3\2\2\2?-\3\2\2\2?\60\3\2\2\2?\63\3\2\2\2?\66"+
		"\3\2\2\2?9\3\2\2\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2\2B\5\3\2\2\2CA\3\2\2\2"+
		"DE\7\34\2\2EF\7\t\2\2FK\5\4\3\2GH\7\b\2\2HJ\5\4\3\2IG\3\2\2\2JM\3\2\2"+
		"\2KI\3\2\2\2KL\3\2\2\2LN\3\2\2\2MK\3\2\2\2NO\7\n\2\2O\7\3\2\2\2PQ\7\34"+
		"\2\2Q\t\3\2\2\2RS\t\5\2\2S\13\3\2\2\2\6\37?AK";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}