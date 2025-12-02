package advent2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent2Test {
    private final Advent2 underTest = new Advent2();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2025/advent2_ex1.in");
        assertEquals(1227775554, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2025/advent2.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2025/advent2_ex1.in");
        assertEquals(4174379265L, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2025/advent2.in");
        System.out.println(run);
    }
}