package lexer;

import java.util.ArrayList;
import java.util.List;

public class TargetCodeGenerator {

    private final List<String> optimizedCode;
    private final List<String> targetCode = new ArrayList<>();

    public TargetCodeGenerator(List<String> optimizedCode) {
        this.optimizedCode = optimizedCode;
    }

    public List<String> generate() {

        targetCode.clear();

        addCode("; ========================================");
        addCode(";        TARGET CODE - 8086 STYLE");
        addCode("; ========================================");

        addCode(".MODEL SMALL");
        addCode(".STACK 100H");
        addCode(".DATA");

        addCode("; Variables");

        generateDataSection();

        addCode("");
        addCode(".CODE");
        addCode("MAIN PROC");

        addCode("MOV AX, @DATA");
        addCode("MOV DS, AX");
        addCode("");

        for (String line : optimizedCode) {
            generateInstruction(line);
        }

        addCode("");
        addCode("MOV AH, 4CH");
        addCode("INT 21H");

        addCode("MAIN ENDP");
        addCode("END MAIN");

        return targetCode;
    }

    private void generateDataSection() {

        for (String line : optimizedCode) {

            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.endsWith(":")) {
                continue;
            }

            if (trimmed.startsWith("IF")) {
                continue;
            }

            if (trimmed.startsWith("GOTO")) {
                continue;
            }

            if (trimmed.startsWith("PRINT")) {
                continue;
            }

            if (trimmed.contains("=")) {

                String[] parts = trimmed.split("=", 2);

                String variable = parts[0].trim();

                if (isIdentifier(variable) && !isTemporary(variable)) {

                    boolean alreadyExists = false;

                    for (String existing : targetCode) {

                        if (existing.contains(variable + " DW")) {
                            alreadyExists = true;
                            break;
                        }
                    }

                    if (!alreadyExists) {
                        addCode(variable + " DW ?");
                    }
                }
            }
        }
    }

    private void generateInstruction(String line) {

        String trimmed = line.trim();

        if (trimmed.isEmpty()) {
            return;
        }

        if (trimmed.endsWith(":")) {

            addCode(trimmed);

            return;
        }

        if (trimmed.startsWith("GOTO")) {

            String label = trimmed.substring(4).trim();

            addCode("JMP " + label);

            return;
        }

        if (trimmed.startsWith("IF")) {

            generateCondition(trimmed);

            return;
        }

        if (trimmed.startsWith("PRINT")) {

            String value = trimmed.substring(5).trim();

            addCode("; PRINT " + value);

            // Simple output placeholder
            addCode("MOV DX, " + value);
            addCode("MOV AH, 02H");
            addCode("INT 21H");

            return;
        }

        if (trimmed.contains("=")) {

            generateAssignment(trimmed);

        }
    }

    private void generateAssignment(String line) {

        String[] parts = line.split("=", 2);

        String left = parts[0].trim();
        String right = parts[1].trim();

        if (!right.contains(" ")) {

            addCode("MOV AX, " + right);
            addCode("MOV " + left + ", AX");

            return;
        }

        String[] expression = right.split("\\s+");

        if (expression.length == 3) {

            String operand1 = expression[0];
            String operator = expression[1];
            String operand2 = expression[2];

            addCode("MOV AX, " + operand1);

            switch (operator) {

                case "+":

                    addCode("ADD AX, " + operand2);
                    break;

                case "-":

                    addCode("SUB AX, " + operand2);
                    break;

                case "*":

                    addCode("MOV BX, " + operand2);
                    addCode("MUL BX");
                    break;

                case "/":

                    addCode("MOV BX, " + operand2);
                    addCode("XOR DX, DX");
                    addCode("DIV BX");
                    break;

                default:

                    addCode("; Unsupported operator: " + operator);
                    break;
            }

            addCode("MOV " + left + ", AX");
        }
    }

    private void generateCondition(String line) {

        String condition = line.substring(2).trim();

        int gotoPosition = condition.indexOf("GOTO");

        if (gotoPosition == -1) {
            return;
        }

        String expression = condition.substring(0, gotoPosition).trim();

        String label = condition.substring(gotoPosition + 4).trim();

        String[] parts = expression.split("\\s+");

        if (parts.length != 3) {
            return;
        }

        String left = parts[0];
        String operator = parts[1];
        String right = parts[2];

        addCode("CMP " + left + ", " + right);

        switch (operator) {

            case ">":

                addCode("JG " + label);
                break;

            case ">=":

                addCode("JGE " + label);
                break;

            case "<":

                addCode("JL " + label);
                break;

            case "<=":

                addCode("JLE " + label);
                break;

            case "==":

                addCode("JE " + label);
                break;

            case "!=":

                addCode("JNE " + label);
                break;

            default:

                addCode("; Unsupported condition: " + operator);
                break;
        }
    }

    private boolean isIdentifier(String value) {

        if (value.isEmpty()) {
            return false;
        }

        char first = value.charAt(0);

        return Character.isLetter(first)
                || first == '_'
                || (first >= '\u0980'
                        && first <= '\u09FF');
    }

    private boolean isTemporary(String value) {

        return value.matches("t[0-9]+");
    }

    private void addCode(String code) {

        targetCode.add(code);
    }

    public void printTargetCode() {

        System.out.println();
        System.out.println("========== TARGET CODE ==========");

        for (String line : targetCode) {

            System.out.println(line);
        }

        System.out.println("=================================");
    }

    public List<String> getTargetCode() {

        return new ArrayList<>(targetCode);
    }
}