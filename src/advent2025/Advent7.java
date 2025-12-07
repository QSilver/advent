package advent2025;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent7 {
    // https://adventofcode.com/2025/day/7

    public Long runP1(String file) {
        List<String> input = fileStream(file).toList();

        long count = 0;

        Set<Integer> beams = newHashSet();
        for (String line : input) {
            if (line.contains("S")) {
                beams.add(line.indexOf("S"));
            } else {
                if (line.contains("^")) {
                    int splitIndex = line.indexOf("^");
                    while (splitIndex >= 0) {
                        if (beams.remove(splitIndex)) {
                            beams.add(splitIndex - 1);
                            beams.add(splitIndex + 1);
                            count++;
                        }
                        splitIndex = line.indexOf("^", splitIndex + 1);
                    }
                }
            }
        }

        return count;
    }

    public Long runP2(String file) {
        List<String> input = fileStream(file).toList();

        Map<Integer, Long> beams = newHashMap();
        for (String line : input) {
            if (line.contains("S")) {
                beams.put(line.indexOf("S"), 1L);
            } else {
                Map<Integer, Long> newBeams = newHashMap();
                if (line.contains("^")) {
                    List<Integer> splitters = newArrayList();
                    int splitIndex = line.indexOf("^");
                    while (splitIndex >= 0) {
                        splitters.add(splitIndex);
                        splitIndex = line.indexOf("^", splitIndex + 1);
                    }

                    log.info(STR."Beams before split: \{beams}");
                    beams.forEach((beam, count) -> {
                        boolean split = false;
                        for (Integer splitter : splitters) {
                            if (Objects.equals(beam, splitter)) {
                                newBeams.merge(splitter - 1, count, Long::sum);
                                newBeams.merge(splitter + 1, count, Long::sum);
                                split = true;
                                break;
                            }
                        }
                        if (!split) {
                            newBeams.merge(beam, count, Long::sum);
                        }

                    });
                    beams = newBeams;
                }
            }
        }

        log.info(STR."Final beams: \{beams}");
        return beams
                .values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }
}
