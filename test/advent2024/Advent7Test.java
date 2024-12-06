package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent7Test {
    private final Advent7 underTest = new Advent7();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2024/advent7_ex1.in");
        assertEquals(0, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2024/advent7.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2024/advent7_ex1.in");
        assertEquals(0, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2024/advent7.in");
        System.out.println(run);
    }
}