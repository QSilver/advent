package advent2019;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent11Test {
    @Test
    void testLeftToUp() {
        Advent11.facing = Facing.LEFT;
        assertEquals(Facing.UP, Advent11.getFacing(1));
    }

    @Test
    void testUpToRight() {
        Advent11.facing = Facing.UP;
        assertEquals(Facing.RIGHT, Advent11.getFacing(1));
    }

    @Test
    void testRightToDown() {
        Advent11.facing = Facing.RIGHT;
        assertEquals(Facing.DOWN, Advent11.getFacing(1));
    }

    @Test
    void testDownToLeft() {
        Advent11.facing = Facing.DOWN;
        assertEquals(Facing.LEFT, Advent11.getFacing(1));
    }

    @Test
    void testLeftToDown() {
        Advent11.facing = Facing.LEFT;
        assertEquals(Facing.DOWN, Advent11.getFacing(0));
    }

    @Test
    void testDownToRight() {
        Advent11.facing = Facing.DOWN;
        assertEquals(Facing.RIGHT, Advent11.getFacing(0));
    }

    @Test
    void testRightToUp() {
        Advent11.facing = Facing.RIGHT;
        assertEquals(Facing.UP, Advent11.getFacing(0));
    }

    @Test
    void testUpToLeft() {
        Advent11.facing = Facing.UP;
        assertEquals(Facing.LEFT, Advent11.getFacing(0));
    }
}