package advent2020;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.InputUtils;

import java.util.stream.Collectors;

@Slf4j
public class Advent12 {
    static int sx = 0;
    static int sy = 0;
    static int wx = 10;
    static int wy = 1;

    public static void main(String[] args) {
        InputUtils.fileStream("advent2020/advent12")
            .collect(Collectors.toList())
            .forEach(line -> {
                int value = Integer.parseInt(line.substring(1));
                switch (line.charAt(0)) {
                    case 'N' -> wy += value;
                    case 'S' -> wy -= value;
                    case 'E' -> wx += value;
                    case 'W' -> wx -= value;
                    case 'L' -> {
                        Pair<Integer, Integer> rotate = rotate(wx, wy, value);
                        wx = rotate.getFirst();
                        wy = rotate.getSecond();
                    }
                    case 'R' -> {
                        Pair<Integer, Integer> rotate = rotate(wx, wy, 360 - value);
                        wx = rotate.getFirst();
                        wy = rotate.getSecond();
                    }
                    case 'F' -> {
                        sx += wx * value;
                        sy += wy * value;
                    }
                }
            });
        log.info("X: {}, Y: {}, Dist: {}", sx, sy, Math.abs(sx) + Math.abs(sy));
    }

    private static Pair<Integer, Integer> rotate(int x, int y, int deg) {
        if (deg == 180) {
            return new Pair<>(-x, -y);
        }
        if (deg == 90) {
            return new Pair<>(-y, x);
        }
        if (deg == 270) {
            return new Pair<>(y, -x);
        }
        return new Pair<>(x, y);
    }
}
