package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent13Test {
    private final Advent13 underTest = new Advent13();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent13_ex1.in");
        assertEquals(405, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent13.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent13_ex1.in");
        assertEquals(400, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent13.in");
        System.out.println(run);
    }
}