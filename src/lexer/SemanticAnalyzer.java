package lexer;

import java.util.List;

public class SemanticAnalyzer {

    private final List<Token> tokens;
    private final SymbolTable symbolTable;

    private int current = 0;
    private boolean hasError = false;

    public SemanticAnalyzer(
            List<Token> tokens,
            SymbolTable symbolTable) {

        this.tokens = tokens;
        this.symbolTable = symbolTable;
    }

    public void analyze() {

        current = 0;
        hasError = false;

        while (!isAtEnd()) {

            checkStatement();
        }

        System.out.println();

        if (!hasError) {

            System.out.println(
                    "Semantic Analysis: No semantic errors found.");

        } else {

            System.out.println(
                    "Semantic Analysis: Errors found.");
        }
    }

    private void checkStatement() {

        TokenType type = currentToken().getType();

        if (type == TokenType.NUMBER_TYPE ||
                type == TokenType.DECIMAL_TYPE) {

            checkDeclaration();
            return;
        }

        if (type == TokenType.IDENTIFIER) {

            checkAssignment();
            return;
        }

        if (type == TokenType.PRINT) {

            checkPrint();
            return;
        }

        if (type == TokenType.IF) {

            checkIf();
            return;
        }

        if (type == TokenType.WHILE) {

            checkWhile();
            return;
        }

        advance();
    }

    private void checkDeclaration() {

        Token typeToken = advance();

        Token name = currentToken();

        if (name.getType() != TokenType.IDENTIFIER) {

            skipUntilSemicolon();
            return;
        }

        advance();

        if (match(TokenType.ASSIGN)) {

            String valueType = checkExpression();

            if (valueType != null) {

                checkTypeCompatibility(
                        typeToken.getLexeme(),
                        valueType,
                        name);
            }
        }

        consumeUntilSemicolon();
    }

    private void checkAssignment() {

        Token name = advance();

        Symbol symbol = symbolTable.lookup(
                name.getLexeme());

        if (symbol == null) {

            semanticError(
                    name,
                    "Variable '" +
                            name.getLexeme() +
                            "' is not declared.");
        }

        if (match(TokenType.ASSIGN)) {

            String valueType = checkExpression();

            if (symbol != null && valueType != null) {

                checkTypeCompatibility(
                        symbol.getType(),
                        valueType,
                        name);
            }
        }

        consumeUntilSemicolon();
    }

    private void checkPrint() {

        advance();

        if (match(TokenType.LEFT_PAREN)) {

            checkExpression();

            consume(
                    TokenType.RIGHT_PAREN);
        }

        consumeUntilSemicolon();
    }

    private void checkIf() {

        advance();

        if (match(TokenType.LEFT_PAREN)) {

            checkCondition();

            consume(
                    TokenType.RIGHT_PAREN);
        }

        if (match(TokenType.LEFT_BRACE)) {

            while (!check(TokenType.RIGHT_BRACE)
                    && !isAtEnd()) {

                checkStatement();
            }

            if (check(TokenType.RIGHT_BRACE)) {
                advance();
            }
        }

        if (match(TokenType.ELSE)) {

            if (match(TokenType.LEFT_BRACE)) {

                while (!check(TokenType.RIGHT_BRACE)
                        && !isAtEnd()) {

                    checkStatement();
                }

                if (check(TokenType.RIGHT_BRACE)) {
                    advance();
                }
            }
        }
    }

    private void checkWhile() {

        advance();

        if (match(TokenType.LEFT_PAREN)) {

            checkCondition();

            consume(
                    TokenType.RIGHT_PAREN);
        }

        if (match(TokenType.LEFT_BRACE)) {

            while (!check(TokenType.RIGHT_BRACE)
                    && !isAtEnd()) {

                checkStatement();
            }

            if (check(TokenType.RIGHT_BRACE)) {
                advance();
            }
        }
    }

    private void checkCondition() {

        while (!check(TokenType.RIGHT_PAREN)
                && !isAtEnd()) {

            if (check(TokenType.IDENTIFIER)) {

                Token identifier = advance();

                if (!symbolTable.contains(
                        identifier.getLexeme())) {

                    semanticError(
                            identifier,
                            "Variable '" +
                                    identifier.getLexeme() +
                                    "' is not declared.");
                }
            }

            else {
                advance();
            }
        }
    }

    private String checkExpression() {

        String resultType = null;

        while (!isAtEnd()
                && !check(TokenType.SEMICOLON)
                && !check(TokenType.RIGHT_PAREN)
                && !check(TokenType.RIGHT_BRACE)) {

            Token token = currentToken();

            if (token.getType() == TokenType.INTEGER_LITERAL) {

                resultType = combineTypes(
                        resultType,
                        "সংখ্যা");

                advance();
            }

            else if (token.getType() == TokenType.DECIMAL_LITERAL) {

                resultType = combineTypes(
                        resultType,
                        "দশমিক");

                advance();
            }

            else if (token.getType() == TokenType.STRING_LITERAL) {

                resultType = combineTypes(
                        resultType,
                        "string");

                advance();
            }

            else if (token.getType() == TokenType.IDENTIFIER) {

                Symbol symbol = symbolTable.lookup(
                        token.getLexeme());

                if (symbol == null) {

                    semanticError(
                            token,
                            "Variable '" +
                                    token.getLexeme() +
                                    "' is not declared.");

                } else {

                    resultType = combineTypes(
                            resultType,
                            symbol.getType());
                }

                advance();
            }

            else if (token.getType() == TokenType.PLUS
                    || token.getType() == TokenType.MINUS
                    || token.getType() == TokenType.MULTIPLY
                    || token.getType() == TokenType.DIVIDE) {

                advance();
            }

            else {

                advance();
            }
        }

        return resultType;
    }

    private String combineTypes(
            String currentType,
            String newType) {

        if (currentType == null) {
            return newType;
        }

        if (currentType.equals("দশমিক")
                && isNumeric(newType)) {

            return "দশমিক";
        }

        if (newType.equals("দশমিক")
                && isNumeric(currentType)) {

            return "দশমিক";
        }

        if (currentType.equals("সংখ্যা")
                && newType.equals("সংখ্যা")) {

            return "সংখ্যা";
        }

        if (currentType.equals("string")
                || newType.equals("string")) {

            return "string";
        }

        return currentType;
    }

    private void checkTypeCompatibility(
            String expectedType,
            String actualType,
            Token token) {

        if (expectedType.equals("সংখ্যা")) {

            if (!actualType.equals("সংখ্যা")) {

                semanticError(
                        token,
                        "Type mismatch. Variable '" +
                                token.getLexeme() +
                                "' expects সংখ্যা but found "
                                + actualType + ".");
            }
        }

        else if (expectedType.equals("দশমিক")) {

            if (!actualType.equals("সংখ্যা")
                    && !actualType.equals("দশমিক")) {

                semanticError(
                        token,
                        "Type mismatch. Variable '" +
                                token.getLexeme() +
                                "' expects দশমিক but found "
                                + actualType + ".");
            }
        }
    }

    private boolean isNumeric(String type) {

        return type.equals("সংখ্যা")
                || type.equals("দশমিক");
    }

    private void consume(TokenType type) {

        if (check(type)) {
            advance();
        }
    }

    private void consumeUntilSemicolon() {

        while (!isAtEnd()
                && !check(TokenType.SEMICOLON)) {

            advance();
        }

        if (check(TokenType.SEMICOLON)) {
            advance();
        }
    }

    private void skipUntilSemicolon() {

        while (!isAtEnd()
                && !check(TokenType.SEMICOLON)) {

            advance();
        }

        if (check(TokenType.SEMICOLON)) {
            advance();
        }
    }

    private void semanticError(
            Token token,
            String message) {

        hasError = true;

        System.err.println(
                "Semantic Error at line "
                        + token.getLine()
                        + ": "
                        + message);
    }

    private Token currentToken() {

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

    private boolean check(TokenType type) {

        if (isAtEnd()) {
            return type == TokenType.EOF;
        }

        return currentToken().getType() == type;
    }

    private boolean match(TokenType type) {

        if (check(type)) {

            advance();

            return true;
        }

        return false;
    }

    private boolean isAtEnd() {

        return currentToken().getType() == TokenType.EOF;
    }
}