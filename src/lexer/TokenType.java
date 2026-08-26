package lexer;

public enum TokenType {
    // keywords

    NUMBER_TYPE,
    DECIMAL_TYPE,
    IF,
    ELSE,
    WHILE,
    PRINT,

    // Literals

    INTEGER_LITERAL,
    DECIMAL_LITERAL,
    STRING_LITERAL,

    // Identifier

    IDENTIFIER,

    // Arithmetic operators

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,

    // Assignment and comparison operators

    ASSIGN,
    EQUAL,
    NOT_EQUAL,
    LESS_THAN,
    GREATER_THAN,
    LESS_EQUAL,
    GREATER_EQUAL,

    // symbols

    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    SEMICOLON,

    // End of source code

    EOF

}
