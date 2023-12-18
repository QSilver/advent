package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent18Test {
    private final Advent18 underTest = new Advent18();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent18_ex1.in");
        assertEquals(62, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent18.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent18_ex1.in");
        assertEquals(952408144115L, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent18.in");
        System.out.println(run);
    }
}