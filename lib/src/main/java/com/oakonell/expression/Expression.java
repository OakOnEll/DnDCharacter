package com.oakonell.expression;

import com.oakonell.expression.grammar.ExpressionLexer;
import com.oakonell.expression.grammar.ExpressionParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Rob on 12/23/2015.
 */
public class Expression<T> {
    private final ExpressionEvaluator evaluator;
    private final ExpressionType<T> resultType;
    private ExpressionParser parser;
    ExpressionContext context;


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

        ExpressionValidator validator = new ExpressionValidator(context);
        ExpressionType<?> resultType = validator.visitRoot(parser.root());
        if (!resultType.equals(expectedResultType)) {
            throw new RuntimeException("Expression '" + formula + "' results in " + resultType + ", but is expecting a " + expectedResultType);
        }

        return new Expression(parser, context, resultType);
    }

    private Expression(ExpressionParser parser, ExpressionContext context, ExpressionType<T> resultType) {
        evaluator = new ExpressionEvaluator(context);
        this.parser = parser;
        this.resultType = resultType;
    }

    public T evaluate() {
        parser.reset();
        return ((ExpressionValue<T>) evaluator.visitRoot(parser.root())).getValue();
    }
}
