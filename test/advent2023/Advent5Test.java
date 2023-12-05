package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent5Test {
    private final Advent5 underTest = new Advent5();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent5_ex1.in");
        assertEquals(35, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent5.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent5_ex1.in");
        assertEquals(46, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent5.in");
        System.out.println(run);
    }
}