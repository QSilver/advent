package advent2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent4Test {
    private final Advent4 underTest = new Advent4();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2025/advent4_ex1.in");
        assertEquals(13, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2025/advent4.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2025/advent4_ex1.in");
        assertEquals(43, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2025/advent4.in");
        System.out.println(run);
    }
}