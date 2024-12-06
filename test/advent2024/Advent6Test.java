package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent6Test {
    private final Advent6 underTest = new Advent6();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2024/advent6_ex1.in");
        assertEquals(41, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2024/advent6.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2024/advent6_ex1.in");
        assertEquals(6, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2024/advent6.in");
        System.out.println(run);
    }
}