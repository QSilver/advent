package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent19Test {
    private final Advent19 underTest = new Advent19();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent19_ex1.in");
        assertEquals(62, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent19.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent19_ex1.in");
        assertEquals(952408144115L, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent19.in");
        System.out.println(run);
    }
}