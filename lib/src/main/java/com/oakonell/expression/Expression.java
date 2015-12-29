package com.oakonell.expression;

import com.oakonell.expression.grammar.ExpressionLexer;
import com.oakonell.expression.grammar.ExpressionParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Rob on 12/23/2015.
 */
public class Expression<T> {
    private final ExpressionEvaluator evaluator;
    private final ExpressionType<T> resultType;
    private final ExpressionParser parser;
    private final ExpressionContext context;

    public static <T> Expression<T> parse(String formula, ExpressionType<T> expectedResultType, ExpressionContext context) {
        ANTLRInputStream stream;
        try {
            stream = new ANTLRInputStream(new StringReader(formula));
        } catch (IOException e) {
            throw new RuntimeException("IO Exception reading string", e);
        }
        ExpressionLexer lexer = new ExpressionLexer(stream);
        TokenStream tokens = new CommonTokenStream(lexer);
        ExpressionParser parser = new ExpressionParser(tokens);


        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());

        ExpressionParser.RootContext root;
        try {
            root = parser.root();
        } catch (ParseCancellationException e) {
            if (e.getCause() instanceof RecognitionException) {
                RecognitionException recogException = (RecognitionException) e.getCause();

                final RuleContext ruleContext = recogException.getCtx().getRuleContext();
                if (ruleContext instanceof ParserRuleContext) {
                    ParserRuleContext pruleContext = (ParserRuleContext) ruleContext;
                    int index = pruleContext.getStart().getStartIndex();
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < index; i++) {
                        builder.append(" ");
                    }
                    System.out.println("Error parsing '" + pruleContext.getText() + "' in formula");
                    System.out.println(formula);
                    System.out.println(builder.toString() + "^");
                }
                throw recogException;
            }
            System.out.println("Error parsing '" + formula + "'");
            throw e;
        }

        ExpressionValidator validator = new ExpressionValidator(context);
        ExpressionType<?> resultType;
        resultType = validator.visitRoot(root);

        if (!resultType.equals(expectedResultType)) {
            throw new RuntimeException("Expression '" + formula + "' results in " + resultType + ", but is expecting a " + expectedResultType);
        }

        return new Expression(parser, context, resultType);
    }

    private Expression(ExpressionParser parser, ExpressionContext context, ExpressionType<T> resultType) {
        evaluator = new ExpressionEvaluator(context);
        this.parser = parser;
        this.resultType = resultType;
        this.context = context;
    }

    public T evaluate() {
        parser.reset();
        return ((ExpressionValue<T>) evaluator.visitRoot(parser.root())).getValue();
    }

    public ExpressionContext getContext() {
        return context;
    }

    protected ExpressionParser getParser() {
        return parser;
    }
}
