package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static advent2022.Advent23.Direction.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;

// 945 too low - part 2
@Slf4j
public class Advent23 {
    static Map<Point, Elf> elves = newHashMap();

    public static void main(String[] args) {
        List<String> mapLines = Util.fileStream("advent2022/advent23").collect(Collectors.toList());

        for (int down = 0; down < mapLines.size(); down++) {
            for (int across = 0; across < mapLines.get(down).length(); across++) {
                if (mapLines.get(down).charAt(across) == '#') {
                    Point position = new Point(down, across);
                    elves.put(position, new Elf(position));
                }
            }
        }

        int round = 0;
        while (true) {
            countPointsInBound(round++);

            elves.values().forEach(Elf::proposeMove);
            // collect all proposed elf moves and remove overlapping
            Set<Point> remainingMoves = elves.values().stream()
                    .map(elf -> elf.proposed)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.groupingBy(identity(), counting())).entrySet().stream()
                    .filter(entry -> entry.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            if (remainingMoves.size() == 0) {
                break;
            }

            // identify which elf proposed each move
            Map<Point, Elf> whoProposedMove = elves.values().stream()
                    .filter(elf -> remainingMoves.contains(elf.proposed))
                    .collect(Collectors.toMap(elf -> elf.proposed, identity()));

            // execute move
            remainingMoves.forEach(point -> whoProposedMove.get(point).move());
        }

        log.info("Last Round: {}", round);
    }

    static void countPointsInBound(int round) {
        if (round == 10) {
            int minDown = elves.keySet().stream().mapToInt(point -> point.down).min().getAsInt();
            int maxDown = elves.keySet().stream().mapToInt(point -> point.down).max().getAsInt();
            int minAcross = elves.keySet().stream().mapToInt(point -> point.across).min().getAsInt();
            int maxAcross = elves.keySet().stream().mapToInt(point -> point.across).max().getAsInt();
            log.info("Empty tiles: {}", (maxDown - minDown + 1) * (maxAcross - minAcross + 1) - elves.size());
        }
    }

    static class Elf {
        Point position;
        Point proposed;
        List<Direction> proposedDirections = newArrayList(NORTH, SOUTH, WEST, EAST);

        public Elf(Point position) {
            this.position = position;
        }

        void move() {
            elves.remove(position);
            Point key = new Point(proposed.down, proposed.across);
            elves.put(key, this);
            position = key;
        }

        void proposeMove() {
            proposed = null;
            if (position.neighbours().stream().noneMatch(point -> elves.containsKey(point))) {
                cycleDirection();
                return;
            }

            Direction selected = null;
            for (Direction direction : proposedDirections) {
                if (position.getPointsInDirection(direction).stream().noneMatch(point -> elves.containsKey(point))) {
                    selected = direction;
                    break;
                }
            }

            if (selected != null) {
                proposed = position.getPointsInDirection(selected).get(1);
            }

            cycleDirection();
        }

        private void cycleDirection() {
            Direction first = proposedDirections.get(0);
            proposedDirections.remove(first);
            proposedDirections.add(first);
        }
    }

    record Point(int down, int across) {
        List<Point> getPointsInDirection(Direction direction) {
            List<Point> points = null;
            switch (direction) {
                case NORTH -> points = northPoints();
                case SOUTH -> points = southPoints();
                case WEST -> points = westPoints();
                case EAST -> points = eastPoints();
                default -> log.error("Unknown Direction");
            }
            return points;
        }

        List<Point> northPoints() {
            return newArrayList(
                    new Point(down - 1, across - 1),
                    new Point(down - 1, across),
                    new Point(down - 1, across + 1));
        }

        List<Point> southPoints() {
            return newArrayList(
                    new Point(down + 1, across - 1),
                    new Point(down + 1, across),
                    new Point(down + 1, across + 1));
        }

        List<Point> westPoints() {
            return newArrayList(
                    new Point(down - 1, across - 1),
                    new Point(down, across - 1),
                    new Point(down + 1, across - 1));
        }

        List<Point> eastPoints() {
            return newArrayList(
                    new Point(down - 1, across + 1),
                    new Point(down, across + 1),
                    new Point(down + 1, across + 1));
        }

        List<Point> neighbours() {
            List<Point> neighbours = newArrayList();
            for (int d = -1; d <= 1; d++) {
                for (int a = -1; a <= 1; a++) {
                    if (d == 0 && a == 0) {
                        continue;
                    }
                    neighbours.add(new Point(this.down + d, this.across + a));
                }
            }
            return neighbours;
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

    enum Direction {
        NORTH, SOUTH, WEST, EAST
    }
}