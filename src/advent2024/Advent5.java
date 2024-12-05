package advent2024;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent5 {
    // https://adventofcode.com/2024/day/5

    private static Map<String, List<String>> precedenceRules = newHashMap();

    public Integer runP1(String file) {
        return parseInput(file)
                .mapToInt(Advent5::calculateValidity)
                .sum();
    }

    public Integer runP2(String file) {
        return parseInput(file)
                .filter(updateArray -> calculateValidity(updateArray) == 0) // only get invalid updates
                .map(Advent5::sortUpdatePage)
                .mapToInt(Advent5::middle)
                .sum();
    }

    private static int calculateValidity(String[] updateArray) {
        for (int index = 1; index < updateArray.length; index++) {
            if (precedenceRules.containsKey(updateArray[index])) {
                for (int v = 0; v < index; v++) {
                    if (precedenceRules.get(updateArray[index]).contains(updateArray[v])) {
                        return 0;
                    }
                }
            }
        }
        return middle(updateArray);
    }

    private static String[] sortUpdatePage(String[] updateArray) {
        return Arrays.stream(updateArray)
                .sorted(updatePageComparator())
                .toArray(String[]::new);
    }

    private static Comparator<String> updatePageComparator() {
        return (o1, o2) -> {
            if (precedenceRules.containsKey(o1) && precedenceRules.get(o1).contains(o2)) {
                return -1;
            }
            if (precedenceRules.containsKey(o2) && precedenceRules.get(o2).contains(o1)) {
                return 1;
            }
            return 0;
        };
    }

    private static int middle(String[] updateArray) {
        return Integer.parseInt(updateArray[updateArray.length / 2]);
    }

    private static Stream<String[]> parseInput(String file) {
        precedenceRules = newHashMap();
        List<String[]> updates = newArrayList();

        boolean isParsingRules = true;
        List<String> input = fileStream(file).toList();
        for (String line : input) {
            if (line.isEmpty()) {
                isParsingRules = false;
                continue;
            }

            if (isParsingRules) {
                String[] split = line.split("\\|");
                if (!precedenceRules.containsKey(split[0])) {
                    precedenceRules.put(split[0], newArrayList());
                }
                precedenceRules.get(split[0]).add(split[1]);
            } else {
                updates.add(line.split(","));
            }
        }
        return updates.stream();
    }
}
