package util;

public enum Direction {
    UP, RIGHT, DOWN, LEFT;

    public Direction clockwise() {
        Direction[] values = Direction.values();
        int next = (this.ordinal() + 1) % values.length;
        return values[next];
    }

    public Direction counterclockwise() {
        Direction[] values = Direction.values();
        int next = (this.ordinal() - 1) % values.length;
        return values[next >= 0 ? next : next + values.length];
    }

    public char toChar() {
        switch (this) {
            case UP -> {
                return '^';
            }
            case RIGHT -> {
                return '>';
            }
            case DOWN -> {
                return 'v';
            }
            case LEFT -> {
                return '<';
            }
        }
        throw new RuntimeException("Impossible Direction");
    }

    public Direction fromChar(char c) {
        switch (c) {
            case '<' -> {
                return LEFT;
            }
            case 'v' -> {
                return DOWN;
            }
            case '>' -> {
                return RIGHT;
            }
            case '^' -> {
                return UP;
            }
            default -> throw new RuntimeException("Unknown Character");
        }
    }
}
