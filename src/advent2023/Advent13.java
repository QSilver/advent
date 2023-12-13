package advent2023;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.min;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static util.Util.fileStream;

@Slf4j
public class Advent13 {
    // https://adventofcode.com/2023/day/13
    public Long runP1(String file) {
        return parseInput(file)
                .mapToLong(this::calculateField)
                .sum();
    }

    public Long runP2(String file) {
        return parseInput(file).mapToLong(field -> {
            long original = calculateField(field);

            List<String> smudgedFields = generateSmudgedFields(field);
            for (String smudge : smudgedFields) {
                long reflection = calculateFieldIgnoringOriginal(smudge, original);
                if (reflection != 0) {
                    log.info("Original: {}, Smudged: {}", original, reflection);
                    return reflection;
                }
            }
            return 0L;
        }).sum();
    }

    private List<String> generateSmudgedFields(String field) {
        List<String> newFields = newArrayList();
        for (int c = 0; c < field.length(); c++) {
            StringBuilder stringBuilder = new StringBuilder(field);
            if (field.charAt(c) == '.') {
                stringBuilder.setCharAt(c, '#');
            } else if (field.charAt(c) == '#') {
                stringBuilder.setCharAt(c, '.');
            }
            newFields.add(stringBuilder.toString());
        }
        return newFields;
    }

    private long calculateField(String field) {
        return calculateFieldIgnoringOriginal(field, 0);
    }

    private long calculateFieldIgnoringOriginal(String field, long original) {
        String[] lines = field.split("\n");

        Long row = reflectRow(lines, original);
        if (row != null) {
            return row;
        }

        Long col = reflectCol(lines, original);
        if (col != null) {
            return col;
        }
        return 0L;
    }

    private Long reflectCol(String[] lines, long original) {
        for (int col = 1; col < lines[0].length(); col++) {
            int maxReflectionCol = min(col, lines[0].length() - col);

            boolean reflect = true;
            for (String line : lines) {
                String left = line.substring(col - maxReflectionCol, col);
                String right = line.substring(col, col + maxReflectionCol);
                if (!left.contentEquals(new StringBuilder(right).reverse())) {
                    reflect = false;
                    break;
                }
            }

            if (reflect && col != original) {
                return (long) col;
            }
        }
        return null;
    }

    private Long reflectRow(String[] lines, long original) {
        for (int row = 1; row < lines.length; row++) {
            int maxReflectionRow = min(row, lines.length - row);

            boolean reflect = true;
            for (int refl = 0; refl < maxReflectionRow; refl++) {
                String top = lines[row - refl - 1];
                String bottom = lines[row + refl];
                if (!top.equals(bottom)) {
                    reflect = false;
                    break;
                }
            }

            if (reflect && 100L * row != original) {
                return 100L * row;
            }
        }
        return null;
    }

    private static Stream<String> parseInput(String file) {
        String collect = fileStream(file).collect(joining("\n"));
        return stream(collect.split("\n\n"));
    }
}
