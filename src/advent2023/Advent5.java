package advent2023;

import lombok.With;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Long.parseLong;

@Slf4j
public class Advent5 {
    Map<String, List<String>> translations = newHashMap();
    Map<String, List<Conversion>> conversions = newHashMap();
    List<String> types = newArrayList("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location");

    public Long runP1(String file) {
        String[] split = Util.fileStream(file)
                .collect(Collectors.joining("\n"))
                .split("\n\n");

        types.forEach(s -> translations.put(s, newArrayList()));

        for (int i = 1; i < split.length; i++) {
            String[] lines = split[i].split("\n");
            String mapType = lines[0].split(" ")[0].split("-")[2];
            translations.get(mapType).addAll(Arrays.asList(lines).subList(1, lines.length));
        }

        long minLocation = Long.MAX_VALUE;

        String[] seeds = split[0].split(" ");
        for (int i = 1; i < seeds.length; i++) {
            long current = parseLong(seeds[i]);
            for (String string : types) {
                List<String> translation = translations.get(string);

                long match = current;
                for (String s : translation) {
                    String[] line = s.split(" ");

                    long start = parseLong(line[1]);
                    long range = parseLong(line[2]);
                    if (current >= start && current < start + range) {
                        match = current - start + parseLong(line[0]);
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
        String[] split = Util.fileStream(file)
                .collect(Collectors.joining("\n"))
                .split("\n\n");
        types.forEach(s -> conversions.put(s, newArrayList()));

        for (int i = 1; i < split.length; i++) {
            String[] lines = split[i].split("\n");
            String mapType = lines[0].split(" ")[0].split("-")[2];

            Arrays.stream(lines).toList().subList(1, lines.length).forEach(line -> {
                String[] entry = line.split(" ");
                conversions.get(mapType)
                        .add(new Conversion(mapType, parseLong(entry[0]), parseLong(entry[1]), parseLong(entry[2])));
            });
        }

        String[] seeds = split[0].split(" ");

        List<Interval> intervals = newArrayList();
        for (int seed = 1; seed <= seeds.length / 2 + 1; seed += 2) {
            intervals.add(new Interval("seed", parseLong(seeds[seed]), parseLong(seeds[seed + 1])));
        }

        for (int type = 1; type < types.size(); type++) {
            String convertTo = types.get(type);
            List<Conversion> list = conversions.get(convertTo);
            intervals = convert(list, intervals, convertTo);
        }

        return 0L;
    }

    private List<Interval> convert(List<Conversion> conversions, List<Interval> intervals, String to) {
        List<Interval> newIntervals = newArrayList();

        intervals.forEach(interval -> {
            ArrayList<Interval> result = newArrayList();
            breakDownInterval(conversions, interval, result);
            newIntervals.addAll(result);
        });

        List<Interval> result = newArrayList();
        newIntervals.forEach(interval -> {
            Optional<Conversion> first = conversions.stream()
                    .filter(conversion -> conversion.start <= interval.start && conversion.end() >= interval.end())
                    .findFirst();

            if (first.isPresent()) {
                result.add(convertInterval(interval, first.get()));
            } else {
                result.add(interval.withType(to));
            }
        });

        return result;
    }

    private Interval convertInterval(Interval interval, Conversion conversion) {
        Long newIntervalStart = conversion.destination + interval.start - conversion.start;
        return interval.withType(conversion.type).withStart(newIntervalStart);
    }

    private void breakDownInterval(List<Conversion> conversions, Interval interval, List<Interval> result) {
        /*
            split interval at conversion start and end
        */

        List<Long> breakpoints = newArrayList();

        conversions.stream()
                .sorted(Comparator.comparing(o -> o.start))
                .filter(conversion -> interval.contains(conversion.start))
                .forEach(conversion -> breakpoints.add(conversion.start));
        conversions.stream()
                .sorted(Comparator.comparing(Conversion::end))
                .filter(conversion -> interval.contains(conversion.end()))
                .forEach(conversion -> breakpoints.add(conversion.end()));

        breakpoints.sort(Long::compare);

        AtomicBoolean requiresBreak = new AtomicBoolean(false);
        final Long[] prev = {interval.start};
        breakpoints.forEach(breakpoint -> {
            result.add(new Interval(interval.type, prev[0], breakpoint - prev[0]));
            prev[0] = breakpoint;
            requiresBreak.set(true);
        });

        if (!requiresBreak.get()) {
            result.add(interval);
        }

        log.info("Breakpoint done");
    }

    @With
    record Interval(String type, Long start, Long range) {
        boolean contains(Long point) {
            return point >= start && point < start + range;
        }

        Long end() {
            return start + range - 1;
        }
    }

    record Conversion(String type, Long destination, Long start, Long range) {
        Long end() {
            return start + range - 1;
        }

        @Override
        public String toString() {
            return start + "-" + end();
        }
    }
}
