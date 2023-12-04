package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent3Test {
    private final Advent3 underTest = new Advent3();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2023/advent3_ex1.in");
        assertEquals(4361, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2023/advent3.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2023/advent3_ex1.in");
        assertEquals(467835, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2023/advent3.in");
        System.out.println(run);
    }
}