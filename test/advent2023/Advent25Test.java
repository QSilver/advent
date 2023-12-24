package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent25Test {
    private final Advent25 underTest = new Advent25();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent25_ex1.in");
        assertEquals(2, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent25.in");
        System.out.println(run);
    }
}