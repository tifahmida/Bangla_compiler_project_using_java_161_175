package lexer;

import java.util.ArrayList;
import java.util.List;

public class IRCodeGenerator {

    private final List<Token> tokens;
    private int current = 0;

    private int tempCount = 1;
    private int labelCount = 1;

    private final List<String> irCode = new ArrayList<>();

    public IRCodeGenerator(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<String> generate() {

        irCode.clear();

        current = 0;
        tempCount = 1;
        labelCount = 1;

        while (!isAtEnd()) {
            generateStatement();
        }

        return irCode;
    }

    public void printIR() {

        System.out.println();
        System.out.println("========== INTERMEDIATE CODE ==========");

        for (String instruction : irCode) {
            System.out.println(instruction);
        }

        System.out.println("=======================================");
    }

    public List<String> getIRCode() {
        return new ArrayList<>(irCode);
    }

    private void generateStatement() {

        TokenType type = currentToken().getType();

        if (type == TokenType.NUMBER_TYPE ||
                type == TokenType.DECIMAL_TYPE) {

            generateDeclaration();
            return;
        }

        if (type == TokenType.IDENTIFIER) {

            generateAssignment();
            return;
        }

        if (type == TokenType.PRINT) {

            generatePrint();
            return;
        }

        if (type == TokenType.IF) {

            generateIf();
            return;
        }

        if (type == TokenType.WHILE) {

            generateWhile();
            return;
        }

        advance();
    }

    private void generateDeclaration() {

        advance();

        Token name = currentToken();
        advance();

        if (match(TokenType.ASSIGN)) {

            String value = expression();

            addIR(
                    name.getLexeme() + " = " + value);
        }

        consume(TokenType.SEMICOLON);
    }

    private void generateAssignment() {

        Token name = currentToken();
        advance();

        if (match(TokenType.ASSIGN)) {

            String value = expression();

            addIR(
                    name.getLexeme() + " = " + value);
        }

        consume(TokenType.SEMICOLON);
    }

    private void generatePrint() {

        advance();

        consume(TokenType.LEFT_PAREN);

        String value = expression();

        consume(TokenType.RIGHT_PAREN);
        consume(TokenType.SEMICOLON);

        addIR(
                "PRINT " + value);
    }

    private void generateIf() {

        advance();

        consume(TokenType.LEFT_PAREN);

        String condition = condition();

        consume(TokenType.RIGHT_PAREN);
        consume(TokenType.LEFT_BRACE);

        String trueLabel = newLabel();
        String falseLabel = newLabel();
        String endLabel = newLabel();

        addIR(
                "IF " + condition + " GOTO " + trueLabel);

        addIR(
                "GOTO " + falseLabel);

        addIR(trueLabel + ":");

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            generateStatement();
        }

        consume(TokenType.RIGHT_BRACE);

        if (match(TokenType.ELSE)) {

            addIR(
                    "GOTO " + endLabel);

            addIR(falseLabel + ":");

            consume(TokenType.LEFT_BRACE);

            while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                generateStatement();
            }

            consume(TokenType.RIGHT_BRACE);

            addIR(
                    endLabel + ":");

        } else {

            addIR(
                    falseLabel + ":");
        }
    }

    private void generateWhile() {

        advance();

        String startLabel = newLabel();
        String bodyLabel = newLabel();
        String endLabel = newLabel();

        addIR(startLabel + ":");

        consume(TokenType.LEFT_PAREN);

        String condition = condition();

        consume(TokenType.RIGHT_PAREN);
        consume(TokenType.LEFT_BRACE);

        addIR(
                "IF " + condition + " GOTO " + bodyLabel);

        addIR(
                "GOTO " + endLabel);

        addIR(
                bodyLabel + ":");

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            generateStatement();
        }

        consume(TokenType.RIGHT_BRACE);

        addIR(
                "GOTO " + startLabel);

        addIR(
                endLabel + ":");
    }

    private String condition() {

        String left = expression();

        if (check(TokenType.GREATER_THAN) ||
                check(TokenType.GREATER_EQUAL) ||
                check(TokenType.LESS_THAN) ||
                check(TokenType.LESS_EQUAL) ||
                check(TokenType.EQUAL) ||
                check(TokenType.NOT_EQUAL)) {

            Token operator = currentToken();
            advance();

            String right = expression();

            return left + " "
                    + operator.getLexeme()
                    + " "
                    + right;
        }

        return left;
    }

    private String expression() {

        String left = term();

        while (check(TokenType.PLUS) ||
                check(TokenType.MINUS)) {

            Token operator = currentToken();
            advance();

            String right = term();

            String temp = newTemp();

            addIR(
                    temp + " = "
                            + left + " "
                            + operator.getLexeme() + " "
                            + right);

            left = temp;
        }

        return left;
    }

    private String term() {

        String left = primary();

        while (check(TokenType.MULTIPLY) ||
                check(TokenType.DIVIDE)) {

            Token operator = currentToken();
            advance();

            String right = primary();

            String temp = newTemp();

            addIR(
                    temp + " = "
                            + left + " "
                            + operator.getLexeme() + " "
                            + right);

            left = temp;
        }

        return left;
    }

    private String primary() {

        if (match(TokenType.INTEGER_LITERAL)) {
            return previous().getLexeme();
        }

        if (match(TokenType.DECIMAL_LITERAL)) {
            return previous().getLexeme();
        }

        if (match(TokenType.IDENTIFIER)) {
            return previous().getLexeme();
        }

        if (match(TokenType.STRING_LITERAL)) {
            return "\"" + previous().getLexeme() + "\"";
        }

        if (match(TokenType.LEFT_PAREN)) {

            String value = expression();

            consume(TokenType.RIGHT_PAREN);

            return value;
        }

        return "";
    }

    private void addIR(String instruction) {
        irCode.add(instruction);
    }

    private String newTemp() {
        return "t" + tempCount++;
    }

    private String newLabel() {
        return "L" + labelCount++;
    }

    private Token currentToken() {

        if (current < tokens.size()) {
            return tokens.get(current);
        }

        return tokens.get(tokens.size() - 1);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return currentToken().getType() == TokenType.EOF;
    }

    private void advance() {

        if (!isAtEnd()) {
            current++;
        }
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

    private void consume(TokenType type) {

        if (check(type)) {
            advance();
        }
    }
}