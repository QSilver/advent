package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent2Test {
    private final Advent2 underTest = new Advent2();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2024/advent2_ex1.in");
        assertEquals(2, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2024/advent2.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2024/advent2_ex1.in");
        assertEquals(4, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2024/advent2.in");
        System.out.println(run);
    }
}