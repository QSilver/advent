package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Advent6 {
    // https://adventofcode.com/2023/day/6
    public Long runP1(String file) {
        List<String> list = Util.fileStream(file).toList();

        String timesString = list.get(0).split(":")[1];
        String distanceString = list.get(1).split(":")[1];

        List<String> times = Arrays.stream(timesString.split(" "))
                .filter(s -> !s.isBlank())
                .toList();

        List<String> distances = Arrays.stream(distanceString.split(" "))
                .filter(s -> !s.isBlank())
                .toList();

        long mult = 1;
        for (int race = 0; race < times.size(); race++) {
            long count = simulateHolding(times.get(race), distances.get(race));
            mult *= count;
        }

        return mult;
    }

    public Long runP2(String file) {
        List<String> list = Util.fileStream(file).toList();

        String timesString = list.get(0).split(":")[1];
        String distanceString = list.get(1).split(":")[1];

        return simulateHolding(timesString, distanceString);
    }

    // TODO - change this to be quadratic when I can be bothered
    private static long simulateHolding(String timesString, String distanceString) {
        long time = Long.parseLong(timesString.replace(" ", ""));
        long distance = Long.parseLong(distanceString.replace(" ", ""));
        long count = 0;

        for (int ms = 0; ms < time; ms++) {
            long timeLeft = time - ms;
            long travel = timeLeft * ms;
            if (travel > distance) {
                count++;
            }
        }

        return count;
    }
}
