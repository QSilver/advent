package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent14Test {
    private final Advent14 underTest = new Advent14();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent14_ex1.in");
        assertEquals(136, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent14.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent14_ex1.in");
        assertEquals(64, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent14.in");
        System.out.println(run);
    }
}