package advent2022;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.max;
import static java.lang.Math.min;

// 24588 - too low
@Slf4j
public class Advent14 {
    static Map<Point, String> caveMap = newHashMap();
    static int minDown, maxDown, minAcross, maxAcross;

    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2022/advent14").collect(Collectors.toList());

        input.forEach(line -> {
            String[] split = line.split(" -> ");
            for (int i = 1; i < split.length; i++) {
                drawRocks(new Point(split[i - 1]), new Point(split[i]));
            }
        });

        minDown = 0;//caveMap.keySet().stream().min(Comparator.comparingInt(o -> o.down)).get().down;
        maxDown = caveMap.keySet().stream().max(Comparator.comparingInt(o -> o.down)).get().down;
        minAcross = caveMap.keySet().stream().min(Comparator.comparingInt(o -> o.across)).get().across;
        maxAcross = caveMap.keySet().stream().max(Comparator.comparingInt(o -> o.across)).get().across;

        drawRocks(new Point(maxDown + 2, minAcross - 1000), new Point(maxDown + 2, maxAcross + 1000));
        maxDown += 2;

        while (true) {
            Point sandEnd = sandFall(new Point("500,0"));
            caveMap.put(sandEnd, "o");
            if (sandEnd.down > maxDown) {
                log.info("Sand off the map");
                caveMap.remove(sandEnd);
                break;
            }
            if (sandEnd.equals(new Point("500,0"))) {
                log.info("Sand reached the top");
                break;
            }
        }

        minAcross = caveMap.entrySet().stream().filter(entry -> entry.getValue().equals("o")).min(Comparator.comparingInt(o -> o.getKey().across)).get().getKey().across - 2;
        maxAcross = caveMap.entrySet().stream().filter(entry -> entry.getValue().equals("o")).max(Comparator.comparingInt(o -> o.getKey().across)).get().getKey().across + 2;
        render();

        long sand = caveMap.values().stream().filter("o"::equals).count();
        log.info("Units of Sand: {}", sand);
    }

    static void drawRocks(Point start, Point end) {
        for (int i = min(start.down, end.down); i <= max(start.down, end.down); i++) {
            for (int j = min(start.across, end.across); j <= max(start.across, end.across); j++) {
                caveMap.put(new Point(i, j), "#");
            }
        }
    }

    static Point sandFall(Point sand) {
        while (true) {
            if (caveMap.containsKey(new Point(sand.down + 1, sand.across))) {
                if (caveMap.containsKey(new Point(sand.down + 1, sand.across - 1))) {
                    if (caveMap.containsKey(new Point(sand.down + 1, sand.across + 1))) {
                        break;
                    } else {
                        sand.down += 1;
                        sand.across += 1;
                    }
                } else {
                    sand.down += 1;
                    sand.across -= 1;
                }
            } else {
                sand.down += 1;
            }

            if (sand.down > maxDown) {
                break;
            }
        }
        return sand;
    }

    private static void render() {
        StringBuffer draw = new StringBuffer();
        draw.append("\n");
        for (int i = minDown; i <= maxDown; i++) {
            for (int j = minAcross; j <= maxAcross; j++) {
                if ("#".equals(caveMap.get(new Point(i, j)))) {
                    draw.append("█");
                } else if ("o".equals(caveMap.get(new Point(i, j)))) {
                    draw.append("o");
                } else {
                    draw.append(".");
                }
            }
            draw.append("\n");
        }
        log.info("{}", draw);
    }

    @AllArgsConstructor
    static class Point {
        int down;
        int across;

        public Point(String coords) {
            String[] split = coords.split(",");
            this.across = Integer.parseInt(split[0]);
            this.down = Integer.parseInt(split[1]);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return down == point.down && across == point.across;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(down, across);
        }

        @Override
        public String toString() {
            return "{" + down + "," + across + "}";
        }
    }
}
