package lexer;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;
    private final SymbolTable symbolTable;

    // Updated constructor to accept SymbolTable
    public Parser(List<Token> tokens, SymbolTable symbolTable) {
        this.tokens = tokens;
        this.symbolTable = symbolTable;
    }

    public ASTNode parse() {
        return program();
    }

    private ASTNode program() {
        List<ASTNode> statements = new ArrayList<>();

        while (!isAtEnd()) {
            ASTNode statement = statement();
            if (statement != null) {
                statements.add(statement);
            }
        }

        return new ProgNode(statements);
    }

    private ASTNode statement() {
        TokenType type = currentTok().getType();

        if (type == TokenType.NUMBER_TYPE || type == TokenType.DECIMAL_TYPE) {
            return declarationStatement();
        }

        if (type == TokenType.IF) {
            return ifStatement();
        }

        if (type == TokenType.WHILE) {
            return whileStatement();
        }

        if (type == TokenType.PRINT) {
            return printStatement();
        }

        if (type == TokenType.IDENTIFIER) {
            return assignmentStatement();
        }

        error("Unexpected token: " + currentTok().getLexeme());
        advance();

        return null;
    }

    private ASTNode declarationStatement() {
        Token typeToken = advance();

        Token name = consume(
                TokenType.IDENTIFIER,
                "Expected variable name.");

        ASTNode value = null;

        if (match(TokenType.ASSIGN)) {
            value = expression();
        }

        consume(
                TokenType.SEMICOLON,
                "Expected ';' after declaration.");

        if (name != null) {
            // Inserts declared variable into the Symbol Table
            symbolTable.insert(name.getLexeme(), typeToken.getLexeme(), name.getLine());
        }

        return new DecNode(
                typeToken.getLexeme(),
                name != null ? name.getLexeme() : "",
                value,
                typeToken.getLine());
    }

    private ASTNode assignmentStatement() {
        Token name = consume(
                TokenType.IDENTIFIER,
                "Expected variable name.");

        consume(
                TokenType.ASSIGN,
                "Expected '=' after variable name.");

        ASTNode value = expression();

        consume(
                TokenType.SEMICOLON,
                "Expected ';' after assignment.");

        return new AssignNode(
                name != null ? name.getLexeme() : "",
                value);
    }

    private ASTNode ifStatement() {
        consume(TokenType.IF, "Expected 'যদি'.");
        consume(TokenType.LEFT_PAREN, "Expected '('.");
        ASTNode condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')'.");
        consume(TokenType.LEFT_BRACE, "Expected '{'.");

        List<ASTNode> thenStatements = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            ASTNode statement = statement();
            if (statement != null) {
                thenStatements.add(statement);
            }
        }

        consume(TokenType.RIGHT_BRACE, "Expected '}'.");

        List<ASTNode> elseStatements = new ArrayList<>();

        if (match(TokenType.ELSE)) {
            consume(TokenType.LEFT_BRACE, "Expected '{' after 'নাহলে'.");

            while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                ASTNode statement = statement();
                if (statement != null) {
                    elseStatements.add(statement);
                }
            }

            consume(TokenType.RIGHT_BRACE, "Expected '}'.");
        }

        return new IfNode(condition, thenStatements, elseStatements);
    }

    private ASTNode whileStatement() {
        consume(TokenType.WHILE, "Expected 'যতক্ষণ'.");
        consume(TokenType.LEFT_PAREN, "Expected '('.");
        ASTNode condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')'.");
        consume(TokenType.LEFT_BRACE, "Expected '{'.");

        List<ASTNode> statements = new ArrayList<>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            ASTNode statement = statement();
            if (statement != null) {
                statements.add(statement);
            }
        }

        consume(TokenType.RIGHT_BRACE, "Expected '}'.");

        return new WhileNode(condition, statements);
    }

    private ASTNode printStatement() {
        consume(TokenType.PRINT, "Expected 'দেখাও'.");
        consume(TokenType.LEFT_PAREN, "Expected '('.");
        ASTNode value = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')'.");
        consume(TokenType.SEMICOLON, "Expected ';'.");

        return new PrintNode(value);
    }

    private ASTNode expression() {
        return equality();
    }

    private ASTNode equality() {
        ASTNode left = comparison();

        while (match(TokenType.EQUAL, TokenType.NOT_EQUAL)) {
            Token operator = previous();
            ASTNode right = comparison();
            left = new BinOpNode(left, operator.getLexeme(), right);
        }

        return left;
    }

    private ASTNode comparison() {
        ASTNode left = term();

        while (match(TokenType.GREATER_THAN, TokenType.GREATER_EQUAL, TokenType.LESS_THAN, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            ASTNode right = term();
            left = new BinOpNode(left, operator.getLexeme(), right);
        }

        return left;
    }

    private ASTNode term() {
        ASTNode left = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = factor();
            left = new BinOpNode(left, operator.getLexeme(), right);
        }

        return left;
    }

    private ASTNode factor() {
        ASTNode left = primary();

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token operator = previous();
            ASTNode right = primary();
            left = new BinOpNode(left, operator.getLexeme(), right);
        }

        return left;
    }

    private ASTNode primary() {
        if (match(TokenType.INTEGER_LITERAL)) {
            return new IntNode(previous().getLexeme());
        }

        if (match(TokenType.DECIMAL_LITERAL)) {
            return new DecimalNode(previous().getLexeme());
        }

        if (match(TokenType.STRING_LITERAL)) {
            return new StringNode(previous().getLexeme());
        }

        if (match(TokenType.IDENTIFIER)) {
            return new IdNode(previous().getLexeme());
        }

        if (match(TokenType.LEFT_PAREN)) {
            ASTNode expression = expression();
            consume(TokenType.RIGHT_PAREN, "Expected ')'.");
            return expression;
        }

        error("Expected expression, found: " + currentTok().getLexeme());
        advance();

        return null;
    }

    private Token currentTok() {
        if (current < tokens.size()) {
            return tokens.get(current);
        }
        return tokens.get(tokens.size() - 1);
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return currentTok().getType() == TokenType.EOF;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return type == TokenType.EOF;
        }
        return currentTok().getType() == type;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        error(message);
        return null;
    }

    private void error(String message) {
        Token token = currentTok();
        System.err.println("Parser Error at line " + token.getLine() + ": " + message);
    }
}