package advent2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent7Test {
    private final Advent7 underTest = new Advent7();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2025/advent7_ex1.in");
        assertEquals(21, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2025/advent7.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2025/advent7_ex1.in");
        assertEquals(40, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2025/advent7.in");
        System.out.println(run);
    }
}