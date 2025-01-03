package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent3Test {
    private final Advent3 underTest = new Advent3();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2024/advent3_ex1.in");
        assertEquals(161, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2024/advent3.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2024/advent3_ex2.in");
        assertEquals(48, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2024/advent3.in");
        System.out.println(run);
    }
}