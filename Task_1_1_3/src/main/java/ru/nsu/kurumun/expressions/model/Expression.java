package ru.nsu.kurumun.expressions.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Expression {
    protected Expression() {
    }
    public final int eval(String assignments) {
        if (assignments == null) {
            throw new IllegalArgumentException("Assignments must not be null");
        }

        Map<String, Integer> variables = new HashMap<>();

        if (assignments.isBlank()) {
            return eval(variables);
        }

        List<String> entries = new ArrayList<>(
                Arrays.asList(assignments.split(";", -1))
        );

        for (String assignment : entries) {
            List<String> parts = new ArrayList<>(
                    Arrays.asList(assignment.split("=", -1))
            );

            if (parts.size() != 2 || parts.get(0).isBlank()) {
                throw new IllegalArgumentException("Invalid assignment: " + assignment);
            }

            String name = parts.get(0).trim();
            int value = Integer.parseInt(parts.get(1).trim());

            if (variables.putIfAbsent(name, value) != null) {
                throw new IllegalArgumentException("Duplicate variable: " + name);
            }
        }

        return eval(variables);
    }

    public abstract int eval(Map<String, Integer> variables);

    public abstract Expression derivative(String variable);

    @Override
    public abstract String toString();

    public void print() {
        System.out.println(this);
    }
}