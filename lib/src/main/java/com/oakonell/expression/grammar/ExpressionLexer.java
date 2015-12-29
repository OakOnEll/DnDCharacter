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
		STRING_LITERAL=1, K_D=2, K_DOUBLE_PIPE=3, K_COLON=4, K_QUESTION_MARK=5, 
		K_COMMA=6, K_L_PAREN=7, K_R_PAREN=8, K_NOT_EQUALS=9, K_EQUALS=10, K_NOT=11, 
		K_OR=12, K_AND=13, K_LTE=14, K_LT=15, K_GTE=16, K_GT=17, K_TRUE=18, K_FALSE=19, 
		K_CARET=20, K_STAR=21, K_SLASH=22, K_MOD=23, K_PLUS=24, K_MINUS=25, ID=26, 
		NUM=27, WS=28;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"STRING_LITERAL", "'d'", "'||'", "':'", "'?'", "','", "'('", "')'", "'!='", 
		"'='", "'NOT'", "'OR'", "'AND'", "'<='", "'<'", "'>='", "'>'", "'true'", 
		"'false'", "'^'", "'*'", "'/'", "'%'", "'+'", "'-'", "ID", "NUM", "WS"
	};
	public static final String[] ruleNames = {
		"STRING_LITERAL", "K_D", "K_DOUBLE_PIPE", "K_COLON", "K_QUESTION_MARK", 
		"K_COMMA", "K_L_PAREN", "K_R_PAREN", "K_NOT_EQUALS", "K_EQUALS", "K_NOT", 
		"K_OR", "K_AND", "K_LTE", "K_LT", "K_GTE", "K_GT", "K_TRUE", "K_FALSE", 
		"K_CARET", "K_STAR", "K_SLASH", "K_MOD", "K_PLUS", "K_MINUS", "ID", "NUM", 
		"WS"
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
		case 27: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\2\4\36\u009a\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27"+
		"\t\27\4\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2"+
		"\3\2\3\2\3\2\7\2@\n\2\f\2\16\2C\13\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\5\3"+
		"\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3"+
		"\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\21\3\21"+
		"\3\21\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\6\33"+
		"\u0088\n\33\r\33\16\33\u0089\3\34\6\34\u008d\n\34\r\34\16\34\u008e\3\35"+
		"\6\35\u0092\n\35\r\35\16\35\u0093\3\35\5\35\u0097\n\35\3\35\3\35\2\36"+
		"\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27"+
		"\r\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27"+
		"\1-\30\1/\31\1\61\32\1\63\33\1\65\34\1\67\35\19\36\2\3\2\6\3))\4C\\c|"+
		"\3\62;\4\13\13\"\"\u009f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3"+
		"\2\2\2\29\3\2\2\2\3;\3\2\2\2\5F\3\2\2\2\7H\3\2\2\2\tK\3\2\2\2\13M\3\2"+
		"\2\2\rO\3\2\2\2\17Q\3\2\2\2\21S\3\2\2\2\23U\3\2\2\2\25X\3\2\2\2\27Z\3"+
		"\2\2\2\31^\3\2\2\2\33a\3\2\2\2\35e\3\2\2\2\37h\3\2\2\2!j\3\2\2\2#m\3\2"+
		"\2\2%o\3\2\2\2\'t\3\2\2\2)z\3\2\2\2+|\3\2\2\2-~\3\2\2\2/\u0080\3\2\2\2"+
		"\61\u0082\3\2\2\2\63\u0084\3\2\2\2\65\u0087\3\2\2\2\67\u008c\3\2\2\29"+
		"\u0096\3\2\2\2;A\7)\2\2<@\n\2\2\2=>\7)\2\2>@\7)\2\2?<\3\2\2\2?=\3\2\2"+
		"\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2\2BD\3\2\2\2CA\3\2\2\2DE\7)\2\2E\4\3\2\2"+
		"\2FG\7f\2\2G\6\3\2\2\2HI\7~\2\2IJ\7~\2\2J\b\3\2\2\2KL\7<\2\2L\n\3\2\2"+
		"\2MN\7A\2\2N\f\3\2\2\2OP\7.\2\2P\16\3\2\2\2QR\7*\2\2R\20\3\2\2\2ST\7+"+
		"\2\2T\22\3\2\2\2UV\7#\2\2VW\7?\2\2W\24\3\2\2\2XY\7?\2\2Y\26\3\2\2\2Z["+
		"\7P\2\2[\\\7Q\2\2\\]\7V\2\2]\30\3\2\2\2^_\7Q\2\2_`\7T\2\2`\32\3\2\2\2"+
		"ab\7C\2\2bc\7P\2\2cd\7F\2\2d\34\3\2\2\2ef\7>\2\2fg\7?\2\2g\36\3\2\2\2"+
		"hi\7>\2\2i \3\2\2\2jk\7@\2\2kl\7?\2\2l\"\3\2\2\2mn\7@\2\2n$\3\2\2\2op"+
		"\7v\2\2pq\7t\2\2qr\7w\2\2rs\7g\2\2s&\3\2\2\2tu\7h\2\2uv\7c\2\2vw\7n\2"+
		"\2wx\7u\2\2xy\7g\2\2y(\3\2\2\2z{\7`\2\2{*\3\2\2\2|}\7,\2\2},\3\2\2\2~"+
		"\177\7\61\2\2\177.\3\2\2\2\u0080\u0081\7\'\2\2\u0081\60\3\2\2\2\u0082"+
		"\u0083\7-\2\2\u0083\62\3\2\2\2\u0084\u0085\7/\2\2\u0085\64\3\2\2\2\u0086"+
		"\u0088\t\3\2\2\u0087\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0087\3\2"+
		"\2\2\u0089\u008a\3\2\2\2\u008a\66\3\2\2\2\u008b\u008d\t\4\2\2\u008c\u008b"+
		"\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f"+
		"8\3\2\2\2\u0090\u0092\t\5\2\2\u0091\u0090\3\2\2\2\u0092\u0093\3\2\2\2"+
		"\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0097\3\2\2\2\u0095\u0097"+
		"\7\1\2\2\u0096\u0091\3\2\2\2\u0096\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098"+
		"\u0099\b\35\2\2\u0099:\3\2\2\2\t\2?A\u0089\u008e\u0093\u0096";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}