package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent19Test {
    private final Advent19 underTest = new Advent19();

    @Test
    void testPart1() {
        assertEquals(6, underTest.runP1("advent2024/advent19_ex1.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent19.in"));
    }

    @Test
    void testPart2() {
        assertEquals(16, underTest.runP2("advent2024/advent19_ex1.in"));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent19.in"));
    }
}