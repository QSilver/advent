package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Slf4j
public class Advent4 {
    // https://adventofcode.com/2023/day/4
    public Integer runP1(String file) {
        return InputUtils.fileStream(file)
                .mapToInt(this::getMatchesFromTicket)
                .map(operand -> operand == 0 ? 0 : (int) Math.pow(2, operand - 1))
                .sum();
    }

    public Integer runP2(String file) {
        List<Integer> list = InputUtils.fileStream(file)
                .map(this::getMatchesFromTicket)
                .collect(Collectors.toList());

        int[] counts = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            counts[i] = 1;
        }

        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 1; j <= list.get(i); j++) {
                counts[i + j] += (counts[i]);
            }
        }

        return stream(counts).sum();
    }

    private int getMatchesFromTicket(String line) {
        String[] split = line.split(": ")[1].split(" \\| ");
        Set<Integer> winningNumbers = getTicketNumbers(split[0].split(" "));
        Set<Integer> playerNumbers = getTicketNumbers(split[1].split(" "));

        return playerNumbers.stream()
                .mapToInt(integer -> winningNumbers.contains(integer) ? 1 : 0)
                .sum();
    }

    private Set<Integer> getTicketNumbers(String[] strings) {
        return stream(strings)
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }
}
