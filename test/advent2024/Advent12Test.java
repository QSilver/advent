package advent2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent12Test {
    private final Advent12 underTest = new Advent12();

    @Test
    void testPart1() {
        assertEquals(140, underTest.runP1("advent2024/advent12_ex1.in"));
        assertEquals(772, underTest.runP1("advent2024/advent12_ex2.in"));
        assertEquals(1930, underTest.runP1("advent2024/advent12_ex3.in"));
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2024/advent12.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        assertEquals(80, underTest.runP2("advent2024/advent12_ex1.in"));
        assertEquals(236, underTest.runP2("advent2024/advent12_ex4.in"));
        assertEquals(368, underTest.runP2("advent2024/advent12_ex5.in"));
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2024/advent12.in");
        System.out.println(run);
    }
}