package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent8Test {
    private final Advent8 underTest = new Advent8();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent8_ex1.in");
        assertEquals(6440, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent8.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent8_ex1.in");
        assertEquals(5905, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent8.in");
        System.out.println(run);
    }
}