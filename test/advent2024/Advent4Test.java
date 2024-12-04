package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent4Test {
    private final Advent4 underTest = new Advent4();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2024/advent4_ex1.in");
        assertEquals(18, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2024/advent4.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2024/advent4_ex1.in");
        assertEquals(9, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2024/advent4.in");
        System.out.println(run);
    }
}