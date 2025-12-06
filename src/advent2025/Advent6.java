package advent2025;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.IntStream.range;
import static util.InputUtils.fileStream;
import static util.Util.optionalTry;

@Slf4j
public class Advent6 {
    // https://adventofcode.com/2025/day/6

    public Long runP1(String file) {
        List<List<String>> transposed = newArrayList();
        fileStream(file)
                .map(Advent6::getNumbersInRow)
                .forEach(transposeRow(transposed));

        return transposed.stream()
                .map(Advent6::calculateColumn)
                .mapToLong(Long::longValue)
                .sum();
    }

    private static List<String> getNumbersInRow(String line) {
        return Arrays.stream(line.split(" "))
                .filter(s -> !s.isBlank())
                .toList();
    }

    private static Consumer<List<String>> transposeRow(List<List<String>> transposed) {
        return row -> {
            for (int index = 0; index < row.size(); index++) {
                if (index >= transposed.size()) {
                    transposed.add(newArrayList());
                }
                transposed.get(index).add(row.get(index));
            }
        };
    }

    private static long calculateColumn(List<String> column) {
        return column.subList(0, column.size() - 1).stream()
                .mapToLong(Long::parseLong)
                .reduce(applySign(column.getLast().charAt(0)))
                .orElseThrow();
    }

    private static LongBinaryOperator applySign(char sign) {
        return (a, b) -> sign == '+' ? a + b : a * b;
    }

    public Long runP2(String file) {
        List<String> input = fileStream(file).toList();

        List<Integer> signPositions = getSignPositions(input);

        int lengthOfLongestLine = input.stream()
                .mapToInt(String::length).max()
                .orElse(0) + 1;

        return range(0, signPositions.size())
                .mapToLong(column -> {
                    int startPos = signPositions.get(column);
                    int endPos = optionalTry(() -> signPositions.get(column + 1)).orElse(lengthOfLongestLine) - 1;

                    return calculateColumn(startPos, endPos, input);
                })
                .sum();
    }

    private static List<Integer> getSignPositions(List<String> input) {
        return range(0, input.getLast().length())
                .filter(i -> input.getLast().charAt(i) != ' ')
                .boxed()
                .toList();
    }

    private static long calculateColumn(int startPos, int endPos, List<String> input) {
        return range(startPos, endPos)
                .mapToObj(i -> processVerticalNumber(input, i))
                .mapToLong(Long::parseLong)
                .reduce(applySign(input.getLast().charAt(startPos)))
                .orElseThrow();
    }

    private static String processVerticalNumber(List<String> input, int index) {
        return transpose(input, index).stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString().trim();
    }

    private static List<Character> transpose(List<String> input, int index) {
        List<Character> number = newArrayList();
        for (int j = 0; j < input.size() - 1; j++) {
            if (index < input.get(j).length()) {
                number.add(input.get(j).charAt(index));
            }
        }
        return number;
    }
}
