package advent2025;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static util.InputUtils.readDoubleNewlineBlocks;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent12 {
    // https://adventofcode.com/2025/day/12

    public Long runP1(String file) {
        String[] strings = readDoubleNewlineBlocks(file);
        return strings[strings.length - 1].lines()
                .map(Advent12::parseArea)
                .map(Area::doAllShapesFit)
                .filter(fits -> fits)
                .count();
    }

    private static Area parseArea(String line) {
        String[] split = line.split(":");
        List<Integer> shapeCounts = Arrays.stream(split[1].trim().split(" "))
                .map(Integer::parseInt)
                .toList();

        String[] size = split[0].split("x");
        return new Area(parseInt(size[0]), parseInt(size[1]), shapeCounts);
    }

    record Area(int width, int height, List<Integer> shapeCounts) {
        private boolean doAllShapesFit() {
            int shapesSpace = shapeCounts.stream()
                    .mapToInt(shape -> shape * 9)
                    .sum();
            return shapesSpace <= width * height;
        }
    }

    public Long runP2(String file) {
        // last star gg
        return 0L;
    }
}
