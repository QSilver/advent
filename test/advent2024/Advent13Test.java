package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent13Test {
    private final Advent13 underTest = new Advent13();

    @Test
    void testPart1() {
        assertEquals(480, underTest.runP1("advent2024/advent13_ex1.in"));
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2024/advent13.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        assertEquals(480, underTest.runP2("advent2024/advent13_ex1.in"));
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2024/advent13.in");
        System.out.println(run);
    }
}