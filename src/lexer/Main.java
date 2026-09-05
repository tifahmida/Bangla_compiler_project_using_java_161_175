package lexer;

import java.util.List;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {

        try {
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            System.setErr(new java.io.PrintStream(System.err, true, StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sourceCode = "সংখ্যা x = 12;\n" +
                "দশমিক y = 5.5;\n\n" +
                "যদি (x > 5) {\n" +
                "    দেখাও(x);\n" +
                "}\n" +
                "নাহলে {\n" +
                "    দেখাও(\"আপনার দেওয়া সংখ্যাটি 5 থেকে ছোট।\");\n" +
                "}\n\n" +
                "যতক্ষণ (x < 50) {\n" +
                "    x = x + 1;\n" +
                "    দেখাও(\"নম্বরটি 1 করে বেড়েছে\");\n" +
                "}";

        System.out.println("------- SOURCE CODE ---------");
        System.out.println(sourceCode);

        // 1. Lexical Analysis
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.tokenize();

        System.out.println("\n-------- TOKENS -----------");
        for (Token token : tokens) {
            System.out.println(token);
        }

        // 2. Symbol Table Initialization
        SymbolTable symbolTable = new SymbolTable();

        // 3. Parsing
        Parser parser = new Parser(tokens, symbolTable);
        ASTNode ast = parser.parse();

        System.out.println("\n------- AST / PARSE TREE ---------");
        if (ast != null) {
            ast.print("");
        }

        // 4. Symbol Table Output
        symbolTable.print();

        System.out.println("--------- PARSING COMPLETED --------");
        System.out.println("AST and Symbol Table generated successfully.");
    }
}
