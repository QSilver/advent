package advent2025;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Long.parseLong;
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
                .sorted(comparingLong(a -> a.start))
                .collect(Collectors.toList());

        for (int i = 0; i < freshIntervals.size() - 1; i++) {
            Interval current = freshIntervals.get(i);
            for (int j = i + 1; j < freshIntervals.size(); j++) {
                Interval other = freshIntervals.get(j);

                List<Interval> overlaps = current.overlaps(other);
                if (!overlaps.isEmpty()) {
                    freshIntervals.remove(other);
                    freshIntervals.remove(current);
                    freshIntervals.addAll(overlaps);

                    // restart search
                    freshIntervals.sort(comparingLong(a -> a.start));

                    i = -1;
                    break;
                }
            }
        }

        return freshIntervals.stream()
                .mapToLong(Interval::count)
                .sum();
    }

    @ToString
    public static class Interval {
        long start;
        long end;

        Interval(String interval) {
            String[] split = interval.split("-");
            this.start = parseLong(split[0]);
            this.end = parseLong(split[1]);
        }

        public Interval(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public boolean contains(long number) {
            return number >= start && number <= end;
        }

        public long count() {
            return end - start + 1;
        }

        public List<Interval> overlaps(Interval other) {
            List<Interval> overlaps = newArrayList();

            if (this.start < other.start && this.end >= other.start && this.end < other.end) {
                overlaps.add(new Interval(this.start, other.start));
                overlaps.add(new Interval(other.start + 1, this.end));
                overlaps.add(new Interval(this.end + 1, other.end));
            } else if (other.start < this.start && other.end >= this.start && other.end < this.end) {
                overlaps.add(new Interval(other.start, this.start));
                overlaps.add(new Interval(this.start + 1, other.end));
                overlaps.add(new Interval(other.end + 1, this.end));
            } else if (this.start >= other.start && this.end <= other.end) {
                overlaps.add(new Interval(other.start, this.start));
                overlaps.add(new Interval(this.start + 1, this.end));
                overlaps.add(new Interval(this.end + 1, other.end));
            } else if (other.start >= this.start && other.end <= this.end) {
                overlaps.add(new Interval(this.start, other.start));
                overlaps.add(new Interval(other.start + 1, other.end));
                overlaps.add(new Interval(other.end + 1, this.end));
            }

            return overlaps;
        }
    }

}
