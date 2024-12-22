package util;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Util2D.PointWithLabel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.joining;

@Slf4j
@ExtensionMethod({Extensions.class})
public class InputUtils {
    public static String[] readDoubleNewlineBlocks(String fileName) {
        return fileStream(fileName)
                .collect(joining("\n"))
                .split("\n\n");
    }

    public static Stream<String> fileStream(String fileName) {
        try {
            return lines(get(".\\resources", fileName));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return newArrayList("").stream();
    }

    public static Stream<String> readSplitLine(String fileName) {
        return fileStream(fileName).collect(joining()).split(" ").stream();
    }

    public static Stream<Long> readSplitLineNumbers(String fileName) {
        return readSplitLine(fileName).mapToLong(Long::parseLong).boxed();
    }

    public static ArrayList<String> splitLine(Stream<String> stream) {
        Optional<String> first = stream.findFirst();
        return first.map(s -> newArrayList(s.split(",")))
                .orElseGet(Lists::newArrayList);
    }

    public static Stream<String> getFirstLineSplit(String file, String delimiter) {
        return fileStream(file).findFirst().orElseThrow().split(delimiter).stream();
    }

    public static List<Point2D> get2DPoints(String file, char point) {
        List<String> list = fileStream(file).toList();

        List<Point2D> points = newArrayList();
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                if (s.charAt(col) == point) {
                    points.add(new Point2D(row, col));
                }
            }
        }
        return points;
    }

    public static List<PointWithLabel> get2DPointsIgnore(String file, char ignore) {
        List<String> list = fileStream(file).toList();

        List<PointWithLabel> points = newArrayList();
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                if (s.charAt(col) != ignore) {
                    points.add(new PointWithLabel(new Point2D(row, col), s.charAt(col)));
                }
            }
        }
        return points;
    }

    public static int[][] loadIntMatrix(String file) {
        List<String> list = fileStream(file).toList();

        int[][] matrix = new int[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                matrix[row][col] = parseInt(valueOf(s.charAt(col)));
            }
        }
        return matrix;
    }

    public static Character[][] loadCharMatrix(String file) {
        List<String> list = fileStream(file).toList();

        Character[][] matrix = new Character[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                matrix[row][col] = s.charAt(col);
            }
        }
        return matrix;
    }

    @SneakyThrows
    public static void clearConsole() {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO()
                .start()
                .waitFor();
        log.debug("{}", System.in.read());
    }

    public static void main(String[] args) {
        fileStream("advent2023/advent2.in");
        // TODO - write generic split array parser
    }
}
