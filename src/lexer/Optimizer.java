package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Optimizer {

    private final List<String> irCode;

    private final List<String> optimizedCode = new ArrayList<>();

    public Optimizer(List<String> irCode) {
        this.irCode = irCode;
    }

    public void optimize() {

        System.out.println();
        System.out.println("========== OPTIMIZED THREE-ADDRESS CODE ==========");

        optimizedCode.clear();

        Map<String, String> copies = new HashMap<>();

        for (String line : irCode) {

            String optimizedLine = optimizeLine(line, copies);

            optimizedCode.add(optimizedLine);
        }

        for (String line : optimizedCode) {
            System.out.println(line);
        }

        System.out.println("==================================================");
    }

    public List<String> getOptimizedCode() {

        return new ArrayList<>(optimizedCode);
    }

    private String optimizeLine(
            String line,
            Map<String, String> copies) {

        String trimmed = line.trim();

        if (trimmed.isEmpty()) {
            return line;
        }

        if (trimmed.endsWith(":")) {

            copies.clear();

            return line;
        }

        if (trimmed.startsWith("GOTO")) {

            copies.clear();

            return line;
        }

        if (trimmed.startsWith("IF")) {

            copies.clear();

            return line;
        }

        if (trimmed.startsWith("PRINT")) {

            String value = trimmed.substring(6).trim();

            if (copies.containsKey(value)) {
                value = copies.get(value);
            }

            return "PRINT " + value;
        }

        if (!trimmed.contains("=")) {
            return line;
        }

        String[] parts = trimmed.split("=", 2);

        String left = parts[0].trim();
        String right = parts[1].trim();

        copies.remove(left);

        if (copies.containsKey(right)) {
            right = copies.get(right);
        }

        String[] expression = right.split("\\s+");

        if (expression.length == 3) {

            String operand1 = expression[0];
            String operator = expression[1];
            String operand2 = expression[2];

            if (copies.containsKey(operand1)) {
                operand1 = copies.get(operand1);
            }

            if (copies.containsKey(operand2)) {
                operand2 = copies.get(operand2);
            }

            if (isNumber(operand1) && isNumber(operand2)) {

                String result = calculate(
                        operand1,
                        operator,
                        operand2);

                copies.put(left, result);

                return left + " = " + result;
            }

            if (operator.equals("+") && operand2.equals("0")) {

                copies.put(left, operand1);

                return left + " = " + operand1;
            }

            if (operator.equals("+") && operand1.equals("0")) {

                copies.put(left, operand2);

                return left + " = " + operand2;
            }

            if (operator.equals("-") && operand2.equals("0")) {

                copies.put(left, operand1);

                return left + " = " + operand1;
            }

            if (operator.equals("*") && operand2.equals("1")) {

                copies.put(left, operand1);

                return left + " = " + operand1;
            }

            if (operator.equals("*") && operand1.equals("1")) {

                copies.put(left, operand2);

                return left + " = " + operand2;
            }

            if (operator.equals("/") && operand2.equals("1")) {

                copies.put(left, operand1);

                return left + " = " + operand1;
            }

            if (operator.equals("*") && operand2.equals("0")) {

                copies.put(left, "0");

                return left + " = 0";
            }

            if (operator.equals("*") && operand1.equals("0")) {

                copies.put(left, "0");

                return left + " = 0";
            }

            return left + " = "
                    + operand1 + " "
                    + operator + " "
                    + operand2;
        }

        if (isIdentifier(right)) {

            copies.put(left, right);

            return left + " = " + right;
        }

        return left + " = " + right;
    }

    private boolean isNumber(String value) {

        String number = convertBengaliDigits(value);

        try {

            Double.parseDouble(number);

            return true;

        } catch (NumberFormatException e) {

            return false;
        }
    }

    private String calculate(
            String first,
            String operator,
            String second) {

        double a = Double.parseDouble(
                convertBengaliDigits(first));

        double b = Double.parseDouble(
                convertBengaliDigits(second));

        double result = 0;

        switch (operator) {

            case "+":
                result = a + b;
                break;

            case "-":
                result = a - b;
                break;

            case "*":
                result = a * b;
                break;

            case "/":

                if (b == 0) {

                    return first + " "
                            + operator + " "
                            + second;
                }

                result = a / b;
                break;

            default:

                return first + " "
                        + operator + " "
                        + second;
        }

        if (result == (int) result) {

            return String.valueOf((int) result);
        }

        return String.valueOf(result);
    }

    private boolean isIdentifier(String value) {

        if (value.isEmpty()) {
            return false;
        }

        char first = value.charAt(0);

        if (!(Character.isLetter(first) || first == '_')) {
            return false;
        }

        for (int i = 1; i < value.length(); i++) {

            char c = value.charAt(i);

            if (!(Character.isLetterOrDigit(c) || c == '_')) {
                return false;
            }
        }

        return true;
    }

    private String convertBengaliDigits(String value) {

        StringBuilder result = new StringBuilder();

        for (char c : value.toCharArray()) {

            if (c >= '\u09E6' && c <= '\u09EF') {

                char englishDigit = (char) ('0' + (c - '\u09E6'));

                result.append(englishDigit);

            } else {

                result.append(c);
            }
        }

        return result.toString();
    }
}