package lexer;

public enum TokenType {

    NUMBER_TYPE,
    DECIMAL_TYPE,
    IF,
    ELSE,
    WHILE,
    PRINT,

    INTEGER_LITERAL,
    DECIMAL_LITERAL,
    STRING_LITERAL,

    IDENTIFIER,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,

    ASSIGN,
    EQUAL,
    NOT_EQUAL,
    LESS_THAN,
    GREATER_THAN,
    LESS_EQUAL,
    GREATER_EQUAL,

    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    SEMICOLON,

    EOF

}
