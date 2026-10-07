package ru.nsu.kurumun.expressions.parser;

import ru.nsu.kurumun.expressions.model.Expression;
import ru.nsu.kurumun.expressions.model.Number;
import ru.nsu.kurumun.expressions.model.Variable;

/**
 * Разбирает строковое представление выражений.
 */
public final class ExpressionParser {

    private static final int OPERATOR_NOT_FOUND = -1;

    private ExpressionParser() {
    }

    /**
     * Разбирает математическое выражение.
     *
     * @param input строка с выражением
     * @return выражение, полученное при разборе строки
     * @throws IllegalArgumentException если строка равна null или содержит неверное выражение
     */
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
        int operatorIndex = findOperatorIndex(content);
        if (operatorIndex == OPERATOR_NOT_FOUND) {
            return parse(content);
        }

        Expression left = parse(content.substring(0, operatorIndex));
        Expression right = parse(content.substring(operatorIndex + 1));
        return createOperation(content.charAt(operatorIndex), left, right);
    }

    /**
     * Находит первый бинарный оператор вне вложенных скобок.
     */
    private static int findOperatorIndex(String text) {
        int depth = 0;

        for (int i = 0; i < text.length(); i++) {
            char symbol = text.charAt(i);

            if (symbol == '(') {
                depth++;
            } else if (symbol == ')') {
                depth--;
                if (depth < 0) {
                    throw new IllegalArgumentException("Unbalanced parentheses: " + text);
                }
            } else if (depth == 0 && i > 0 && isOperator(symbol)) {
                return i;
            }
        }

        if (depth != 0) {
            throw new IllegalArgumentException("Unbalanced parentheses: " + text);
        }

        return OPERATOR_NOT_FOUND;
    }

    /**
     * Создаёт выражение для указанного оператора и двух операндов.
     */
    private static Expression createOperation(char symbol, Expression left, Expression right) {
        Operator operator = Operator.fromSymbol(symbol);
        if (operator == null) {
            throw new IllegalArgumentException("Unknown operator: " + symbol);
        }
        return operator.create(left, right);
    }

    private static boolean isOperator(char symbol) {
        return Operator.fromSymbol(symbol) != null;
    }
}
