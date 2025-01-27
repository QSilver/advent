package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent24Test {
    private final Advent24 underTest = new Advent24();

    @Test
    void testPart1() {
        assertEquals(4, underTest.runP1("advent2024/advent24_ex1.in"));
        assertEquals(2024, underTest.runP1("advent2024/advent24_ex2.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent24.in"));
    }

    @Test
    void testPart2() {
        assertEquals("z00,z01,z02,z05", underTest.runP2("advent2024/advent24_ex3.in"));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent24.in"));
    }
}