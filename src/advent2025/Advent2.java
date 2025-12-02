package advent2025;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent2 {
    // https://adventofcode.com/2025/day/1

    public Long runP1(String file) {
        Stream<String> stringStream = fileStream(file);

        List<Interval> list = stringStream
                .flatMap(line -> Arrays.stream(line.split(","))
                        .map(Advent2::getInterval)).toList();

        return list.stream()
                .map(interval -> {
                    long acc = 0;
                    for (long i = interval.start; i <= interval.end; i++) {
                        String s = STR."\{i}";
                        if (s.length() % 2 == 0) {
                            if (s.substring(0, s.length() / 2).equals(s.substring(s.length() / 2))) {
                                acc += i;
                            }
                        }
                    }
                    return acc;
                })
                .mapToLong(Long::longValue)
                .sum();
    }

    private static Interval getInterval(String s) {
        String[] split = s.split("-");
        return new Interval(parseLong(split[0]), parseLong(split[1]));
    }

    public Long runP2(String file) {
        Stream<String> stringStream = fileStream(file);

        List<Interval> list = stringStream
                .flatMap(line -> Arrays.stream(line.split(","))
                        .map(Advent2::getInterval)).toList();

        return list.stream()
                .map(interval -> {
                    long acc = 0;
                    for (long number = interval.start; number <= interval.end; number++) {
                        String s = STR."\{number}";

                        for (int i = 10; i >= 2; i--) {
                            if (s.length() % i == 0) {
                                String substring = s.substring(0, s.length() / i);
                                if (substring.repeat(i).equals(s)) {
                                    acc += number;
                                    break;
                                }
                            }
                        }
                    }
                    return acc;
                })
                .mapToLong(Long::longValue)
                .sum();
    }

    record Interval(long start, long end) {

    }
}
