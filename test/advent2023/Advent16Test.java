package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent16Test {
    private final Advent16 underTest = new Advent16();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent16_ex1.in");
        assertEquals(46, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent16.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent16_ex1.in");
        assertEquals(51, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent16.in");
        System.out.println(run);
    }
}