package util;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.abs;
import static java.util.stream.IntStream.range;

@Slf4j
public
class Util {
    public static String[] readDoubleNewlineBlocks(String fileName) {
        return fileStream(fileName).collect(Collectors.joining("\n")).split("\n\n");
    }

    public static List<String> lines(String fileName) {
        return fileStream(fileName).collect(Collectors.toList());
    }

    public static Stream<String> fileStream(String fileName) {
        try {
            return Files.lines(Paths.get(".\\resources", fileName));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return newArrayList("").stream();
    }

    public static ArrayList<String> splitLine(Stream<String> stream) {
        Optional<String> first = stream.findFirst();
        return first.map(s -> newArrayList(s.split(",")))
                .orElseGet(Lists::newArrayList);
    }

    @SneakyThrows
    public static void clearConsole() {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO()
                .start()
                .waitFor();
        log.debug("{}", System.in.read());
    }

    public static Set<Integer> rangeToSet(int startInclusive, int endExclusive) {
        return range(startInclusive, endExclusive).boxed().collect(Collectors.toSet());
    }

    public static Surface calculateSurface(List<? extends Point> points) {
        long perimeter = 0L;
        long shoelaceArea = 0L;

        // https://en.m.wikipedia.org/wiki/Shoelace_formula
        for (int i = 0; i < points.size() - 1; i++) {
            Point current = points.get(i);
            Point next = points.get(i + 1);

            perimeter += current.distanceTo(next);
            shoelaceArea += ((current.row() * next.col()) - (next.row() * current.col()));
        }

        Point last = points.getLast();
        Point first = points.getFirst();

        perimeter += last.distanceTo(first);
        shoelaceArea += ((last.row() * first.col()) - (first.row() * last.col()));
        shoelaceArea = abs(shoelaceArea) / 2L;

        // https://en.m.wikipedia.org/wiki/Pick%27s_theorem
        long insidePoints = shoelaceArea + 1 - perimeter / 2;
        long area = insidePoints + perimeter;

        return new Surface(perimeter, area, insidePoints, shoelaceArea, newArrayList(points));
    }

    public record Surface(long perimeter, long area, long insidePoints, long shoelaceArea, List<Point> points) {
    }

    public record Point(long row, long col) {

        long distanceTo(Point other) {
            return abs(row - other.row) + abs(col - other.col);
        }
    }
}
