package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent13 {
    // https://adventofcode.com/2024/day/13

    private static long extra = 0L;

    public Long runP1(String file) {
        return fileStream(file).collect(Collectors.joining("SEPARATOR")).split("SEPARATORSEPARATOR").stream()
                .mapToLong(Advent13::calculateButtons)
                .sum();
    }

    public Long runP2(String file) {
        extra = 10000000000000L;
        return fileStream(file).collect(Collectors.joining("SEPARATOR")).split("SEPARATORSEPARATOR").stream()
                .mapToLong(Advent13::calculateButtons)
                .sum();
    }

    private static long calculateButtons(String clawMachine) {
        Matcher matcher = compile("\\d+").matcher(clawMachine);
        matcher.find();
        long ax = parseInt(matcher.group());
        matcher.find();
        long ay = parseInt(matcher.group());
        matcher.find();
        long bx = parseInt(matcher.group());
        matcher.find();
        long by = parseInt(matcher.group());
        matcher.find();
        long px = parseInt(matcher.group()) + extra;
        matcher.find();
        long py = parseInt(matcher.group()) + extra;
        Result result = new Result(ax, ay, bx, by, px, py);

        /*
         ax A + bx B = px
         ay A + by B = py

         ax A = px - bx B
         A = (px - bx B) / ax

         ay (px - bx B) / ax + by B = py
         ay (px - bx B) + ax by B = py ax
         ay px - ay bx B + ax by B = py ax
         B (ax by - ay bx) = py ax - px ay
         B = (py ax - px ay) / (ax by - ay bx)
         */

        long B = (result.py() * result.ax() - result.px() * result.ay()) / (result.ax() * result.by() - result.ay() * result.bx());
        long A = (result.px() - result.bx() * B) / result.ax();

        if (result.ax() * A + result.bx() * B != result.px() || result.ay() * A + result.by() * B != result.py()) {
            return 0;
        }
        return A * 3 + B;
    }

    private record Result(long ax, long ay, long bx, long by, long px, long py) {
    }
}
