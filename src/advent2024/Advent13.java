package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util.EQSystemResult;

import java.util.regex.Matcher;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static util.InputUtils.readDoubleNewlineBlocks;
import static util.Util.solve2System;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent13 {
    // https://adventofcode.com/2024/day/13

    private static long extra = 0L;

    public Long runP1(String file) {
        return run(file);
    }

    public Long runP2(String file) {
        extra = 10000000000000L;
        return run(file);
    }

    private static long run(String file) {
        return readDoubleNewlineBlocks(file).stream()
                .mapToLong(Advent13::calculateButtons)
                .sum();
    }

    private static long calculateButtons(String clawMachine) {
        Matcher matcher = compile("""
                Button A: X\\+(\\d+), Y\\+(\\d+)
                Button B: X\\+(\\d+), Y\\+(\\d+)
                Prize: X=(\\d+), Y=(\\d+)""")
                .matcher(clawMachine);
        matcher.find();

        long ax = parseInt(matcher.group(1));
        long ay = parseInt(matcher.group(2));
        long bx = parseInt(matcher.group(3));
        long by = parseInt(matcher.group(4));
        long px = parseInt(matcher.group(5)) + extra;
        long py = parseInt(matcher.group(6)) + extra;

        EQSystemResult result = solve2System(ax, ay, bx, by, px, py);
        if (!result.solvable()) {
            return 0;
        }
        return 3 * result.A() + result.B();
    }
}
