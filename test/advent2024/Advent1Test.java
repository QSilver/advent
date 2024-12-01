package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent1Test {
    private final Advent1 underTest = new Advent1();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2024/advent1_ex1.in");
        assertEquals(11, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2024/advent1.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2024/advent1_ex1.in");
        assertEquals(31, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2024/advent1.in");
        System.out.println(run);
    }
}