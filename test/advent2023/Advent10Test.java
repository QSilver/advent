package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent10Test {
    private final Advent10 underTest = new Advent10();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent10_ex1.in");
        assertEquals(4, run);
    }

    @Test
    void testPart1_2() {
        Long run = underTest.runP1("advent2023/advent10_ex2.in");
        assertEquals(8, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent10.in");
        System.out.println(run);
    }

    @Test
    void testPart2_1() {
        Long run = underTest.runP2("advent2023/advent10_ex1.in");
        assertEquals(1, run);
    }

    @Test
    void testPart2_3() {
        Long run = underTest.runP2("advent2023/advent10_ex3.in");
        assertEquals(4, run);
    }

    @Test
    void testPart2_4() {
        Long run = underTest.runP2("advent2023/advent10_ex4.in");
        assertEquals(4, run);
    }

    @Test
    void testPart2_5() {
        Long run = underTest.runP2("advent2023/advent10_ex5.in");
        assertEquals(8, run);
    }

    @Test
    void testPart2_6() {
        Long run = underTest.runP2("advent2023/advent10_ex6.in");
        assertEquals(10, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent10.in");
        System.out.println(run);
    }
}