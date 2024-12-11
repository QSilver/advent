package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent11 {
    // https://adventofcode.com/2024/day/11

    public Long runP1(String file) {
        List<Long> list = Arrays.stream(fileStream(file)
                        .collect(Collectors.joining())
                        .split(" "))
                .mapToLong(Long::parseLong).boxed()
                .collect(Collectors.toList());

        for (int blink = 1; blink <= 25; blink++) {
            List<Long> temp = newArrayList();

            list.forEach(value -> {
                if (value == 0) {
                    temp.add(1L);
                } else if (String.valueOf(value).length() % 2 == 0) {
                    temp.add(Long.parseLong(String.valueOf(value).substring(0, String.valueOf(value).length() / 2)));
                    temp.add(Long.parseLong(String.valueOf(value).substring(String.valueOf(value).length() / 2)));

                } else {
                    temp.add(value * 2024);
                }
            });

            list.clear();
            list.addAll(temp);
        }

        return (long) list.size();
    }

    public Long runP2(String file) {
        Map<Long, Long> numberCounts = newHashMap();

        Arrays.stream(fileStream(file)
                        .collect(Collectors.joining())
                        .split(" "))
                .mapToLong(Long::parseLong).boxed()
                .forEach(value -> {
                    if (numberCounts.get(value) != null) {
                        numberCounts.put(value, numberCounts.get(value) + 1);
                    } else {
                        numberCounts.put(value, 1L);
                    }
                });

        for (int blink = 1; blink <= 75; blink++) {
            Map<Long, Long> next = newHashMap();

            numberCounts.keySet().forEach(value -> {
                if (value == 0) {
                    next.merge(1L, numberCounts.get(value), Long::sum);
                } else if (String.valueOf(value).length() % 2 == 0) {
                    next.merge(Long.parseLong(String.valueOf(value).substring(0, String.valueOf(value).length() / 2)), numberCounts.get(value), Long::sum);
                    next.merge(Long.parseLong(String.valueOf(value).substring(String.valueOf(value).length() / 2)), numberCounts.get(value), Long::sum);
                } else {
                    next.merge(value * 2024, numberCounts.get(value), Long::sum);
                }
            });

            numberCounts.clear();
            numberCounts.putAll(next);
        }

        return numberCounts.values().stream().mapToLong(value -> value).sum();
    }
}
