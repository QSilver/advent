package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent23Test {
    private final Advent23 underTest = new Advent23();

    @Test
    void testPart1() {
        assertEquals(7, underTest.runP1("advent2024/advent23_ex1.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent23.in"));
    }

    @Test
    void testPart2() {
        assertEquals("co,de,ka,ta", underTest.runP2("advent2024/advent23_ex1.in"));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent23.in"));
    }
}