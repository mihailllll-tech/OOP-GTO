package ru.nsu.kurumun.expressions.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExpressionTest {

    @Test
    void constantKeepsItsValueAfterDifferentiation() {
        Expression number = new Number(-7);

        assertEquals(-7, number.eval(Map.of("x", 10)));

        Expression derivative = number.derivative("x");

        assertEquals(0, derivative.eval(Map.of()));
        assertEquals("-7", number.toString());
    }

    @Test
    void variableUsesValuesFromEachEvaluation() {
        Expression variable = new Variable("speed");

        assertEquals(10, variable.eval(Map.of("s", 99, "speed", 10)));
        assertEquals(20, variable.eval(Map.of("speed", 20)));
    }

    @Test
    void variableDerivativeMatchesWholeName() {
        Expression variable = new Variable("speed");

        assertEquals(1, variable.derivative("speed").eval(Map.of()));
        assertEquals(0, variable.derivative("speedLimit").eval(Map.of()));
        assertEquals("speed", variable.toString());
    }

    @Test
    void missingVariableValueIsRejected() {
        Expression variable = new Variable("x");

        assertThrows(
                IllegalArgumentException.class,
                () -> variable.eval(Map.of("y", 10))
        );
    }

    @Test
    void additionPreservesOriginalWhenDifferentiated() {
        Expression expression = new Add(new Number(3), new Variable("x"));

        Expression derivative = expression.derivative("x");

        assertEquals(13, expression.eval(Map.of("x", 10)));
        assertEquals("(3+x)", expression.toString());
        assertEquals("(0+1)", derivative.toString());
        assertEquals(1, derivative.eval(Map.of()));
    }
    @Test
    void subtractionPreservesOperandOrder() {
        Expression expression = new Sub(new Number(3), new Variable("x"));

        Expression derivative = expression.derivative("x");

        assertEquals(-7, expression.eval(Map.of("x", 10)));
        assertEquals("(3-x)", expression.toString());
        assertEquals("(0-1)", derivative.toString());
        assertEquals(-1, derivative.eval(Map.of()));
    }
    @Test
    void assignmentExampleEvaluatesAndDifferentiates() {
        Expression expression = new Add(
                new Number(3),
                new Mul(new Number(2), new Variable("x"))
        );

        Expression derivative = expression.derivative("x");

        assertEquals(23, expression.eval("x = 10; y = 13"));
        assertEquals("(3+(2*x))", expression.toString());
        assertEquals("(0+((0*x)+(2*1)))", derivative.toString());
        assertEquals(2, derivative.eval("x = 10"));
    }

    @Test
    void squaredVariableUsesBothDerivativeTerms() {
        Expression variable = new Variable("x");
        Expression expression = new Mul(variable, variable);

        Expression derivative = expression.derivative("x");

        assertEquals(8, derivative.eval(Map.of("x", 4)));
        assertEquals(16, expression.eval(Map.of("x", 4)));
    }
    @Test
    void quotientUsesBothDerivativeTerms() {
        Expression variable = new Variable("x");
        Expression expression = new Div(
                new Mul(variable, variable),
                variable
        );

        Expression derivative = expression.derivative("x");

        assertEquals(3, expression.eval(Map.of("x", 3)));
        assertEquals(1, derivative.eval(Map.of("x", 3)));
        assertEquals("((x*x)/x)", expression.toString());
    }

    @Test
    void divisionByZeroIsRejected() {
        Expression expression = new Div(new Number(10), new Variable("x"));

        assertThrows(
                ArithmeticException.class,
                () -> expression.eval(Map.of("x", 0))
        );
    }
    @Test
    void assignmentStringSupportsSeveralNamedValues() {
        Expression expression = new Sub(
                new Variable("speed"),
                new Variable("offset")
        );

        assertEquals(-13, expression.eval(" speed = -10 ; offset = 3 "));
    }
    @Test
    void rejectsNullAssignments() {
        Expression expression = new Variable("x");

        assertThrows(
                IllegalArgumentException.class,
                () -> expression.eval((String) null)
        );
    }

    @Test
    void rejectsMalformedAssignments() {
        Expression expression = new Variable("x");

        assertThrows(IllegalArgumentException.class, () -> expression.eval("x"));
        assertThrows(IllegalArgumentException.class, () -> expression.eval(" = 10"));
        assertThrows(IllegalArgumentException.class, () -> expression.eval("x ="));
        assertThrows(IllegalArgumentException.class, () -> expression.eval("x = abc"));
        assertThrows(IllegalArgumentException.class, () -> expression.eval("x = 1 = 2"));
        assertThrows(IllegalArgumentException.class, () -> expression.eval("x = 1;"));
    }

    @Test
    void rejectsDuplicateAssignments() {
        Expression expression = new Variable("x");

        assertThrows(
                IllegalArgumentException.class,
                () -> expression.eval("x = 1; x = 2")
        );
    }

    @Test
    void rejectsBlankVariableNames() {
        assertThrows(IllegalArgumentException.class, () -> new Variable(null));
        assertThrows(IllegalArgumentException.class, () -> new Variable(""));
        assertThrows(IllegalArgumentException.class, () -> new Variable("   "));
    }

    @Test
    void divisionTruncatesTowardZero() {
        Expression positive = new Div(new Number(7), new Number(2));
        Expression negative = new Div(new Number(-7), new Number(2));

        assertEquals(3, positive.eval(""));
        assertEquals(-3, negative.eval(""));
    }

    @Test
    void printsExpressionWithLineSeparator() {
        Expression expression = new Add(new Number(3), new Variable("x"));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try (PrintStream output =
                     new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            expression.print();
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals(
                "(3+x)" + System.lineSeparator(),
                buffer.toString(StandardCharsets.UTF_8)
        );
    }
}

