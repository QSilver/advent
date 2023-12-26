package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent23Test {
    private final Advent23 underTest = new Advent23();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent23_ex1.in");
        assertEquals(94, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent23.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent23_ex1.in");
        assertEquals(154, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent23.in");
        System.out.println(run);
    }
}