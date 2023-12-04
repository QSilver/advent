package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent2Test {
    private final Advent2 underTest = new Advent2();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2023/advent2_ex1.in");
        assertEquals(8, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2023/advent2.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2023/advent2_ex1.in");
        assertEquals(2286, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2023/advent2.in");
        System.out.println(run);
    }
}