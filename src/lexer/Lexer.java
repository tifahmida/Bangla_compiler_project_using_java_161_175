package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    // Bangla keywords
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("সংখ্যা", TokenType.NUMBER_TYPE);
        keywords.put("দশমিক", TokenType.DECIMAL_TYPE);
        keywords.put("যদি", TokenType.IF);
        keywords.put("নাহলে", TokenType.ELSE);
        keywords.put("যতক্ষণ", TokenType.WHILE);
        keywords.put("দেখাও", TokenType.PRINT);
    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {

        while (!isAtEnd()) {

            start = current;

            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", line));

        return tokens;
    }

    private void scanToken() {

        char c = advance();

        switch (c) {

            // Parentheses
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;

            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;

            // Braces
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;

            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;

            // Statement terminator
            case ';':
                addToken(TokenType.SEMICOLON);
                break;

            // Arithmetic operators
            case '+':
                addToken(TokenType.PLUS);
                break;

            case '-':
                addToken(TokenType.MINUS);
                break;

            case '*':
                addToken(TokenType.MULTIPLY);
                break;

            case '/':
                addToken(TokenType.DIVIDE);
                break;

            // Assignment or equality
            case '=':
                addToken(
                        match('=') ? TokenType.EQUAL : TokenType.ASSIGN);
                break;

            // Not equal
            case '!':
                if (match('=')) {
                    addToken(TokenType.NOT_EQUAL);
                } else {
                    error("Unexpected character '!'.");
                }
                break;

            // Less than / less than or equal
            case '<':
                addToken(
                        match('=') ? TokenType.LESS_EQUAL : TokenType.LESS_THAN);
                break;

            // Greater than / greater than or equal
            case '>':
                addToken(
                        match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER_THAN);
                break;

            // Ignore spaces
            case ' ':
            case '\r':
            case '\t':
                break;

            // New line
            case '\n':
                line++;
                break;

            // Anything else
            default:

                if (isDigit(c)) {
                    number();
                }

                else if (isIdentifierStart(c)) {
                    identifier();
                }

                else if (c == '"') {
                    string();
                }

                else {
                    error("Unexpected character: " + c);
                }

                break;
        }
    }

    // Identifier and keyword recognition

    private void identifier() {

        while (!isAtEnd() && isIdentifierPart(peek())) {
            advance();
        }

        String text = source.substring(start, current);

        TokenType type = keywords.get(text);

        if (type == null) {
            type = TokenType.IDENTIFIER;
        }

        addToken(type);
    }

    // Number recognition

    private void number() {

        while (!isAtEnd() && isDigit(peek())) {
            advance();
        }

        // Check for decimal point
        if (peek() == '.' && isDigit(peekNext())) {

            advance();

            while (!isAtEnd() && isDigit(peek())) {
                advance();
            }

            addToken(TokenType.DECIMAL_LITERAL);

        } else {

            addToken(TokenType.INTEGER_LITERAL);
        }
    }

    // String recognition

    private void string() {

        while (!isAtEnd() && peek() != '"') {

            if (peek() == '\n') {
                line++;
            }

            advance();
        }

        if (isAtEnd()) {
            error("Unterminated string.");
            return;
        }

        // Consume closing quotation mark
        advance();

        addToken(TokenType.STRING_LITERAL);
    }

    // Character checking (Updated for Bangla Unicode support)

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9') || (c >= '\u09E6' && c <= '\u09EF'); // English & Bangla Digits
    }

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_' || Character.getType(c) == Character.OTHER_LETTER;
    }

    private boolean isIdentifierPart(char c) {
        int type = Character.getType(c);
        return Character.isLetterOrDigit(c)
                || c == '_'
                || type == Character.NON_SPACING_MARK // Virama/Hasanta (্), Anusvara (ং)
                || type == Character.COMBINING_SPACING_MARK // Vowel signs (া, ি, ী, etc.)
                || type == Character.FORMAT; // ZWJ/ZWNJ
    }

    // Character movement

    private char advance() {

        return source.charAt(current++);
    }

    private char peek() {

        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    private char peekNext() {

        if (current + 1 >= source.length()) {
            return '\0';
        }

        return source.charAt(current + 1);
    }

    private boolean match(char expected) {

        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(current) != expected) {
            return false;
        }

        current++;

        return true;
    }

    private boolean isAtEnd() {

        return current >= source.length();
    }

    // Token creation

    private void addToken(TokenType type) {

        String text = source.substring(start, current);

        tokens.add(
                new Token(type, text, line));
    }

    // Error reporting

    private void error(String message) {

        System.err.println(
                "Lexer Error at line " + line + ": " + message);
    }
}