package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent9Test {
    private final Advent9 underTest = new Advent9();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2024/advent9_ex1.in");
        assertEquals(1928, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2024/advent9.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2024/advent9_ex1.in");
        assertEquals(2858, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2024/advent9.in");
        System.out.println(run);
    }
}