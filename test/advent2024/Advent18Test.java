package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent18Test {
    private final Advent18 underTest = new Advent18();

    @Test
    void testPart1() {
        assertEquals(22, underTest.runP1("advent2024/advent18_ex1.in", 6, 12));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent18.in", 70, 1024));
    }

    @Test
    void testPart2() {
        assertEquals("6,1", underTest.runP2("advent2024/advent18_ex1.in", 6));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent18.in", 70));
    }
}