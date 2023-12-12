package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent11 {
    Set<Integer> emptyColumns = newHashSet();
    Set<Integer> emptyRows = newHashSet();

    List<Point> points = newArrayList();

    public Long runP1(String file) {
        return run(file, 1);
    }

    public Long runP2(String file) {
        return run(file, 999999);
    }

    private long run(String file, int d) {
        List<String> list = Util.fileStream(file).toList();

        Set<Integer> columns = newHashSet();
        Set<Integer> rows = newHashSet();

        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                if (s.charAt(col) == '#') {
                    points.add(new Point(row, col));
                    columns.add(col);
                    rows.add(row);
                }
            }
        }

        emptyColumns = IntStream.range(0, list.size()).filter(value -> !columns.contains(value)).boxed().collect(Collectors.toSet());
        emptyRows = IntStream.range(0, list.size()).filter(value -> !rows.contains(value)).boxed().collect(Collectors.toSet());

        long pointSums = 0L;
        for (int p1 = 0; p1 < points.size() - 1; p1++) {
            for (int p2 = p1 + 1; p2 < points.size(); p2++) {
                long distance = getDistance(points.get(p1), points.get(p2), d);
                pointSums += distance;
            }
        }

        return pointSums;
    }

    long getDistance(Point p1, Point p2, long d) {
        int minRow = Math.min(p1.row, p2.row);
        int maxRow = Math.max(p1.row, p2.row);
        int minCol = Math.min(p1.col, p2.col);
        int maxCol = Math.max(p1.col, p2.col);

        long rowSum = 0;
        for (int row = minRow; row < maxRow; row++) {
            if (emptyRows.contains(row)) {
                rowSum += d;
            }
            rowSum++;
        }

        long colSum = 0;
        for (int col = minCol; col < maxCol; col++) {
            if (emptyColumns.contains(col)) {
                colSum += d;
            }
            colSum++;
        }

        return rowSum + colSum;
    }

    record Point(int row, int col) {

    }
}
