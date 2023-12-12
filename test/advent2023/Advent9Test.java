package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent9Test {
    private final Advent9 underTest = new Advent9();

    @Test
    void testPart1() {
        int run = underTest.runP1("advent2023/advent9_ex1.in");
        assertEquals(114, run);
    }

    @Test
    void runPart1() {
        int run = underTest.runP1("advent2023/advent9.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        int run = underTest.runP2("advent2023/advent9_ex1.in");
        assertEquals(2, run);
    }

    @Test
    void runPart2() {
        int run = underTest.runP2("advent2023/advent9.in");
        System.out.println(run);
    }
}