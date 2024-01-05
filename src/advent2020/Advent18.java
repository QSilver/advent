package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Advent18 {
    private static final Function<String, Long> FUNCTION = Advent18::parsePrecedence;

    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2020/advent18")
                                 .map(line -> line.replace(" ", ""))
                                 .collect(Collectors.toList());

        long sum = input.stream()
                        .map(Advent18::solveLine)
                        .map(FUNCTION)
                        .reduce(Long::sum)
                        .orElse(0L);
        log.info("{}", sum);
    }

    private static String solveLine(String line) {
        while (line.contains(")")) {
            line = solveBracket(line);
        }
        return line;
    }

    private static String solveBracket(String line) {
        int startPos = !line.contains(")") ? line.length() : line.indexOf(")");
        int endPos = line.substring(0, startPos)
                         .lastIndexOf("(") == -1 ? 0 : line.substring(0, startPos)
                                                           .lastIndexOf("(");
        return line.substring(0, endPos) +
                FUNCTION.apply(line.substring((endPos) + 1, startPos)) +
                line.substring((startPos) + 1);
    }

    private static long parsePrecedence(String input) {
        input = calculate(input, "+");
        input = calculate(input, "*");
        return Long.parseLong(input);
    }

    private static String calculate(String input, String sign) {
        while (input.contains(sign)) {
            int pos = input.indexOf(sign);
            String[] split1 = input.substring(0, pos)
                                   .split("[^0-9]+");
            String[] split2 = input.substring(pos + 1)
                                   .split("[^0-9]+");
            long x1 = Long.parseLong(split1[split1.length - 1]);
            long x2 = Long.parseLong(split2[0]);

            long temp = 0;
            if (sign.equals("+")) {
                temp = x1 + x2;
            } else if (sign.equals("*")) {
                temp = x1 * x2;
            }
            input = input.substring(0, pos - split1[split1.length - 1].length()) +
                    temp +
                    input.substring(pos + split2[0].length() + 1);
        }
        return input;
    }

    private static long calculateNoPrecedence(String input) {
        long acc = Long.parseLong(input.split("[^0-9]+")[0]);
        int pos = 0;
        while (pos < input.length() - 1) {
            if (input.charAt(pos) == '+') {
                acc += Long.parseLong(input.substring(pos + 1)
                                           .split("[^0-9]+")[0]);
                pos += input.substring(pos + 1)
                            .split("[^0-9]+")[0].length() - 1;
            } else if (input.charAt(pos) == '*') {
                acc *= Long.parseLong(input.substring(pos + 1)
                                           .split("[^0-9]+")[0]);
                pos += input.substring(pos + 1)
                            .split("[^0-9]+")[0].length() - 1;
            }
            pos++;
        }
        return acc;
    }
}
