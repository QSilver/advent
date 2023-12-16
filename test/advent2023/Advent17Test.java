package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent17Test {
    private final Advent17 underTest = new Advent17();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent17_ex1.in");
        assertEquals(0, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent17.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent17_ex1.in");
        assertEquals(0, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent17.in");
        System.out.println(run);
    }
}