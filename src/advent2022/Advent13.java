package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent13 {
    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2022/advent13").collect(Collectors.toList());
        List<List> packets = newArrayList();

        int indexSum = 0;
        for (int i = 0; i < input.size(); i += 3) {
            List left = newArrayList();
            parseLine(left, input.get(i), 0);
            packets.add(left);

            List right = newArrayList();
            parseLine(right, input.get(i + 1), 0);
            packets.add(right);

            if (compareLists(left, right) < 0) {
                indexSum += (i / 3 + 1);
            }
        }

        log.info("Sum of indices: {}", indexSum);

        ArrayList<Integer> divider1 = newArrayList(newArrayList(2));
        packets.add(divider1);
        ArrayList<Integer> divider2 = newArrayList(newArrayList(6));
        packets.add(divider2);

        packets.sort(Advent13::compareLists);
        log.info("Decoder Key: {}", (packets.indexOf(divider1) + 1) * (packets.indexOf(divider2) + 1));
    }

    static int parseLine(List list, String line, int index) {
        for (int i = index; i < line.length(); i++) {
            if (line.charAt(i) == '[' && i != 0) {
                List inner = newArrayList();
                list.add(inner);
                i = parseLine(inner, line, i + 1);
            } else if (line.charAt(i) == ']') {
                return i + 1;
            } else if (Character.isDigit(line.charAt(i))) {
                int temp = i;
                while (Character.isDigit(line.charAt(temp))) {
                    temp++;
                }
                int value = Integer.parseInt(line.substring(i, temp));
                list.add(value);
                i += (temp - i) - 1;
            }
        }
        return line.length();
    }

    static int compareLists(List left, List right) {
        int subCompare = 0;
        for (int i = 0; i < Math.min(left.size(), right.size()) && subCompare == 0; i++) {
            if (left.get(i) instanceof List<?> && right.get(i) instanceof Integer) {
                subCompare = compareLists((List) left.get(i), newArrayList(right.get(i)));
            } else if (left.get(i) instanceof Integer && right.get(i) instanceof List<?>) {
                subCompare = compareLists(newArrayList(left.get(i)), (List) right.get(i));
            } else if (left.get(i) instanceof List<?> && right.get(i) instanceof List<?>) {
                subCompare = compareLists((List) left.get(i), (List) right.get(i));
            } else if (left.get(i) instanceof Integer && right.get(i) instanceof Integer) {
                subCompare = Integer.compare((Integer) left.get(i), (Integer) right.get(i));
            }
        }
        if (subCompare != 0) {
            return subCompare;
        }
        return Integer.compare(left.size(), right.size());
    }
}
