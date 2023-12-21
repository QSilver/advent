package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent21Test {
    private final Advent21 underTest = new Advent21();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent21_ex1.in", 6);
        assertEquals(16, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent21.in", 64);
        System.out.println(run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent21.in");
        System.out.println(run);
    }
}