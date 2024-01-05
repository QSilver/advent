package advent2023;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent2 {
    // https://adventofcode.com/2023/day/2
    public Integer runP1(String file) {
        return run(file, r12g13b14());
    }

    public Integer runP2(String file) {
        return run(file, getRGBMult());
    }

    private static int run(String file, Function<Stats, Integer> RGBMult) {
        return fileStream(file)
                .map(Advent2::getGameStats)
                .map(RGBMult)
                .mapToInt(value -> value)
                .sum();
    }

    private static Function<Stats, Integer> r12g13b14() {
        return stats -> stats.maxR <= 12 && stats.maxG <= 13 && stats.maxB <= 14 ? stats.id : 0;
    }

    private static Function<Stats, Integer> getRGBMult() {
        return stats -> stats.maxR * stats.maxG * stats.maxB;
    }

    private static Stats getGameStats(String s) {
        String[] split = s.split(":");

        Stats stats = new Stats();
        for (String round : split[1].split(";")) {
            for (String result : round.split(",")) {
                updateRGBMax(result.trim().split(" "), stats);
            }
        }

        stats.id = parseInt(split[0].split(" ")[1]);
        return stats;
    }

    private static void updateRGBMax(String[] outcome, Stats stats) {
        int i = parseInt(outcome[0]);
        switch (outcome[1]) {
            case "red" -> {
                if (i > stats.maxR) {
                    stats.maxR = i;
                }
            }
            case "green" -> {
                if (i > stats.maxG) {
                    stats.maxG = i;
                }
            }
            case "blue" -> {
                if (i > stats.maxB) {
                    stats.maxB = i;
                }
            }
        }
    }

    @NoArgsConstructor
    static class Stats {
        int id;
        int maxR;
        int maxG;
        int maxB;
    }
}
