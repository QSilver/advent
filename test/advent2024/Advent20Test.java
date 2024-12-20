package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent20Test {
    private final Advent20 underTest = new Advent20();

    @Test
    void testPart1() {
        assertEquals(0, underTest.runP1("advent2024/advent20_ex1.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent20.in"));
    }

    @Test
    void testPart2() {
        assertEquals(0, underTest.runP2("advent2024/advent20_ex1.in"));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent20.in"));
    }
}