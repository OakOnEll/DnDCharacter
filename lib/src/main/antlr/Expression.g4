grammar Expression;
root : genericExpression EOF;

genericExpression:
         K_L_PAREN  genericExpression K_R_PAREN                                                      #exprParen
        | function                                                                                   #exprFunction
        | variable                                                                                   #exprVariable

// dice expressions
       | K_D genericExpression                                                                       #exprSingleDie
       | genericExpression K_D genericExpression                                                     #exprDie

// String expressions
        | genericExpression K_DOUBLE_PIPE genericExpression                                          #stringConcat
        | STRING_LITERAL                                                                             #stringLiteral

// Math expressions
       | (K_MINUS | K_PLUS) genericExpression                                                        #mathUnary
       | genericExpression K_CARET genericExpression                                                 #mathPower
       | genericExpression (K_STAR|K_SLASH|K_MOD) genericExpression                                  #mathProduct
       | genericExpression (K_PLUS | K_MINUS) genericExpression                                      #mathSum
       | NUM                                                                                         #mathLiteral

// Boolean expressions
       | K_NOT genericExpression                                                                     #logicalNot
       | genericExpression (K_EQUALS | K_NOT_EQUALS | K_GT | K_GTE | K_LT | K_LTE) genericExpression #logicalCompare
       | genericExpression K_AND genericExpression                                                   #logicalAnd
       | genericExpression K_OR genericExpression                                                    #logicalOr
       | boolean_literal                                                                             #logicalLiteral

// low order precedence
        | genericExpression K_QUESTION_MARK genericExpression K_COLON genericExpression               #exprConditional


     ;

function : ID K_L_PAREN genericExpression (K_COMMA genericExpression)* K_R_PAREN;
variable : ID ;
STRING_LITERAL : '\'' (~'\'' | '\'\'')* '\'';
boolean_literal : K_TRUE | K_FALSE;

K_D : 'd';
K_DOUBLE_PIPE : '||';
K_COLON : ':' ;
K_QUESTION_MARK : '?';
K_COMMA: ',';
K_L_PAREN: '(';
K_R_PAREN: ')';
K_NOT_EQUALS: '!=';
K_EQUALS: '=';
K_NOT : 'NOT';
K_OR : 'OR';
K_AND : 'AND';
K_LTE : '<=';
K_LT : '<';
K_GTE : '>=';
K_GT : '>';
K_TRUE : 'true';
K_FALSE : 'false';
K_CARET :   '^' ;
K_STAR :   '*' ; // assigns token name to '*' used above in grammar
K_SLASH :   '/' ;
K_MOD :   '%' ;
K_PLUS :   '+' ;
K_MINUS :   '-' ;
ID  :   [a-zA-Z]+ ;      // match identifiers
NUM :   [0-9]+ ;         // match integers
WS  :   ([ \t]+|EOF) -> skip ; // toss out whitespace
