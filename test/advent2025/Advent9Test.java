package advent2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent9Test {
    private final Advent9 underTest = new Advent9();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2025/advent9_ex1.in");
        assertEquals(40, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2025/advent9.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2025/advent9_ex1.in");
        assertEquals(25272, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2025/advent9.in");
        System.out.println(run);
    }
}