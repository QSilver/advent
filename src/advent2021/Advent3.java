package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent3 {
    public static void main(String[] args) {
        List<String> collect = Util.fileStream("advent2021/advent3")
                                   .collect(Collectors.toList());

        int length = collect.get(0)
                            .length();

        StringBuilder stringBuilder = new StringBuilder();
        IntStream.range(0, length)
                 .forEach(value -> stringBuilder.append(getMostCommon(collect, value)));

        int gamma = Integer.parseInt(stringBuilder.toString(), 2);
        int epsilon = (int) Math.pow(2, length) - 1 - gamma;
        log.info("P1: {}", gamma * epsilon);

        String oxygen = getRating(collect, length, true);
        String carbon = getRating(collect, length, false);

        int oxygenN = Integer.parseInt(oxygen, 2);
        int carbonN = Integer.parseInt(carbon, 2);
        log.info("P2: {}", oxygenN * carbonN);
    }

    private static String getRating(List<String> input, int length, boolean mostCommon) {
        List<String> oxygen = newArrayList(input);
        for (int i = 0; i < length; i++) {
            int finalI = i;
            oxygen.removeIf(s -> {
                if (mostCommon) {
                    return s.charAt(finalI) != getMostCommon(oxygen, finalI).charAt(0);
                }
                return s.charAt(finalI) == getMostCommon(oxygen, finalI).charAt(0);
            });
            if (oxygen.size() == 1) {
                return oxygen.stream()
                             .iterator()
                             .next();
            }
        }
        return "";
    }

    private static String getMostCommon(List<String> input, Integer position) {
        long count = input.stream()
                          .map(s -> s.charAt(position) == '1')
                          .filter(b -> b)
                          .count();
        return (count >= (input.size() - count)) ? "1" : "0";
    }
}