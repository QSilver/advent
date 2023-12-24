package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent24Test {
    private final Advent24 underTest = new Advent24();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent24_ex1.in", 7, 27);
        assertEquals(2, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent24.in", 200000000000000L, 400000000000000L);
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent24_ex1.in");
        assertEquals(47, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent24.in");
        System.out.println(run);
    }
}