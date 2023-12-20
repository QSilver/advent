package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent20Test {
    private final Advent20 underTest = new Advent20();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent20_ex1.in");
        assertEquals(32000000, run);
    }

    @Test
    void testPart1_2() {
        Long run = underTest.runP1("advent2023/advent20_ex2.in");
        assertEquals(11687500, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent20.in");
        System.out.println(run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent20.in");
        System.out.println(run);
    }
}