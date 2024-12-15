package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent15Test {
    private final Advent15 underTest = new Advent15();

    @Test
    void testPart1() {
        assertEquals(10092, underTest.runP1("advent2024/advent15_ex1.in"));
        assertEquals(2028, underTest.runP1("advent2024/advent15_ex2.in"));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent15.in"));
    }

    @Test
    void testPart2() {
        assertEquals(618, underTest.runP2("advent2024/advent15_ex3.in", true));
        assertEquals(1751, underTest.runP2("advent2024/advent15_ex2.in", true));
        assertEquals(9021, underTest.runP2("advent2024/advent15_ex1.in", true));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent15.in", false));
    }
}