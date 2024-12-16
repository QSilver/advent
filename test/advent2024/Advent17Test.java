package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent17Test {
    private final Advent17 underTest = new Advent17();

    @Test
    void testPart1() {
        assertEquals(0, underTest.runP1("advent2024/advent17_ex1.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent17.in"));
    }

    @Test
    void testPart2() {
        assertEquals(0, underTest.runP2("advent2024/advent17_ex1.in"));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent17.in"));
    }
}