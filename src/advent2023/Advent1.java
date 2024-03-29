package advent2023;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent1 {
    // https://adventofcode.com/2023/day/1
    public static final Pattern DIGIT_REGEX = Pattern.compile("\\d");
    private final List<String> digits = newArrayList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "zero");
    private final List<String> modded = newArrayList("o1e", "t2o", "t3e", "f4r", "f5e", "s6x", "s7n", "e8t", "n9e", "z0o");

    public Integer runP1(String file) {
        return fileStream(file)
                .map(this::extractFirstAndLastDigit)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    public Integer runP2(String file) {
        return fileStream(file)
                .map(this::replaceDigitsNondestructive)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private String replaceDigitsNondestructive(String s) {
        String string = s;
        AtomicBoolean stop = new AtomicBoolean(false);

        while (!stop.get()) {
            string = replaceDigits(string, stop);
        }

        return extractFirstAndLastDigit(string);
    }

    private String extractFirstAndLastDigit(String s) {
        Matcher matcher = DIGIT_REGEX.matcher(s);
        List<String> digits = newArrayList();

        while (matcher.find()) {
            digits.add(matcher.group());
        }

        return digits.getFirst() + digits.getLast();
    }

    private String replaceDigits(String string, AtomicBoolean stop) {
        for (int i = 0; i < digits.size(); i++) {
            String target = digits.get(i);
            String replacement = modded.get(i);
            if (string.contains(target)) {
                string = string.replace(target, replacement);
                stop.set(false);
                break;
            }
            stop.set(true);
        }
        return string;
    }
}
