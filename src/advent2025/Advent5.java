package advent2025;

import lombok.extern.slf4j.Slf4j;
import util.Interval;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static util.InputUtils.readDoubleNewlineBlocks;

@Slf4j
public class Advent5 {
    // https://adventofcode.com/2025/day/5

    public Long runP1(String file) {
        String[] strings = readDoubleNewlineBlocks(file);

        List<Interval> freshIntervals = strings[0].lines()
                .map(Interval::new)
                .toList();

        return strings[1].lines()
                .map(Long::parseLong)
                .filter(ingredient -> freshIntervals.stream()
                        .anyMatch(interval -> interval.contains(ingredient)))
                .count();
    }

    public Long runP2(String file) {
        String[] strings = readDoubleNewlineBlocks(file);

        List<Interval> freshIntervals = strings[0].lines()
                .map(Interval::new)
                .toList();

        return overlapAndSplit(freshIntervals).stream()
                .mapToLong(Interval::count)
                .sum();
    }

    private static List<Interval> overlapAndSplit(List<Interval> freshIntervals) {
        List<Interval> intervals = freshIntervals.stream()
                .sorted(comparingLong(a -> a.start))
                .collect(Collectors.toList());

        for (int i = 0; i < intervals.size() - 1; i++) {
            Interval current = intervals.get(i);
            for (int j = i + 1; j < intervals.size(); j++) {
                Interval other = intervals.get(j);

                List<Interval> overlaps = current.overlaps(other);
                if (!overlaps.isEmpty()) {
                    intervals.remove(other);
                    intervals.remove(current);
                    intervals.addAll(overlaps);

                    // restart search
                    intervals.sort(comparingLong(a -> a.start));

                    i = -1;
                    break;
                }
            }
        }
        return intervals;
    }
}
