package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;

@Slf4j
public class Advent9 {
    public int runP1(String file) {
        return run(file, false);
    }

    public int runP2(String file) {
        return run(file, true);
    }

    private int run(String file, boolean isNegative) {
        return Util.fileStream(file)
                .mapToInt(string -> {
                    int[] line = Arrays.stream(string.split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    if (isNegative) {
                        return getNegativeDelta(line);
                    }
                    return getDelta(line);
                })
                .sum();
    }

    int getDelta(int[] input) {
        int[] derivative = derivative(input);

        if (derivative[derivative.length - 1] != 0) {
            return input[input.length - 1] + getDelta(derivative);
        } else {
            return input[input.length - 1];
        }
    }

    int getNegativeDelta(int[] input) {
        int[] derivative = derivative(input);

        if (derivative[derivative.length - 1] != 0) {
            return input[0] - getNegativeDelta(derivative);
        } else {
            return input[0];
        }
    }

    int[] derivative(int[] input) {
        int[] result = new int[input.length - 1];

        for (int i = 0; i < input.length - 1; i++) {
            result[i] = input[i + 1] - input[i];
        }
        return result;
    }


}
