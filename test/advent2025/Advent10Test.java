package advent2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent10Test {
    private final Advent10 underTest = new Advent10();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2025/advent10_ex1.in");
        assertEquals(7, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2025/advent10.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2025/advent10_ex1.in");
        assertEquals(24, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2025/advent10.in");
        System.out.println(run);
    }
}