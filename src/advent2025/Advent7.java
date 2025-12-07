package advent2025;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent7 {
    // https://adventofcode.com/2025/day/7

    public Long runP1(String file) {
        List<String> input = fileStream(file).toList();

        AtomicLong splitCount = new AtomicLong();
        Set<Beam> beams = newHashSet();
        processBeams(input, beams, splitCount);

        return splitCount.get();
    }

    public Long runP2(String file) {
        List<String> input = fileStream(file).toList();

        AtomicLong splitCount = new AtomicLong();
        Set<Beam> beams = newHashSet();
        beams = processBeams(input, beams, splitCount);

        return beams.stream()
                .mapToLong(beam -> beam.count)
                .sum();
    }

    private static Set<Beam> processBeams(List<String> input, Set<Beam> beams, AtomicLong splitCount) {
        beams.add(new Beam(input.getFirst().indexOf("S"), 1L));

        for (String line : input) {
            Map<Integer, Long> newBeams = newHashMap();
            List<Integer> splitters = getSplitters(line);

            Integer splits = beams.stream()
                    .map(entry -> splitBeam(entry, splitters, newBeams))
                    .reduce(0, Integer::sum);

            splitCount.getAndAdd(splits);

            beams = newBeams.entrySet().stream()
                    .map(Beam::new)
                    .collect(toSet());
        }

        log.info("Total splits: {}", splitCount.get());
        return beams;
    }

    private static int splitBeam(Beam entry, List<Integer> splitters, Map<Integer, Long> newBeams) {
        int beamSplit = 0;
        boolean split = false;
        for (Integer splitter : splitters) {
            if (Objects.equals(entry.position, splitter)) {
                newBeams.merge(splitter - 1, entry.count, Long::sum);
                newBeams.merge(splitter + 1, entry.count, Long::sum);
                split = true;
                beamSplit++;
                break;
            }
        }
        if (!split) {
            newBeams.merge(entry.position, entry.count, Long::sum);
        }

        return beamSplit;
    }

    private static List<Integer> getSplitters(String line) {
        List<Integer> splitters = newArrayList();
        int splitIndex = line.indexOf("^");
        while (splitIndex >= 0) {
            splitters.add(splitIndex);
            splitIndex = line.indexOf("^", splitIndex + 1);
        }
        return splitters;
    }

    record Beam(int position, long count) {
        public Beam(Map.Entry<Integer, Long> entry) {
            this(entry.getKey(), entry.getValue());
        }
    }
}
