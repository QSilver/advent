package advent2015;

import lombok.With;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent3 {
    private static final int ACTORS = 2;
    private static final Point STARTING = new Point(0, 0);

    public static void main(String[] args) {
        String moves = InputUtils.fileStream("advent2015/advent3")
                .toList()
                .get(0);

        Set<Point> houses = newHashSet();
        List<Point> currentPoints = newArrayList();
        IntStream.range(0, ACTORS)
                .forEach(value -> currentPoints.add(STARTING));
        houses.add(STARTING);

        int step = 0;
        for (char move : moves.toCharArray()) {
            Point current = currentPoints.get(step % ACTORS);
            switch (move) {
                case '^' -> replace(currentPoints, step, current.up());
                case 'v' -> replace(currentPoints, step, current.down());
                case '<' -> replace(currentPoints, step, current.left());
                case '>' -> replace(currentPoints, step, current.right());
            }
            houses.add(currentPoints.get(step % ACTORS));
            step++;
        }

        log.info("Houses: {}", houses.size());
    }

    private static void replace(List<Point> currentPoints, int step, Point up) {
        currentPoints.add(step % ACTORS, up);
        currentPoints.remove(step % ACTORS + 1);
    }
}

@With
record Point(int x, int y) {
    Point up() {
        return this.withY(y + 1);
    }

    Point down() {
        return this.withY(y - 1);
    }

    Point left() {
        return this.withX(x - 1);
    }

    Point right() {
        return this.withX(x + 1);
    }
}