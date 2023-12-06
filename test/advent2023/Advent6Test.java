package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent6Test {
    private final Advent6 underTest = new Advent6();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent6_ex1.in");
        assertEquals(288, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent6.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent6_ex1.in");
        assertEquals(71503, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent6.in");
        System.out.println(run);
    }
}