// Generated from E:\projects\DnDCharacter\lib\src\main\antlr\Expression.g4 by ANTLR 4.0
package com.oakonell.expression.grammar;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExpressionLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STRING_LITERAL=1, K_DOUBLE_PIPE=2, K_COLON=3, K_QUESTION_MARK=4, K_COMMA=5, 
		K_L_PAREN=6, K_R_PAREN=7, K_NOT_EQUALS=8, K_EQUALS=9, K_NOT=10, K_OR=11, 
		K_AND=12, K_LTE=13, K_LT=14, K_GTE=15, K_GT=16, K_TRUE=17, K_FALSE=18, 
		K_CARET=19, K_STAR=20, K_SLASH=21, K_MOD=22, K_PLUS=23, K_MINUS=24, ID=25, 
		NUM=26, WS=27;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"STRING_LITERAL", "'||'", "':'", "'?'", "','", "'('", "')'", "'!='", "'='", 
		"'NOT'", "'OR'", "'AND'", "'<='", "'<'", "'>='", "'>'", "'true'", "'false'", 
		"'^'", "'*'", "'/'", "'%'", "'+'", "'-'", "ID", "NUM", "WS"
	};
	public static final String[] ruleNames = {
		"STRING_LITERAL", "K_DOUBLE_PIPE", "K_COLON", "K_QUESTION_MARK", "K_COMMA", 
		"K_L_PAREN", "K_R_PAREN", "K_NOT_EQUALS", "K_EQUALS", "K_NOT", "K_OR", 
		"K_AND", "K_LTE", "K_LT", "K_GTE", "K_GT", "K_TRUE", "K_FALSE", "K_CARET", 
		"K_STAR", "K_SLASH", "K_MOD", "K_PLUS", "K_MINUS", "ID", "NUM", "WS"
	};


	public ExpressionLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Expression.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 26: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\2\4\35\u0093\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27"+
		"\t\27\4\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\3"+
		"\2\7\2>\n\2\f\2\16\2A\13\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6"+
		"\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3"+
		"\r\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22"+
		"\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25"+
		"\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\6\32\u0084\n\32\r\32\16"+
		"\32\u0085\3\33\6\33\u0089\n\33\r\33\16\33\u008a\3\34\6\34\u008e\n\34\r"+
		"\34\16\34\u008f\3\34\3\34\2\35\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17"+
		"\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22"+
		"\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1\61\32\1\63\33\1\65\34\1"+
		"\67\35\2\3\2\6\3))\4C\\c|\3\62;\4\13\13\"\"\u0097\2\3\3\2\2\2\2\5\3\2"+
		"\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3"+
		"\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\39\3\2\2\2\5D\3\2\2\2\7G\3\2\2\2\tI\3"+
		"\2\2\2\13K\3\2\2\2\rM\3\2\2\2\17O\3\2\2\2\21Q\3\2\2\2\23T\3\2\2\2\25V"+
		"\3\2\2\2\27Z\3\2\2\2\31]\3\2\2\2\33a\3\2\2\2\35d\3\2\2\2\37f\3\2\2\2!"+
		"i\3\2\2\2#k\3\2\2\2%p\3\2\2\2\'v\3\2\2\2)x\3\2\2\2+z\3\2\2\2-|\3\2\2\2"+
		"/~\3\2\2\2\61\u0080\3\2\2\2\63\u0083\3\2\2\2\65\u0088\3\2\2\2\67\u008d"+
		"\3\2\2\29?\7)\2\2:>\n\2\2\2;<\7)\2\2<>\7)\2\2=:\3\2\2\2=;\3\2\2\2>A\3"+
		"\2\2\2?=\3\2\2\2?@\3\2\2\2@B\3\2\2\2A?\3\2\2\2BC\7)\2\2C\4\3\2\2\2DE\7"+
		"~\2\2EF\7~\2\2F\6\3\2\2\2GH\7<\2\2H\b\3\2\2\2IJ\7A\2\2J\n\3\2\2\2KL\7"+
		".\2\2L\f\3\2\2\2MN\7*\2\2N\16\3\2\2\2OP\7+\2\2P\20\3\2\2\2QR\7#\2\2RS"+
		"\7?\2\2S\22\3\2\2\2TU\7?\2\2U\24\3\2\2\2VW\7P\2\2WX\7Q\2\2XY\7V\2\2Y\26"+
		"\3\2\2\2Z[\7Q\2\2[\\\7T\2\2\\\30\3\2\2\2]^\7C\2\2^_\7P\2\2_`\7F\2\2`\32"+
		"\3\2\2\2ab\7>\2\2bc\7?\2\2c\34\3\2\2\2de\7>\2\2e\36\3\2\2\2fg\7@\2\2g"+
		"h\7?\2\2h \3\2\2\2ij\7@\2\2j\"\3\2\2\2kl\7v\2\2lm\7t\2\2mn\7w\2\2no\7"+
		"g\2\2o$\3\2\2\2pq\7h\2\2qr\7c\2\2rs\7n\2\2st\7u\2\2tu\7g\2\2u&\3\2\2\2"+
		"vw\7`\2\2w(\3\2\2\2xy\7,\2\2y*\3\2\2\2z{\7\61\2\2{,\3\2\2\2|}\7\'\2\2"+
		"}.\3\2\2\2~\177\7-\2\2\177\60\3\2\2\2\u0080\u0081\7/\2\2\u0081\62\3\2"+
		"\2\2\u0082\u0084\t\3\2\2\u0083\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085"+
		"\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\64\3\2\2\2\u0087\u0089\t\4\2"+
		"\2\u0088\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u0088\3\2\2\2\u008a\u008b"+
		"\3\2\2\2\u008b\66\3\2\2\2\u008c\u008e\t\5\2\2\u008d\u008c\3\2\2\2\u008e"+
		"\u008f\3\2\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0091\3\2"+
		"\2\2\u0091\u0092\b\34\2\2\u00928\3\2\2\2\b\2=?\u0085\u008a\u008f";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}