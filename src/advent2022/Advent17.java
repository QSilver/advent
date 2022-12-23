package advent2022;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

// 1572093021178 - too low
// 1572093023882 - too high
@Slf4j
public class Advent17 {
    static Set<Point> occupied = newHashSet();
    static boolean[][] map = new boolean[50000][7];
    static Map<Pair<String, Integer>, Pair<Integer, Integer>> memoization = newHashMap();
    static Map<Pair<String, Integer>, String> state = newHashMap();

    public static void main(String[] args) {
        List<Character> directions = Util.fileStream("advent2022/advent17")
                .collect(Collectors.toList()).get(0)
                .chars().mapToObj(e -> (char) e)
                .collect(Collectors.toList());

        Pair<Pair<String, Integer>, Pair<Integer, Integer>> period = getPeriod(directions, 5600);

        Integer periodTurns = period.getSecond().getFirst();
        Integer periodHeight = period.getSecond().getSecond();
        Integer initialTurns = memoization.get(period.getFirst()).getFirst();
        Integer initialHeight = memoization.get(period.getFirst()).getSecond();

        long cycles = (1000000000000L - initialTurns) / periodTurns + 1;
        log.info("Max Height: {}", cycles * periodHeight + initialHeight - 1);
    }

    private static Pair<Pair<String, Integer>, Pair<Integer, Integer>> getPeriod(List<Character> directions, int turns) {
        int maxHeight;
        int windIndex = 0;
        for (int turn = 1; turn <= turns; turn++) {
            maxHeight = occupied.size() > 0 ? occupied.stream().mapToInt(point -> point.down).max().getAsInt() : 0;
            Shape newRock = nextFallingRock(turn - 1, maxHeight + 1);

            Pair<String, Integer> rockWindPair = new Pair<>(newRock.getClass().getSimpleName(), windIndex % directions.size());
            while (!newRock.settled) {
                char wind = directions.get(windIndex++ % directions.size());
                newRock.pushedByWind(wind);
                newRock.fall();
            }
            occupied.addAll(newRock.points);
            occupied.stream()
                    .sorted(Comparator.comparingInt(o -> o.down))
                    .forEach(point -> map[point.down][point.across] = true);

            if (memoization.containsKey(rockWindPair)) {
                if (toByte(maxHeight).equals(state.get(rockWindPair))) {
                    log.info("Found loop: {} - First {} @ {} - Second {} @ {}", rockWindPair, memoization.get(rockWindPair).getFirst(), memoization.get(rockWindPair).getSecond(), turn, maxHeight);
                    Pair<Integer, Integer> period = new Pair<>(turn - memoization.get(rockWindPair).getFirst(), maxHeight - memoization.get(rockWindPair).getSecond());
                    return new Pair<>(rockWindPair, period);
                }
            }
            memoization.put(rockWindPair, new Pair<>(turn, maxHeight));
            state.put(rockWindPair, toByte(maxHeight));
        }
        return null;
    }

    static String toByte(int current) {
        StringBuilder sb = new StringBuilder();
        for (int iter = 0; iter < 10; iter++) {
            for (boolean b : map[current]) {
                sb.append(b ? 0 : 1);
            }
        }
        return sb.toString();
    }

    static Shape nextFallingRock(int turn, int maxHeight) {
        switch (turn % 5) {
            case 0 -> {
                return new H_Line(maxHeight);
            }
            case 1 -> {
                return new Cross(maxHeight);
            }
            case 2 -> {
                return new L_Shape(maxHeight);
            }
            case 3 -> {
                return new V_Line(maxHeight);
            }
            case 4 -> {
                return new Square(maxHeight);
            }
        }
        return null;
    }

    static class H_Line extends Shape {
        //..####
        public H_Line(int top) {
            this.points.add(new Point(top + 3, 2));
            this.points.add(new Point(top + 3, 3));
            this.points.add(new Point(top + 3, 4));
            this.points.add(new Point(top + 3, 5));
        }
    }

    static class Cross extends Shape {
        //...#.
        //..###
        //...#.
        public Cross(int top) {
            this.points.add(new Point(top + 3, 3));
            this.points.add(new Point(top + 4, 2));
            this.points.add(new Point(top + 4, 3));
            this.points.add(new Point(top + 4, 4));
            this.points.add(new Point(top + 5, 3));
        }
    }

    static class L_Shape extends Shape {
        //....#
        //....#
        //..###
        public L_Shape(int top) {
            this.points.add(new Point(top + 3, 2));
            this.points.add(new Point(top + 3, 3));
            this.points.add(new Point(top + 3, 4));
            this.points.add(new Point(top + 4, 4));
            this.points.add(new Point(top + 5, 4));
        }
    }

    static class V_Line extends Shape {
        //..#
        //..#
        //..#
        //..#
        public V_Line(int top) {
            this.points.add(new Point(top + 3, 2));
            this.points.add(new Point(top + 4, 2));
            this.points.add(new Point(top + 5, 2));
            this.points.add(new Point(top + 6, 2));
        }
    }

    static class Square extends Shape {
        //..##
        //..##
        public Square(int top) {
            this.points.add(new Point(top + 3, 2));
            this.points.add(new Point(top + 3, 3));
            this.points.add(new Point(top + 4, 2));
            this.points.add(new Point(top + 4, 3));
        }
    }

    static abstract class Shape {
        List<Point> points = newArrayList();
        boolean settled = false;

        void fall() {
            if (points.stream().anyMatch(point -> point.down == 1) || !canFall()) {
                settled = true;
                return;
            }
            points.forEach(point -> point.down -= 1);
        }

        private boolean canFall() {
            return points.stream().noneMatch(point -> occupied.contains(new Point(point.down - 1, point.across)));
        }

        void pushedByWind(char direction) {
            if (direction == '<' && canMoveLeft()) {
                points.forEach(point -> point.across -= 1);
            }
            if (direction == '>' && canMoveRight()) {
                points.forEach(point -> point.across += 1);
            }
        }

        private boolean canMoveLeft() {
            return points.stream().mapToInt(point -> point.across).min().getAsInt() > 0 &&
                    points.stream().noneMatch(point -> occupied.contains(new Point(point.down, point.across - 1)));
        }

        private boolean canMoveRight() {
            return points.stream().mapToInt(point -> point.across).max().getAsInt() < 6 &&
                    points.stream().noneMatch(point -> occupied.contains(new Point(point.down, point.across + 1)));
        }
    }

    @AllArgsConstructor
    static class Point {
        int down;
        int across;

        @Override
        public String toString() {
            return down + "," + across;
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
            return 100003 * down + across;
        }
    }
}
