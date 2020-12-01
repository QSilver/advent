package advent2019;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent7 {
    public static void main(String[] args) {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent2019/advent7"))
                                      .stream()
                                      .map(Long::parseLong)
                                      .collect(Collectors.toCollection(Lists::newArrayList));

        ArrayList<Long> outputs = newArrayList();

        List<List<Integer>> phasePermutations = listPermutations(newArrayList(5, 6, 7, 8, 9));

        for (List<Integer> permutation : phasePermutations) {
            log.info("{}", permutation);
            Computer amplifier1 = new Computer(program);
            Computer amplifier2 = new Computer(program);
            Computer amplifier3 = new Computer(program);
            Computer amplifier4 = new Computer(program);
            Computer amplifier5 = new Computer(program);

            amplifier1.addInput(permutation.get(0));
            amplifier2.addInput(permutation.get(1));
            amplifier3.addInput(permutation.get(2));
            amplifier4.addInput(permutation.get(3));
            amplifier5.addInput(permutation.get(4));

            long amp1out_amp2in;
            long amp2out_amp3in;
            long amp3out_amp4in;
            long amp4out_amp5in;
            long amp5out_amp1in = 0;

            while (amplifier1.isRunning() && amplifier2.isRunning() && amplifier3.isRunning() && amplifier4.isRunning() && amplifier5.isRunning()) {
                amp1out_amp2in = runAmplifier(amplifier1, amp5out_amp1in);
                amp2out_amp3in = runAmplifier(amplifier2, amp1out_amp2in);
                amp3out_amp4in = runAmplifier(amplifier3, amp2out_amp3in);
                amp4out_amp5in = runAmplifier(amplifier4, amp3out_amp4in);
                amp5out_amp1in = runAmplifier(amplifier5, amp4out_amp5in);
            }

            outputs.add(amp5out_amp1in);
        }

        OptionalInt max = outputs.stream()
                                 .mapToInt(Math::toIntExact)
                                 .max();
        log.info("Max output = {}", max.isPresent() ? max.getAsInt() : 0);
    }

    private static long runAmplifier(Computer amplifier, long input) {
        amplifier.addInput(input);
        amplifier.solve();
        return amplifier.getOutput();
    }

    private static List<List<Integer>> listPermutations(List<Integer> list) {
        if (list.size() == 0) {
            List<List<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }

        List<List<Integer>> returnMe = new ArrayList<>();
        Integer firstElement = list.remove(0);

        List<List<Integer>> recursiveReturn = listPermutations(list);
        for (List<Integer> li : recursiveReturn) {
            for (int index = 0; index <= li.size(); index++) {
                List<Integer> temp = new ArrayList<>(li);
                temp.add(index, firstElement);
                returnMe.add(temp);
            }
        }
        return returnMe;
    }
}