package advent2024;

import lombok.extern.slf4j.Slf4j;
import util.Util2D.Point2D;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static util.Util2D.loadCharMatrix;

@Slf4j
public class Advent4 {
    // https://adventofcode.com/2024/day/4

    public Integer runP1(String file) {
        char[][] chars = loadCharMatrix(file);

        int count = 0;
        for (int row = 0; row < chars.length; row++) {
            for (int col = 0; col < chars[row].length; col++) {
                count += identifyP1(chars, new Point2D(row, col));
            }
        }

        return count;
    }

    public Integer runP2(String file) {
        char[][] chars = loadCharMatrix(file);

        int count = 0;
        for (int row = 0; row < chars.length; row++) {
            for (int col = 0; col < chars[row].length; col++) {
                count += identifyP2(chars, new Point2D(row, col));
            }
        }

        return count;
    }

    private int identifyP1(char[][] chars, Point2D point) {
        List<List<Point2D>> toCheck = newArrayList();

        int row = (int) point.row();
        int col = (int) point.col();

        toCheck.add(newArrayList(new Point2D(row - 1, col + 0), new Point2D(row + -2, col + 0), new Point2D(row + -3, col + 0))); // UP
        toCheck.add(newArrayList(new Point2D(row + 1, col + 0), new Point2D(row + 2, col + 0), new Point2D(row + 3, col + 0))); // DOWN
        toCheck.add(newArrayList(new Point2D(row + 0, col + -1), new Point2D(row + 0, col + -2), new Point2D(row + 0, col + -3))); // LEFT
        toCheck.add(newArrayList(new Point2D(row + 0, col + 1), new Point2D(row + 0, col + 2), new Point2D(row + 0, col + 3))); // RIGHT

        toCheck.add(newArrayList(new Point2D(row + -1, col + -1), new Point2D(row + -2, col + -2), new Point2D(row + -3, col + -3))); // UP-LEFT
        toCheck.add(newArrayList(new Point2D(row + 1, col + 1), new Point2D(row + 2, col + 2), new Point2D(row + 3, col + 3))); // DOWN-RIGHT
        toCheck.add(newArrayList(new Point2D(row + 1, col + -1), new Point2D(row + 2, col + -2), new Point2D(row + 3, col + -3))); // DOWN-LEFT
        toCheck.add(newArrayList(new Point2D(row + -1, col + 1), new Point2D(row + -2, col + 2), new Point2D(row + -3, col + 3))); // UP-RIGHT

        AtomicInteger count = new AtomicInteger();
        if (chars[(int) point.row()][(int) point.col()] == 'X') {
            toCheck.forEach(direction -> {
                try {
                    if (chars[(int) direction.get(0).row()][(int) direction.get(0).col()] == 'M') {
                        if (chars[(int) direction.get(1).row()][(int) direction.get(1).col()] == 'A') {
                            if (chars[(int) direction.get(2).row()][(int) direction.get(2).col()] == 'S') {
                                count.getAndIncrement();
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException _) {

                }
            });
        }

        return count.get();
    }

    private int identifyP2(char[][] chars, Point2D point) {
        List<List<Point2D>> toCheck = newArrayList();

        int row = (int) point.row();
        int col = (int) point.col();

        toCheck.add(newArrayList(new Point2D(row - 1, col - 1), new Point2D(row + 1, col + 1), new Point2D(row - 1, col + 1), new Point2D(row + 1, col - 1))); // UP-LEFT to DOWN-RIGHT and DOWN-LEFT to UP-RIGHT
        toCheck.add(newArrayList(new Point2D(row - 1, col - 1), new Point2D(row + 1, col + 1), new Point2D(row + 1, col - 1), new Point2D(row - 1, col + 1))); // UP-LEFT to DOWN-RIGHT and UP-RIGHT to DOWN-LEFT
        toCheck.add(newArrayList(new Point2D(row + 1, col + 1), new Point2D(row - 1, col - 1), new Point2D(row - 1, col + 1), new Point2D(row + 1, col - 1))); // DOWN-RIGHT to UP-LEFT and DOWN-LEFT to UP-RIGHT
        toCheck.add(newArrayList(new Point2D(row + 1, col + 1), new Point2D(row - 1, col - 1), new Point2D(row + 1, col - 1), new Point2D(row - 1, col + 1))); // DOWN-RIGHT to UP-LEFT and DOWN-LEFT to UP-RIGHT

        AtomicInteger count = new AtomicInteger();
        if (chars[(int) point.row()][(int) point.col()] == 'A') {
            toCheck.forEach(direction -> {
                try {
                    if (chars[(int) direction.get(0).row()][(int) direction.get(0).col()] == 'M') {
                        if (chars[(int) direction.get(1).row()][(int) direction.get(1).col()] == 'S') {
                            if (chars[(int) direction.get(2).row()][(int) direction.get(2).col()] == 'M') {
                                if (chars[(int) direction.get(3).row()][(int) direction.get(3).col()] == 'S') {
                                    count.getAndIncrement();
                                }
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException _) {

                }
            });
        }

        return count.get();
    }
}
