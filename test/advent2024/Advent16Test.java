package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent16Test {
    private final Advent16 underTest = new Advent16();

    @Test
    void testPart1() {
        assertEquals(7036, underTest.runP1("advent2024/advent16_ex1.in"));
        assertEquals(11048, underTest.runP1("advent2024/advent16_ex2.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent16.in"));
    }

    @Test
    void testPart2() {
        assertEquals(45, underTest.runP2("advent2024/advent16_ex1.in"));
        assertEquals(64, underTest.runP2("advent2024/advent16_ex2.in"));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent16.in"));
    }
}