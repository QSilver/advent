package util;

import lombok.ToString;
import lombok.With;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Long.parseLong;

@With
@ToString
public class Interval {
    public long start;
    public long end;

    boolean startInclusive = true;
    boolean endInclusive = true;

    public Interval(String interval) {
        String[] split = interval.split("-");
        this.start = parseLong(split[0]);
        this.end = parseLong(split[1]);
    }

    public Interval(long start, long end, boolean startInclusive, boolean endInclusive) {
        this.start = start;
        this.end = end;
        this.startInclusive = startInclusive;
        this.endInclusive = endInclusive;
    }

    public Interval(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(long number) {
        if (startInclusive && endInclusive) {
            return number >= start && number <= end;
        } else if (startInclusive) {
            return number >= start && number < end;
        } else if (endInclusive) {
            return number > start && number <= end;
        } else {
            return number > start && number < end;
        }
    }

    public long count() {
        if (startInclusive && endInclusive) {
            return end - start + 1;
        } else if (startInclusive || endInclusive) {
            return end - start;
        } else {
            return end - start - 1;
        }
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

        return overlaps.stream()
                .filter(interval -> interval.count() > 0)
                .toList();
    }
}