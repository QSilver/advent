package advent2025;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static util.InputUtils.fileStream;
import static util.Util.generateCombinations;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent10 {
    // https://adventofcode.com/2025/day/10

    public Long runP1(String file) {
        Stream<String> stringStream = fileStream(file);

        List<Machine> machines = newArrayList();

        stringStream.forEach(line -> {
            String[] split = line.split(" ");

            List<Boolean> lights = newArrayList();
            for (int c = 1; c < split[0].length() - 1; c++) {
                lights.add(split[0].charAt(c) == '#');
            }

            List<Button> buttons = newArrayList();
            for (int s = 1; s < split.length - 1; s++) {
                List<Integer> list = split[s]
                        .substring(1, split[s].length() - 1)
                        .split(",").stream()
                        .map(Integer::parseInt)
                        .toList();
                buttons.add(new Button(list));
            }

            // TODO - joltage
            machines.add(new Machine(lights, buttons, 0));
        });

        AtomicLong buttonPresses = new AtomicLong(0L);
        machines.forEach(machine -> {
            int combinationSize = 0;

            while (true) {
                List<List<Button>> lists = generateCombinations(combinationSize, machine.buttons, true);

                for (List<Button> combination : lists) {
                    List<Boolean> lightsState = newArrayList(machine.lights);
                    combination.forEach(button -> button.positions
                            .forEach(pos -> lightsState
                                    .set(pos, !lightsState.get(pos))));

                    boolean allOn = lightsState.stream().allMatch(light -> light);

                    if (allOn) {
                        log.info("For Machine [{}] Found solution with {} button presses", machine, combination.size());
                        buttonPresses.addAndGet(combinationSize);
                        return;
                    }
                }

                if (combinationSize > 20) {
                    log.info("No solution found");
                    break;
                } else {
                    combinationSize++;
                }
            }
        });

        return buttonPresses.get();
    }

    public Long runP2(String file) {
        Stream<String> stringStream = fileStream(file);

        return 0L;
    }

    record Machine(List<Boolean> lights, List<Button> buttons, int joltage) {
        @Override
        public String toString() {
            return lights.stream()
                    .map(light -> light ? "#" : ".")
                    .reduce("", String::concat);
        }
    }

    record Button(List<Integer> positions) {

    }
}
