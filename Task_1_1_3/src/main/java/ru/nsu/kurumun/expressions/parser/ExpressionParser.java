package ru.nsu.kurumun.expressions.parser;

import ru.nsu.kurumun.expressions.model.Add;
import ru.nsu.kurumun.expressions.model.Div;
import ru.nsu.kurumun.expressions.model.Expression;
import ru.nsu.kurumun.expressions.model.Mul;
import ru.nsu.kurumun.expressions.model.Number;
import ru.nsu.kurumun.expressions.model.Sub;
import ru.nsu.kurumun.expressions.model.Variable;
public final class ExpressionParser {

    private ExpressionParser() {
    }

    public static Expression parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Expression must not be blank");
        }

        String text = input.trim();
        if (text.startsWith("(")) {
            return parseOperation(text);
        }

        if (isVariableName(text)) {
            return new Variable(text);
        }

        return new Number(Integer.parseInt(text));
    }

    private static boolean isVariableName(String text) {
        char first = text.charAt(0);
        if (!Character.isLetter(first) && first != '_') {
            return false;
        }

        for (int i = 1; i < text.length(); i++) {
            char current = text.charAt(i);
            if (!Character.isLetterOrDigit(current) && current != '_') {
                return false;
            }
        }

        return true;
    }
    private static Expression parseOperation(String text) {
        if (!text.endsWith(")")) {
            throw new IllegalArgumentException("Missing closing parenthesis: " + text);
        }

        String content = text.substring(1, text.length() - 1).trim();
        int depth = 0;

        for (int i = 0; i < content.length(); i++) {
            char symbol = content.charAt(i);

            if (symbol == '(') {
                depth++;
            } else if (symbol == ')') {
                depth--;
                if (depth < 0) {
                    throw new IllegalArgumentException("Unbalanced parentheses: " + text);
                }
            } else if (depth == 0 && i > 0 && isOperator(symbol)) {
                Expression left = parse(content.substring(0, i));
                Expression right = parse(content.substring(i + 1));

                return switch (symbol) {
                    case '+' -> new Add(left, right);
                    case '-' -> new Sub(left, right);
                    case '*' -> new Mul(left, right);
                    case '/' -> new Div(left, right);
                    default -> throw new IllegalArgumentException("Unknown operator: " + symbol);
                };
            }
        }

        if (depth != 0) {
            throw new IllegalArgumentException("Unbalanced parentheses: " + text);
        }

        return parse(content);
    }

    private static boolean isOperator(char symbol) {
        return symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/';
    }
}