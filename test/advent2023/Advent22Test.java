package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent22Test {
    private final Advent22 underTest = new Advent22();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent22_ex1.in", false);
        assertEquals(5, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent22.in", true);
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent22_ex1.in", false);
        assertEquals(7, run);
    }

    @Test
    void testPart2_custom() {
        assertEquals(3, underTest.runP2("advent2023/advent22_ex2.in", false));
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent22.in", true);
        System.out.println(run);
    }
}