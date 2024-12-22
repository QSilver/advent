package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Point2D;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.comparingLong;
import static util.InputUtils.fileStream;
import static util.InputUtils.get2DPoints;

// 98323 - too high

@Slf4j
public class Advent14 {
    // https://adventofcode.com/2023/day/14
    public static final int CYCLES = 1000000000;

    List<Point2D> roundRockList = newArrayList();
    List<Point2D> squareRockList = newArrayList();

    Map<List<Point2D>, Long> cycleCache = newHashMap();
    Long period;

    // TODO - this needs a massive performance improvement by using [][]

    public Long runP1(String file) {
        List<String> list = getRocks(file);

        slideNorth();

        return roundRockList.stream()
                .mapToLong(value -> list.size() - value.row())
                .sum();
    }

    public Long runP2(String file) {
        List<String> list = getRocks(file);

        long cycleCount = 0;
        while (true) {
            cycle(list.size());
            cycleCount++;
            log.info("Cycle {} complete", cycleCount);

            List<Point2D> points = newArrayList(roundRockList);
            points.sort(comparingInt(o -> Math.toIntExact(o.row() * 100 + o.col())));

            if (cycleCache.containsKey(points)) {
                period = cycleCount - cycleCache.get(points);
                log.info("Period: {}", period);
                break;
            }

            cycleCache.put(points, cycleCount);
        }

        long start = cycleCache.size() - period;
        long remainder = (CYCLES - start) % period;

        List<Point2D> key = cycleCache.entrySet().stream()
                .filter(entry -> entry.getValue() == start + remainder)
                .findFirst().orElseThrow()
                .getKey();

        return key.stream()
                .mapToLong(value -> list.size() - value.row())
                .sum();
    }

    private void cycle(int listSize) {
        slideNorth();
        slideWest();
        slideSouth(listSize);
        slideEast(listSize);
    }

    private void slideNorth() {
        roundRockList.sort(comparingLong(Point2D::row));

        for (int rock = 0; rock < roundRockList.size(); rock++) {
            while (true) {
                Point2D current = roundRockList.get(rock);
                if (current.row() == 0) {
                    break;
                }

                Point2D newPos = new Point2D(current.row() - 1, current.col());

                if (!roundRockList.contains(newPos) && !squareRockList.contains(newPos)) {
                    roundRockList.remove(current);
                    roundRockList.add(rock, newPos);
                } else {
                    break;
                }
            }
        }
    }

    private void slideSouth(int listSize) {
        roundRockList.sort((o1, o2) -> Long.compare(o2.row(), o1.row()));

        for (int rock = 0; rock < roundRockList.size(); rock++) {
            while (true) {
                Point2D current = roundRockList.get(rock);
                if (current.row() == listSize - 1) {
                    break;
                }

                Point2D newPos = new Point2D(current.row() + 1, current.col());

                if (!roundRockList.contains(newPos) && !squareRockList.contains(newPos)) {
                    roundRockList.remove(current);
                    roundRockList.add(rock, newPos);
                } else {
                    break;
                }
            }
        }
    }

    private void slideWest() {
        roundRockList.sort(comparingLong(Point2D::col));

        for (int rock = 0; rock < roundRockList.size(); rock++) {
            while (true) {
                Point2D current = roundRockList.get(rock);
                if (current.col() == 0) {
                    break;
                }

                Point2D newPos = new Point2D(current.row(), current.col() - 1);

                if (!roundRockList.contains(newPos) && !squareRockList.contains(newPos)) {
                    roundRockList.remove(current);
                    roundRockList.add(rock, newPos);
                } else {
                    break;
                }
            }
        }
    }

    private void slideEast(int listSize) {
        roundRockList.sort((o1, o2) -> Long.compare(o2.col(), o1.col()));

        for (int rock = 0; rock < roundRockList.size(); rock++) {
            while (true) {
                Point2D current = roundRockList.get(rock);
                if (current.col() == listSize - 1) {
                    break;
                }

                Point2D newPos = new Point2D(current.row(), current.col() + 1);

                if (!roundRockList.contains(newPos) && !squareRockList.contains(newPos)) {
                    roundRockList.remove(current);
                    roundRockList.add(rock, newPos);
                } else {
                    break;
                }
            }
        }
    }

    private List<String> getRocks(String file) {
        List<String> list = fileStream(file).toList();
        roundRockList = get2DPoints(file, 'O');
        squareRockList = get2DPoints(file, '#');
        return list;
    }
}
