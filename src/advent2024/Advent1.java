package advent2024;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.frequency;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent1 {

    public static final String DELIMITER = " {3}";

    // https://adventofcode.com/2024/day/1
    public Integer runP1(String file) {
        List<Integer> first = newArrayList();
        List<Integer> second = newArrayList();

        fileStream(file).forEach(line -> {
            String[] s = line.split(DELIMITER);
            first.add(Integer.parseInt(s[0]));
            second.add(Integer.parseInt(s[1]));
        });

        first.sort(Integer::compareTo);
        second.sort(Integer::compareTo);

        return IntStream.range(0, first.size())
                .map(index -> Math.abs(first.get(index) - second.get(index)))
                .sum();
    }

    public Integer runP2(String file) {
        List<Integer> first = newArrayList();
        List<Integer> second = newArrayList();

        fileStream(file).forEach(line -> {
            String[] s = line.split(DELIMITER);
            first.add(Integer.parseInt(s[0]));
            second.add(Integer.parseInt(s[1]));
        });

        return first.stream()
                .mapToInt(integer -> integer * frequency(second, integer))
                .sum();
    }
}
