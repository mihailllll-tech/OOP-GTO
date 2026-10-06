package ru.nsu.kurumun.expressions.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.nsu.kurumun.expressions.model.Expression;

class ExpressionParserTest {

    @Test
    void parsesNegativeNumberWithSpaces() {
        Expression expression = ExpressionParser.parse("  -12  ");

        assertEquals(-12, expression.eval(""));
        assertEquals("-12", expression.toString());
    }

    @Test
    void parsesFullVariableName() {
        Expression expression = ExpressionParser.parse(" speed2 ");

        assertEquals(7, expression.eval("speed2 = 7"));
        assertEquals("speed2", expression.toString());
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("12abc")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("   ")
        );
    }
    @Test
    void parsesNestedExpressionFromAssignment() {
        Expression expression = ExpressionParser.parse("(3+(2*x))");

        assertEquals(23, expression.eval("x = 10; y = 13"));
        assertEquals("(3+(2*x))", expression.toString());
        assertEquals("(0+((0*x)+(2*1)))", expression.derivative("x").toString());
    }

    @Test
    void parsesSubtractionAndDivisionWithNegativeNumber() {
        Expression expression = ExpressionParser.parse(" ( (-12 / 3) - offset ) ");

        assertEquals(-6, expression.eval("offset = 2"));
        assertEquals("((-12/3)-offset)", expression.toString());
    }

    @Test
    void rejectsBrokenOperations() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("(3+)")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("(3+(2*x)")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("3+2")
        );
    }
    @Test
    void rejectsNullExpression() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse(null)
        );
    }

    @Test
    void parsesGroupedVariableWithUnderscores() {
        Expression expression = ExpressionParser.parse(" (( _speed_2 )) ");

        assertEquals("_speed_2", expression.toString());
        assertEquals(12, expression.eval("_speed_2 = 12"));
        assertEquals("1", expression.derivative("_speed_2").toString());
    }

    @Test
    void rejectsUnbalancedParentheses() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("((x)")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("(x))")
        );
    }

    @Test
    void rejectsUnsupportedTokens() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("speed!")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ExpressionParser.parse("(x^2)")
        );
    }
}