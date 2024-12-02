package advent2024;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.frequency;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent1 {
    // https://adventofcode.com/2024/day/1

    public Integer runP1(String file) {
        Result result = parseInput(file);

        result.first.sort(Integer::compareTo);
        result.second.sort(Integer::compareTo);

        return IntStream.range(0, result.first.size())
                .map(index -> Math.abs(result.first.get(index) - result.second.get(index)))
                .sum();
    }


    public Integer runP2(String file) {
        Result result = parseInput(file);

        return result.first.stream()
                .mapToInt(integer -> integer * frequency(result.second, integer))
                .sum();
    }

    private static Result parseInput(String file) {
        List<Integer> first = newArrayList();
        List<Integer> second = newArrayList();

        fileStream(file).forEach(line -> {
            String[] s = line.split(" {3}");
            first.add(Integer.parseInt(s[0]));
            second.add(Integer.parseInt(s[1]));
        });
        return new Result(first, second);
    }

    private record Result(List<Integer> first, List<Integer> second) {
    }

}
