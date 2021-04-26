package advent2020;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent24 {
    static Map<Hex, Boolean> tiles = newHashMap();
    static Map<Hex, Boolean> newTiles = newHashMap();

    private static final int DAYS = 100;
    private static final int MAX_DEPTH = 2;

    public static void main(String[] args) {
        List<String> input = Util.fileStream("advent2020/advent24")
                                 .collect(Collectors.toList());

        input.forEach(string -> {
            int row = 0;
            int col = 0;

            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (c == 'n') {
                    row--;
                    col += updateCol(string, i);
                    i++;
                } else if (c == 's') {
                    row++;
                    col += updateCol(string, i);
                    i++;
                } else if (c == 'w') {
                    col -= 2;
                } else if (c == 'e') {
                    col += 2;
                }
            }

            Hex hex = new Hex(col, row);
            tiles.put(hex, tiles.get(hex) == null || !tiles.get(hex));
        });

        logDay(0);
        for (int day = 1; day <= DAYS; day++) {
            newTiles.clear();
            tiles.keySet()
                 .forEach(hex -> processHex(hex, 0));
            tiles.clear();
            tiles.putAll(newTiles);
            logDay(day);
        }
    }

    private static void logDay(int day) {
        log.info("Day {}: {}", day, tiles.values()
                                         .stream()
                                         .filter(b -> b)
                                         .count());
    }

    private static void processHex(Hex h, int depth) {
        if (depth == MAX_DEPTH) {
            return;
        }

        int n = countNeighbours(h, depth);
        boolean flip = tiles.get(h) != null && tiles.get(h);
        if ((n == 0 || n > 2) && (tiles.get(h) != null && tiles.get(h))) {
            flip = false;
        }
        if (n == 2 && (tiles.get(h) == null || !tiles.get(h))) {
            flip = true;
        }
        newTiles.put(h, flip);
    }

    private static int countNeighbours(Hex h, int depth) {
        Boolean nw = tiles.get(new Hex(h.column - 1, h.row - 1));
        Boolean ne = tiles.get(new Hex(h.column + 1, h.row - 1));
        Boolean sw = tiles.get(new Hex(h.column - 1, h.row + 1));
        Boolean se = tiles.get(new Hex(h.column + 1, h.row + 1));
        Boolean w = tiles.get(new Hex(h.column - 2, h.row));
        Boolean e = tiles.get(new Hex(h.column + 2, h.row));

        if (nw == null) {
            processHex(new Hex(h.column - 1, h.row - 1), depth + 1);
        }
        if (ne == null) {
            processHex(new Hex(h.column + 1, h.row - 1), depth + 1);
        }
        if (sw == null) {
            processHex(new Hex(h.column - 1, h.row + 1), depth + 1);
        }
        if (se == null) {
            processHex(new Hex(h.column + 1, h.row + 1), depth + 1);
        }
        if (w == null) {
            processHex(new Hex(h.column - 2, h.row), depth + 1);
        }
        if (e == null) {
            processHex(new Hex(h.column + 2, h.row), depth + 1);
        }

        return (int) newArrayList(nw, ne, sw, se, w, e)
                .stream()
                .filter(java.util.Objects::nonNull)
                .filter(b -> b)
                .count();
    }

    private static int updateCol(String string, int i) {
        if (string.charAt(i + 1) == 'w') {
            return -1;
        } else {
            return 1;
        }
    }
}

class Hex {
    int column;
    int row;

    public Hex(int column, int row) {
        this.column = column;
        this.row = row;
    }

    @Override
    public String toString() {
        return "Hex{" +
                "c=" + column +
                ", r=" + row +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hex hex = (Hex) o;
        return column == hex.column && row == hex.row;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(column, row);
    }
}
