package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Advent4 {

    public Integer runP1(String file) {
        return Util.fileStream(file)
                .mapToInt(Advent4::getMatchesFromTicket)
                .map(operand -> operand == 0 ? 0 : (int) Math.pow(2, operand - 1))
                .sum();
    }

    public Integer runP2(String file) {
        List<Integer> list = Util.fileStream(file)
                .map(Advent4::getMatchesFromTicket)
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

        return Arrays.stream(counts).sum();
    }

    private static int getMatchesFromTicket(String line) {
        String[] split = line.split(": ")[1].split(" \\| ");
        Set<Integer> winningNumbers = getTicketNumbers(split[0].split(" "));
        Set<Integer> playerNumbers = getTicketNumbers(split[1].split(" "));

        return playerNumbers.stream().mapToInt(integer -> winningNumbers.contains(integer) ? 1 : 0).sum();
    }

    private static Set<Integer> getTicketNumbers(String[] strings) {
        return Arrays.stream(strings)
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }
}
