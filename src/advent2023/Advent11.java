package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util2D.Point2D;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.fileStream;
import static util.Util2D.get2DPoints;

@Slf4j
public class Advent11 {
    // https://adventofcode.com/2023/day/11
    Set<Integer> emptyColumns = newHashSet();
    Set<Integer> emptyRows = newHashSet();

    List<Point2D> points = newArrayList();

    public Long runP1(String file) {
        return run(file, 1);
    }

    public Long runP2(String file) {
        return run(file, 999999);
    }

    private long run(String file, int expansion) {
        List<String> list = fileStream(file).toList();

        Set<Integer> columns = newHashSet();
        Set<Integer> rows = newHashSet();

        points = get2DPoints(file, '#');

        points.forEach(point2D -> {
            rows.add((int) point2D.row());
            columns.add((int) point2D.col());
        });

        emptyColumns = IntStream.range(0, list.size())
                .filter(value -> !columns.contains(value))
                .boxed().collect(Collectors.toSet());
        emptyRows = IntStream.range(0, list.size())
                .filter(value -> !rows.contains(value))
                .boxed().collect(Collectors.toSet());

        long pointSums = 0L;
        for (int p1 = 0; p1 < points.size() - 1; p1++) {
            for (int p2 = p1 + 1; p2 < points.size(); p2++) {
                long distance = getDistance(points.get(p1), points.get(p2), expansion);
                pointSums += distance;
            }
        }
        return pointSums;
    }

    long getDistance(Point2D p1, Point2D p2, long expansion) {
        int minRow = (int) Math.min(p1.row(), p2.row());
        int maxRow = (int) Math.max(p1.row(), p2.row());
        int minCol = (int) Math.min(p1.col(), p2.col());
        int maxCol = (int) Math.max(p1.col(), p2.col());

        long rowSum = 0;
        for (int row = minRow; row < maxRow; row++) {
            if (emptyRows.contains(row)) {
                rowSum += expansion;
            }
            rowSum++;
        }

        long colSum = 0;
        for (int col = minCol; col < maxCol; col++) {
            if (emptyColumns.contains(col)) {
                colSum += expansion;
            }
            colSum++;
        }

        return rowSum + colSum;
    }
}
