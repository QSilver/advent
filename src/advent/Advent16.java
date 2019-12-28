package advent;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent16 {
    private static final ArrayList<Integer> PATTERN = newArrayList(0, 1, 0, -1);
    private static final int MULTIPLIER = 10000;

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Integer> fileInput = Arrays.stream(Util.splitLine(Util.fileStream("advent16"))
                                                         .stream()
                                                         .findFirst()
                                                         .map(s -> s.split(""))
                                                         .stream()
                                                         .findFirst()
                                                         .get())
                                             .mapToInt(Integer::parseInt)
                                             .collect(ArrayList::new, List::add, List::addAll);

        ArrayList<Integer> input = newArrayList(fileInput);
        for (int phase = 1; phase <= 100; phase++) {
            input = fft(input);
        }
        System.out.println(input);
    }

    private static ArrayList<Integer> fft(ArrayList<Integer> input) {
        ArrayList<Integer> output = newArrayList();

        for (int i = 0; i < input.size(); i++) {
            ArrayList<Integer> patternForStep = newArrayList();
            addToList(patternForStep, PATTERN.get(0), i + 1);
            addToList(patternForStep, PATTERN.get(1), i + 1);
            addToList(patternForStep, PATTERN.get(2), i + 1);
            addToList(patternForStep, PATTERN.get(3), i + 1);

            int sum = 0;
            for (int j = 0; j < input.size(); j++) {
                sum += input.get(j) * patternForStep.get((j + 1) % patternForStep.size());
            }
            output.add(Math.abs(sum % 10));
        }
        return output;
    }

    private static void addToList(ArrayList<Integer> list, int element, int times) {
        for (int i = 1; i <= times; i++) {
            list.add(element);
        }
    }
}