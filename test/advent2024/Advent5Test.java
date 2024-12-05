package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent5Test {
    private final Advent5 underTest = new Advent5();

    @Test
    void testPart1() {
        Integer run = underTest.runP1("advent2024/advent5_ex1.in");
        assertEquals(143, run);
    }

    @Test
    void runPart1() {
        Integer run = underTest.runP1("advent2024/advent5.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Integer run = underTest.runP2("advent2024/advent5_ex1.in");
        assertEquals(123, run);
    }

    @Test
    void runPart2() {
        Integer run = underTest.runP2("advent2024/advent5.in");
        System.out.println(run);
    }
}