package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Long.min;
import static java.lang.Long.parseLong;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static util.InputUtils.readDoubleNewlineBlocks;

@Slf4j
public class Advent5 {
    // https://adventofcode.com/2023/day/5
    Map<String, List<Conversion>> conversions = newHashMap();
    List<String> types = newArrayList("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location");

    public Long runP1(String file) {
        String[] seeds = parseInputAndGetSeeds(file);

        long minLocation = Long.MAX_VALUE;
        for (int i = 1; i < seeds.length; i++) {
            long current = parseLong(seeds[i]);
            for (String string : types) {
                List<Conversion> translation = conversions.get(string);

                long match = current;
                for (Conversion conversion : translation) {
                    if (current >= conversion.start && current <= conversion.end) {
                        match = current - conversion.start + conversion.destination;
                    }
                }
                current = match;
            }

            if (current < minLocation) {
                minLocation = current;
            }
        }

        return minLocation;
    }

    public Long runP2(String file) {
        String[] seeds = parseInputAndGetSeeds(file);

        List<Interval> intervals = newArrayList();
        for (int seed = 1; seed < seeds.length; seed += 2) {
            long start = parseLong(seeds[seed]);
            long l = parseLong(seeds[seed + 1]);
            intervals.add(new Interval(start, start + l - 1, false, false));
        }

        // apply each set of conversions in order
        for (int type = 1; type < types.size(); type++) {
            String convertTo = types.get(type);
            List<Conversion> list = conversions.get(convertTo);
            intervals = applyConversions(list, intervals);
        }

        return intervals.stream()
                .sorted(comparing(o -> o.start))
                .findFirst().orElseThrow()
                .start;
    }

    private String[] parseInputAndGetSeeds(String file) {
        types.forEach(s -> conversions.put(s, newArrayList()));

        String[] split = readDoubleNewlineBlocks(file);
        parseConversionBlock(split);

        return split[0].split(" ");
    }

    private void parseConversionBlock(String[] conversionBlock) {
        for (int i = 1; i < conversionBlock.length; i++) {
            List<String> lines = stream(conversionBlock[i].split("\n")).toList();
            String mapType = lines.getFirst().split(" ")[0].split("-")[2];

            lines.subList(1, lines.size())
                    .forEach(line -> conversions.get(mapType).add(new Conversion(line.split(" "))));
        }
    }

    private List<Interval> applyConversions(List<Conversion> conversions, List<Interval> intervals) {
        List<Interval> newIntervals = newArrayList();

        // break down intervals into smaller ones so that an interval is either fully contained or fully outside a conversion
        intervals.forEach(interval -> {
            ArrayList<Interval> result = newArrayList();
            breakDownInterval(conversions, interval, result);
            newIntervals.addAll(result);
        });

        List<Interval> result = newArrayList();

        // for each interval, find the conversion that contains it, then convert it, otherwise keep it as is
        newIntervals.forEach(interval -> conversions.stream()
                .filter(conversion -> conversion.containsInterval(interval))
                .findFirst()
                .ifPresentOrElse(conversion -> result.add(convertInterval(interval, conversion)),
                        () -> result.add(interval)));
        return result;
    }

    private Interval convertInterval(Interval interval, Conversion conversion) {
        long delta = conversion.destination - conversion.start;
        return interval
                .withStart(interval.start + delta)
                .withEnd(interval.end + delta);
    }

    private void breakDownInterval(List<Conversion> conversions, Interval interval, List<Interval> result) {
        List<Long> breakpoints = newArrayList();

        conversions.stream()
                .sorted(comparing(o -> o.start))
                .filter(conversion -> interval.contains(conversion.start))
                .forEach(conversion -> breakpoints.add(conversion.start));
        conversions.stream()
                .sorted(comparing(Conversion::end))
                .filter(conversion -> interval.contains(conversion.end))
                .forEach(conversion -> breakpoints.add(conversion.end));

        breakpoints.sort(Long::compare);

        result.addAll(splitIntervalAtBreakpoints(interval, breakpoints));
    }

    private List<Interval> splitIntervalAtBreakpoints(Interval interval, List<Long> breakpoints) {
        boolean requiresBreak = false;
        long prev = interval.start;
        List<Interval> result = newArrayList();

        for (int b = 0; b < breakpoints.size(); b++) {
            Long breakpoint = breakpoints.get(b);

            result.add(interval
                    .withStart(prev)
                    .withEnd(breakpoint - 1));

            Long tempBreak = interval.end;
            if (b != breakpoints.size() - 1) {
                tempBreak = breakpoints.get(b + 1);
            }
            long end = min(interval.end, tempBreak);
            result.add(interval
                    .withStart(breakpoint)
                    .withEnd(end));

            prev = breakpoint;
            requiresBreak = true;
        }

        if (!requiresBreak) {
            result.add(interval);
        }

        return result;
    }

    record Conversion(Long destination, Long start, Long end) {
        Conversion(String[] entry) {
            this(parseLong(entry[0]), parseLong(entry[1]), parseLong(entry[1]) + parseLong(entry[2]) - 1);
        }

        boolean containsInterval(Interval interval) {
            return this.start <= interval.start && this.end >= interval.end;
        }
    }
}
