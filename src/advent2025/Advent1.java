package advent2025;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent1 {
    // https://adventofcode.com/2025/day/1

    public Integer runP1(String file) {
        AtomicInteger acc = new AtomicInteger(50);

        return fileStream(file)
                .map(Advent1::parseRotation)
                .map(delta -> {
                    acc.set(acc.addAndGet(delta) % 100);
                    return acc.get() == 0 ? 1 : 0;
                })
                .reduce(0, Integer::sum);
    }

    private static int parseRotation(String line) {
        int sign = line.charAt(0) == 'R' ? 1 : -1;
        return sign * parseInt(line.substring(1));
    }

    public Integer runP2(String file) {
        AtomicInteger acc = new AtomicInteger(50);

        return fileStream(file)
                .map(Advent1::parseRotation)
                .map(delta -> {
                    int count = abs(delta) / 100;
                    delta %= 100;

                    boolean wasNegative = acc.get() <= 0;
                    acc.addAndGet(delta);

                    if (acc.get() >= 100) {
                        count++;
                    } else if (acc.get() <= 0) {
                        acc.set(100 + acc.get());
                        if (!wasNegative) {
                            count++;
                        }
                    }
                    acc.set(acc.get() % 100);
                    return count;
                })
                .reduce(0, Integer::sum);
    }
}
