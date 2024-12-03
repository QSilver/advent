package advent2024;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent3 {
    // https://adventofcode.com/2024/day/3

    public Integer runP1(String file) {
        String mul = "mul\\(\\d+,\\d+\\)";
        return fileStream(file)
                .map(compile(mul)::matcher)
                .flatMap(Advent3::extractPattern)
                .mapToInt(Advent3::calculate)
                .sum();
    }

    public Integer runP2(String file) {
        AtomicBoolean enabled = new AtomicBoolean(true);
        String mulWithToggle = "mul\\(\\d+,\\d+\\)|do\\(\\)|don't\\(\\)";
        return fileStream(file)
                .map(compile(mulWithToggle)::matcher)
                .flatMap(Advent3::extractPattern)
                .mapToInt(find -> calculateWithToggle(find, enabled))
                .sum();
    }

    private static Stream<String> extractPattern(Matcher matcher) {
        List<String> finds = newArrayList();
        while (matcher.find()) {
            finds.add(matcher.group());
        }
        return finds.stream();
    }

    private static int calculate(String find) {
        String[] split = find.substring(4, find.length() - 1).split(",");
        return parseInt(split[0]) * parseInt(split[1]);
    }

    private static int calculateWithToggle(String find, AtomicBoolean enabled) {
        if (find.equals("do()")) {
            enabled.set(true);
            return 0;
        }
        if (find.equals("don't()")) {
            enabled.set(false);
            return 0;
        }

        if (enabled.get()) {
            return calculate(find);
        }
        return 0;
    }
}
