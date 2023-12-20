package advent2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent20Test {

    @Test
    void testPart1() {
        Long run = new Advent20().runP1("advent2023/advent20_ex1.in");
        assertEquals(32000000, run);
    }

    @Test
    void testPart1_2() {
        Long run = new Advent20().runP1("advent2023/advent20_ex2.in");
        assertEquals(11687500, run);
    }

    @Test
    void runPart1() {
        Long run = new Advent20().runP1("advent2023/advent20.in");
        System.out.println(run);
        assertEquals(731517480, run);
    }

    @Test
    void runPart2() {
        Long run = new Advent20().runP2("advent2023/advent20.in");
        System.out.println(run);
    }
}