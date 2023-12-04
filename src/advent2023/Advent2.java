package advent2023;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

@Slf4j
public class Advent2 {

    public Integer runP1(String file) {
        return Util.fileStream(file)
                .map(Advent2::getGameStats)
                .map(stats -> stats.maxR <= 12 && stats.maxG <= 13 && stats.maxB <= 14 ? stats.id : 0)
                .mapToInt(value -> value)
                .sum();
    }

    public Integer runP2(String file) {
        return Util.fileStream(file)
                .map(Advent2::getGameStats)
                .map(stats -> stats.maxR * stats.maxG * stats.maxB)
                .mapToInt(value -> value)
                .sum();
    }

    private static Stats getGameStats(String s) {
        String[] split = s.split(":");

        Stats stats = new Stats();
        for (String round : split[1].split(";")) {
            String[] resultArray = round.split(",");
            for (String result : resultArray) {
                String[] outcome = result.trim().split(" ");
                final int i = Integer.parseInt(outcome[0]);

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
        }

        stats.id = Integer.parseInt(split[0].split(" ")[1]);
        return stats;
    }

    @NoArgsConstructor
    static class Stats {
        int id;
        int maxR;
        int maxG;
        int maxB;
    }
}
