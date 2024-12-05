package advent2024;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent5 {
    // https://adventofcode.com/2024/day/5

    public Integer runP1(String file) {
        Map<String, List<String>> precedenceRules = newHashMap();

        List<String> updates = newArrayList();

        boolean parsingRules = true;
        List<String> input = fileStream(file).toList();
        for (String line : input) {
            if (line.isEmpty()) {
                parsingRules = false;
                continue;
            }

            if (parsingRules) {
                String[] split = line.split("\\|");
                if (!precedenceRules.containsKey(split[0])) {
                    precedenceRules.put(split[0], newArrayList());
                }
                precedenceRules.get(split[0]).add(split[1]);
            } else {
                updates.add(line);
            }
        }

        return updates.stream()
                .map(update -> update.split(","))
                .map(updateArray -> {
                    for (int index = 1; index < updateArray.length; index++) {
                        if (precedenceRules.containsKey(updateArray[index])) {
                            for (int v = 0; v < index; v++) {
                                if (precedenceRules.get(updateArray[index]).contains(updateArray[v])) {
                                    return 0;
                                }
                            }
                        }
                    }
                    return Integer.parseInt(updateArray[updateArray.length / 2]);
                })
                .mapToInt(value -> value)
                .sum();
    }

    public Integer runP2(String file) {
        Map<String, List<String>> precedenceRules = newHashMap();

        List<String> updates = newArrayList();

        boolean parsingRules = true;
        List<String> input = fileStream(file).toList();
        for (String line : input) {
            if (line.isEmpty()) {
                parsingRules = false;
                continue;
            }

            if (parsingRules) {
                String[] split = line.split("\\|");
                if (!precedenceRules.containsKey(split[0])) {
                    precedenceRules.put(split[0], newArrayList());
                }
                precedenceRules.get(split[0]).add(split[1]);
            } else {
                updates.add(line);
            }
        }

        List<String> incorrectlyOrderedUpdates = newArrayList();
        updates.stream()
                .map(update -> {
                    String[] updateArray = update.split(",");
                    for (int index = 1; index < updateArray.length; index++) {
                        if (precedenceRules.containsKey(updateArray[index])) {
                            for (int v = 0; v < index; v++) {
                                if (precedenceRules.get(updateArray[index]).contains(updateArray[v])) {
                                    incorrectlyOrderedUpdates.add(update);
                                    return 0;
                                }
                            }
                        }
                    }
                    return Integer.parseInt(updateArray[updateArray.length / 2]);
                })
                .mapToInt(value -> value)
                .sum();

        List<String> list = incorrectlyOrderedUpdates.stream()
                .map(update -> update.split(","))
                .map(updateArray -> Arrays.stream(updateArray)
                        .sorted((o1, o2) -> {
                            if (precedenceRules.containsKey(o1) && precedenceRules.get(o1).contains(o2)) {
                                return -1;
                            }
                            if (precedenceRules.containsKey(o2) && precedenceRules.get(o2).contains(o1)) {
                                return 1;
                            }
                            return 0;
                        }).collect(Collectors.joining(",")))
                .toList();
        return list.stream().mapToInt(value -> {
            String[] updateArray = value.split(",");
            return Integer.parseInt(updateArray[updateArray.length / 2]);
        }).sum();
    }
}
