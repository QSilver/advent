package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent25Test {
    private final Advent25 underTest = new Advent25();

    @Test
    void testPart1() {
        assertEquals(3, underTest.runP1("advent2024/advent25_ex1.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent25.in"));
    }
}