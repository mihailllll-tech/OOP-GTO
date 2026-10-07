package ru.nsu.kurumun.expressions.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.nsu.kurumun.expressions.model.Add;
import ru.nsu.kurumun.expressions.model.Div;
import ru.nsu.kurumun.expressions.model.Expression;
import ru.nsu.kurumun.expressions.model.Mul;
import ru.nsu.kurumun.expressions.model.Sub;

/**
 * Бинарные арифметические операции и создание соответствующих выражений.
 */
enum Operator {

    /**
     * Сложение.
     */
    ADD('+') {
        @Override
        Expression create(Expression left, Expression right) {
            return new Add(left, right);
        }
    },

    /**
     * Вычитание.
     */
    SUB('-') {
        @Override
        Expression create(Expression left, Expression right) {
            return new Sub(left, right);
        }
    },

    /**
     * Умножение.
     */
    MUL('*') {
        @Override
        Expression create(Expression left, Expression right) {
            return new Mul(left, right);
        }
    },

    /**
     * Деление.
     */
    DIV('/') {
        @Override
        Expression create(Expression left, Expression right) {
            return new Div(left, right);
        }
    };

    private static final List<Operator> OPERATORS = new ArrayList<>(Arrays.asList(values()));

    private final char symbol;

    Operator(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Находит операцию по её символу.
     *
     * @param symbol символ операции
     * @return соответствующая операция или {@code null}, если символ неизвестен
     */
    static Operator fromSymbol(char symbol) {
        for (Operator operator : OPERATORS) {
            if (operator.symbol == symbol) {
                return operator;
            }
        }
        return null;
    }

    /**
     * Создаёт выражение этой операции с указанными операндами.
     *
     * @param left левый операнд
     * @param right правый операнд
     * @return выражение для двух операндов
     * @throws NullPointerException если хотя бы один операнд равен {@code null}
     */
    abstract Expression create(Expression left, Expression right);
}
