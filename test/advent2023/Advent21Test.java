package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent21Test {
    private final Advent21 underTest = new Advent21();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent20_ex1.in");
        assertEquals(0L, run);
    }

    @Test
    void testPart1_2() {
        Long run = underTest.runP1("advent2023/advent20_ex2.in");
        assertEquals(0L, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent20.in");
        System.out.println(run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent20.in");
        System.out.println(run);
    }
}