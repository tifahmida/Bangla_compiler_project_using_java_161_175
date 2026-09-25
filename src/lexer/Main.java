package lexer;

import java.util.List;
import java.nio.charset.StandardCharsets;

public class Main {

        public static void main(String[] args) {

                try {

                        System.setOut(
                                        new java.io.PrintStream(
                                                        System.out,
                                                        true,
                                                        StandardCharsets.UTF_8.name()));

                        System.setErr(
                                        new java.io.PrintStream(
                                                        System.err,
                                                        true,
                                                        StandardCharsets.UTF_8.name()));

                } catch (Exception e) {

                        e.printStackTrace();
                }

                String sourceCode = "সংখ্যা x = ১২;\n" +
                                "দশমিক y = ৫.৫;\n\n" +

                                "যদি (x > ৫) {\n" +
                                "    দেখাও(x);\n" +
                                "}\n" +

                                "নাহলে {\n" +
                                "    দেখাও(\"আপনার দেওয়া সংখ্যাটি ৫ থেকে ছোট।\");\n" +
                                "}\n\n" +

                                "যতক্ষণ (x < ৫০) {\n" +
                                "    x = x + ১;\n" +
                                "    দেখাও(\"নম্বরটি ১ করে বেড়েছে\");\n" +
                                "}";

                System.out.println("------- SOURCE CODE ---------");

                System.out.println(sourceCode);

                Lexer lexer = new Lexer(sourceCode);

                List<Token> tokens = lexer.tokenize();

                System.out.println("\n-------- TOKENS -----------");

                for (Token token : tokens) {

                        System.out.println(token);
                }

                SymbolTable symbolTable = new SymbolTable();

                Parser parser = new Parser(tokens, symbolTable);

                ASTNode ast = parser.parse();

                System.out.println(
                                "\n------- AST / PARSE TREE ---------");

                if (ast != null) {

                        ast.print("");
                }

                symbolTable.print();

                System.out.println(
                                "------- SEMANTIC ANALYSIS ---------");

                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(
                                tokens,
                                symbolTable);

                semanticAnalyzer.analyze();

                System.out.println();

                IRCodeGenerator irGenerator = new IRCodeGenerator(tokens);

                List<String> irCode = irGenerator.generate();

                irGenerator.printIR();

                System.out.println();

                Optimizer optimizer = new Optimizer(irCode);

                optimizer.optimize();

                List<String> optimizedCode = optimizer.getOptimizedCode();

                System.out.println();

                TargetCodeGenerator targetGenerator = new TargetCodeGenerator(
                                optimizedCode);

                targetGenerator.generate();

                targetGenerator.printTargetCode();

                System.out.println();

                System.out.println(
                                "--------- COMPILATION COMPLETED --------");

                System.out.println(
                                "Lexer, Parser, AST, Symbol Table, " +
                                                "Semantic Analysis, IR Generation, " +
                                                "Optimization and Target Code Generation " +
                                                "completed successfully.");
        }
}