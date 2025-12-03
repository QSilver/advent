package advent2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent3Test {
    private final Advent3 underTest = new Advent3();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2025/advent3_ex1.in");
        assertEquals(357, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2025/advent3.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2025/advent3_ex1.in");
        assertEquals(3121910778619L, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2025/advent3.in");
        System.out.println(run);
    }
}