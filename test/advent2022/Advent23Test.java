package advent2022;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent23Test {
    Advent23.Point p1 = new Advent23.Point(1, 0);
    Advent23.Point p2 = new Advent23.Point(0, 31);

    @Test
    void testEquality() {
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}