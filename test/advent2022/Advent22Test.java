package advent2022;

import org.junit.jupiter.api.Test;

import static advent2022.Advent22.Facing.*;
import static advent2022.Advent22.playerFacing;
import static advent2022.Advent22.rotate;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent22Test {
    @Test
    void testRotate() {
        playerFacing = UP;
        rotate('L');
        assertEquals(LEFT, playerFacing);

        playerFacing = UP;
        rotate('R');
        assertEquals(RIGHT, playerFacing);

        playerFacing = DOWN;
        rotate('L');
        assertEquals(RIGHT, playerFacing);

        playerFacing = DOWN;
        rotate('R');
        assertEquals(LEFT, playerFacing);

        playerFacing = RIGHT;
        rotate('L');
        assertEquals(UP, playerFacing);

        playerFacing = RIGHT;
        rotate('R');
        assertEquals(DOWN, playerFacing);

        playerFacing = LEFT;
        rotate('L');
        assertEquals(DOWN, playerFacing);

        playerFacing = LEFT;
        rotate('R');
        assertEquals(UP, playerFacing);
    }
}