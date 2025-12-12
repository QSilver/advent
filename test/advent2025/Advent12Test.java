package advent2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent12Test {
    private final Advent12 underTest = new Advent12();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2025/advent12_ex1.in");
        assertEquals(2, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2025/advent12.in");
        System.out.println(run);
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2025/advent12_ex1.in");
        assertEquals(2, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2025/advent12.in");
        System.out.println(run);
    }
}