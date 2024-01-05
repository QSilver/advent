package advent2019;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Advent16 {
    private static final int MULTIPLIER = 10000;
    private static int offset = 0;

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Integer> fileInput = Arrays.stream(InputUtils.splitLine(InputUtils.fileStream("advent2019/advent16"))
                                                         .stream()
                                                         .findFirst()
                                                         .map(s -> s.split(""))
                                                         .stream()
                                                         .findFirst()
                                                         .get())
                                             .mapToInt(Integer::parseInt)
                                             .collect(ArrayList::new, List::add, List::addAll);

        int[] input = new int[fileInput.size() * MULTIPLIER];
        for (int i = 0; i < MULTIPLIER; i++) {
            for (int j = 0; j < fileInput.size(); j++) {
                input[fileInput.size() * i + j] = fileInput.get(j);
            }
        }

        for (int i = 0; i < 7; i++) {
            offset += Math.pow(10, 6 - i) * input[i];
        }

        for (int phase = 1; phase <= 100; phase++) {
            fft(input);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            stringBuilder.append(input[offset + i]);
        }
        log.info("Offset Output: {}", stringBuilder.toString());
    }

    private static void fft(int[] input) {
        for (int i = input.length - 2; i >= offset; i--) {
            input[i] = (input[i + 1] + input[i]) % 10;
        }
    }

}