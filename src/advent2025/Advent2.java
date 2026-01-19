package advent2025;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
import static java.util.stream.LongStream.rangeClosed;
import static util.InputUtils.fileStream;
import static util.Util.primeFactors;

@Slf4j
public class Advent2 {
    // https://adventofcode.com/2025/day/2

    public Long runP1(String file) {
        return execute(file, false);
    }

    public Long runP2(String file) {
        return execute(file, true);
    }

    private static long execute(String file, boolean allFactors) {
        return fileStream(file)
                .flatMap(Advent2::getIntervals)
                .mapToLong(processInterval(allFactors))
                .sum();
    }

    private static Stream<Interval> getIntervals(String line) {
        return Arrays.stream(line.split(","))
                .map(Interval::new);
    }

    private static ToLongFunction<Interval> processInterval(boolean allFactors) {
        return interval -> rangeClosed(interval.start, interval.end)
                .map(number -> check(String.valueOf(number), allFactors) ? number : 0L)
                .sum();
    }

    private static boolean check(String number, boolean allFactors) {
        List<Long> primeFactors = primeFactors(number.length());
        return (allFactors ? primeFactors : List.of(2L))
                .stream()
                .mapToInt(Long::intValue)
                .anyMatch(primeFactor -> isRepeated(number, primeFactor));
    }

    private static boolean isRepeated(String str, int factor) {
        String substring = str.substring(0, str.length() / factor);
        return substring.repeat(factor).equals(str);
    }

    static class Interval {
        long start;
        long end;

        Interval(String interval) {
            String[] split = interval.split("-");
            this.start = parseLong(split[0]);
            this.end = parseLong(split[1]);
        }
    }
}
