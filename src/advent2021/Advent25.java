package advent2021;

import advent2021.Advent5.Point;
import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent25 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<String> input = InputUtils.fileStream("advent2021/advent25")
                                 .toList();

        int sizeX = input.size();
        int sizeY = input.get(0)
                         .length();
        Map<Point, Facing> cucumbers = newHashMap();
        for (int i = 0; i < input.size(); i++) {
            String[] split = input.get(i)
                                  .split("");
            for (int j = 0; j < split.length; j++) {
                if (split[j].charAt(0) == '>') {
                    cucumbers.put(new Point(i, j), Facing.EAST);
                } else if (split[j].charAt(0) == 'v') {
                    cucumbers.put(new Point(i, j), Facing.SOUTH);
                }
            }
        }

        int steps = 0;
        AtomicBoolean hasStopped = new AtomicBoolean(true);
        do {
            steps++;
            log.info("Step {}", steps);
            hasStopped.set(true);
            display(cucumbers, sizeX, sizeY);
            Map<Point, Facing> newCucumbersEast = newHashMap();
            for (Point current : cucumbers.keySet()) {
                Facing facing = cucumbers.get(current);
                if (facing.equals(Facing.EAST)) {
                    Point newPos = new Point(current.X, (current.Y + 1) % sizeY);
                    if (!cucumbers.containsKey(newPos)) {
                        newCucumbersEast.put(newPos, facing);
                        hasStopped.set(false);
                    } else {
                        newCucumbersEast.put(current, facing);
                    }
                } else {
                    newCucumbersEast.put(current, facing);
                }
            }
            cucumbers = newCucumbersEast;
            Map<Point, Facing> newCucumbersSouth = newHashMap();
            for (Point current : cucumbers.keySet()) {
                Point newPos;
                Facing facing = cucumbers.get(current);
                if (facing.equals(Facing.SOUTH)) {
                    newPos = new Point((current.X + 1) % sizeX, current.Y);
                    if (!cucumbers.containsKey(newPos)) {
                        newCucumbersSouth.put(newPos, facing);
                        hasStopped.set(false);
                    } else {
                        newCucumbersSouth.put(current, facing);
                    }
                } else {
                    newCucumbersSouth.put(current, facing);
                }
            }
            cucumbers = newCucumbersSouth;
        } while (!hasStopped.get());
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    static void display(Map<Point, Facing> cucumbers, int sizeX, int sizeY) {
        StringBuilder display = new StringBuilder("\n");
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (cucumbers.containsKey(new Point(i, j))) {
                    display.append(cucumbers.get(new Point(i, j))
                                            .equals(Facing.EAST) ? ">" : "v");
                } else {
                    display.append(".");
                }
            }
            display.append("\n");
        }
        log.info("{}", display);
    }

    @AllArgsConstructor
    static class Cucumber {
        Point position;
        Facing facing;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Cucumber cucumber = (Cucumber) o;
            return Objects.equal(position, cucumber.position);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(position);
        }
    }

    enum Facing {
        EAST,
        SOUTH
    }
}
