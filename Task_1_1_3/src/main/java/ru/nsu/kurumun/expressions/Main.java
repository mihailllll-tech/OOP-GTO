package ru.nsu.kurumun.expressions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import ru.nsu.kurumun.expressions.io.ExpressionIo;
import ru.nsu.kurumun.expressions.model.Expression;
import ru.nsu.kurumun.expressions.parser.ExpressionParser;

/**
 * Приложение для работы с математическими выражениями.
 */
public final class Main {

    private Main() {
    }

    /**
     * Читает выражение, вычисляет его значение и выводит производную.
     *
     * @param args необязательные пути к входному файлу и файлу для записи производной
     */
    public static void main(String[] args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            if (arguments.size() > 2) {
                throw new IllegalArgumentException(
                        "Expected at most two arguments: input file and derivative output file"
                );
            }

            Expression expression;

            if (arguments.isEmpty()) {
                System.out.println("Enter expression:");
                expression = ExpressionParser.parse(scanner.nextLine());
            } else {
                expression = ExpressionIo.read(Path.of(arguments.get(0)));
            }

            System.out.println("Enter variable values (example: x = 10; y = 13):");
            String assignments = scanner.nextLine();

            System.out.println("Differentiate with respect to:");
            String variable = scanner.nextLine().trim();

            if (variable.isEmpty()) {
                throw new IllegalArgumentException("Variable name must not be blank");
            }

            Expression derivative = expression.derivative(variable);
            int value = expression.eval(assignments);

            System.out.print("Expression: ");
            expression.print();

            System.out.println("Value: " + value);

            System.out.print("Derivative: ");
            derivative.print();

            if (arguments.size() == 2) {
                ExpressionIo.write(Path.of(arguments.get(1)), derivative);
                System.out.println("Derivative saved to: " + arguments.get(1));
            }
        } catch (IOException | IllegalArgumentException
                 | ArithmeticException | NoSuchElementException exception) {
            System.err.println("Error: " + exception.getMessage());
        }
    }
}