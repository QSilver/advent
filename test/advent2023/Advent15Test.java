package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent15Test {
    private final Advent15 underTest = new Advent15();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent15_ex1.in");
        assertEquals(1320, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent15.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent15_ex1.in");
        assertEquals(145, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent15.in");
        System.out.println(run);
    }
}