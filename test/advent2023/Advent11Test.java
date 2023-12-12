package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent11Test {
    private final Advent11 underTest = new Advent11();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent11_ex1.in");
        assertEquals(374, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent11.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent11_ex1.in");
        assertEquals(82000210, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent11.in");
        System.out.println(run);
    }
}