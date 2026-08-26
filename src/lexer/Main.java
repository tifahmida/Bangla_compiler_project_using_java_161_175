package lexer;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Enable UTF-8 output in the Java terminal
        System.setOut(new java.io.PrintStream(
                System.out, true, java.nio.charset.StandardCharsets.UTF_8));

        System.setErr(new java.io.PrintStream(
                System.err, true, java.nio.charset.StandardCharsets.UTF_8));

        String code = """
                সংখ্যা x = 12;
                দশমিক y = 5.5;

                যদি (x > 5) {
                    দেখাও(x);
                }
                নাহলে {
                    দেখাও("আপনার দেওয়া সংখ্যাটি 5 থেকে ছোট।");
                }

                যতক্ষণ (x < 50) {
                    x = x + 1;
                    দেখাও("নম্বরটি 1 করে বেড়েছে");
                }
                """;

        // Display the source code
        System.out.println(code);

        // Create the lexer
        Lexer lexer = new Lexer(code);

        // Scan the source code
        List<Token> tokens = lexer.scanTokens();

        // Display all tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}