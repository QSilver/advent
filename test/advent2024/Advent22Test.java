package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent22Test {
    private final Advent22 underTest = new Advent22();

    @Test
    void testPart1() {
        assertEquals(37327623, underTest.runP1("advent2024/advent22_ex1.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent22.in"));
    }

    @Test
    void testPart2() {
        assertEquals(23, underTest.runP2("advent2024/advent22_ex2.in"));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent22.in"));
        // 2222 - too high
    }
}