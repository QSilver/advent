package advent2024;

import org.junit.jupiter.api.Test;

import static java.math.BigInteger.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent14Test {
    private final Advent14 underTest = new Advent14();

    @Test
    void testPart1() {
        assertEquals(12, underTest.runP1("advent2024/advent14_ex1.in", valueOf(11), valueOf(7)));
    }

    @Test
    void runPart1() {
        System.out.println(underTest.runP1("advent2024/advent14.in", valueOf(101), valueOf(103)));
    }

    @Test
    void testPart2() {
        assertEquals(1, underTest.runP2("advent2024/advent14_ex1.in", valueOf(11), valueOf(7)));
    }

    @Test
    void runPart2() {
        System.out.println(underTest.runP2("advent2024/advent14.in", valueOf(101), valueOf(103)));
    }
}