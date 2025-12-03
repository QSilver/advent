package advent2025;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent3 {
    // https://adventofcode.com/2025/day/3

    public Long runP1(String file) {
        return fileStream(file)
                .mapToLong(line -> {
                    String[] split = line.split("");
                    int max1 = 0;
                    int max2 = 0;
                    int count = 0;

                    for (int i = 0; i < split.length - 1; i++) {
                        int val = parseInt(split[i]);
                        if (val > max1) {
                            max1 = val;
                            count = i;
                        }
                    }

                    for (int i = count + 1; i < split.length; i++) {
                        int val = parseInt(split[i]);
                        if (val > max2) {
                            max2 = val;
                        }
                    }

                    return (max1 * 10L + max2);
                }).sum();
    }

    public Long runP2(String file) {
        return fileStream(file)
                .mapToLong(line -> {
                    String[] split = line.split("");
                    int max[] = new int[12];
                    int count[] = new int[13];

                    for (int c = 0; c < 12; c++) {
                        max[c] = 0;
                        count[c] = -1;
                    }

                    for (int c = 0; c < 12; c++) {
                        for (int i = count[c] + 1; i < split.length - (11 - c); i++) {
                            int val = parseInt(split[i]);
                            if (val > max[c]) {
                                max[c] = val;
                                count[c + 1] = i;
                            }
                        }
                    }

                    return Long.parseLong(Arrays.stream(max)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.joining("")));
                }).sum();
    }
}
