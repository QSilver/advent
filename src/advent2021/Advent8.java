package advent2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent8 {
    public static void main(String[] args) {
        List<Pair<List<String>, List<String>>> sequence = Util.fileStream("advent2021/advent8")
                                                              .map(s -> s.replace("|", "0")
                                                                         .split(" 0 "))
                                                              .map(stringStream -> {
                                                                  List<String> sortedInput = Stream.of(stringStream[0].split(" "))
                                                                                                   .sorted(Comparator.comparingInt(String::length))
                                                                                                   .map(s -> {
                                                                                                       char[] a = s.toCharArray();
                                                                                                       Arrays.sort(a);
                                                                                                       return new String(a);
                                                                                                   })
                                                                                                   .collect(Collectors.toList());
                                                                  List<String> digits = List.of(stringStream[1].split(" "));
                                                                  return Pair.create(sortedInput, digits);
                                                              })
                                                              .collect(Collectors.toList());

        Integer result = sequence.stream()
                                 .map(listListPair -> {
                                     List<String> first = listListPair.getFirst();
                                     String[] numbers = deduceDigits(first);

                                     List<String> second = listListPair.getSecond();
                                     String collect = second.stream()
                                                            .map(s -> match(numbers, s))
                                                            .collect(Collectors.joining());
                                     log.info("Number: {}", collect);
                                     return collect;
                                 })
                                 .map(Integer::parseInt)
                                 .mapToInt(value -> value)
                                 .sum();
        log.info("P2: {}", result);
    }

    private static String match(String[] numbers, String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        for (int i = 0; i <= 9; i++) {
            if (numbers[i].equals(new String(a))) {
                return i + "";
            }
        }
        return "X";
    }

    private static String[] deduceDigits(List<String> first) {
        String[] numbers = new String[10];

        numbers[1] = first.get(0);
        numbers[7] = first.get(1);
        numbers[4] = first.get(2);
        numbers[8] = first.get(9);

        Set<String> size6 = newHashSet(first.get(6), first.get(7), first.get(8));
        size6.forEach(s -> {
            if (contains(s, numbers[4])) {
                numbers[9] = s;
            }
        });
        size6.remove(numbers[9]);
        size6.forEach(s -> {
            if (!contains(s, numbers[1])) {
                numbers[6] = s;
            }
        });
        size6.remove(numbers[6]);
        numbers[0] = size6.iterator()
                          .next();

        Set<String> size5 = newHashSet(first.get(3), first.get(4), first.get(5));
        size5.forEach(s -> {
            if (contains(s, numbers[7])) {
                numbers[3] = s;
            }
        });
        size5.remove(numbers[3]);
        size5.forEach(s -> {
            if (contains(numbers[6], s)) {
                numbers[5] = s;
            }
        });
        size5.remove(numbers[5]);
        numbers[2] = size5.iterator()
                          .next();

        return numbers;
    }

    static boolean contains(String string, String substring) {
        int j = 0;
        for (int i = 0; i < string.length() && j < substring.length(); i++) {
            if (substring.charAt(j) == string.charAt(i)) {
                j++;
            }
        }
        return (j == substring.length());
    }

    // size 2 =   c  f  = 1
    // size 3 = a c  f  = 7
    // size 4 =  bcd f  = 4

    // size 5 = a cde g = 2
    // size 5 = a cd fg = 3 = contains 7
    // size 5 = ab d fg = 5 = is contained in 6

    // size 6 = abc efg = 0
    // size 6 = ab defg = 6 = doesn't contain 1
    // size 6 = abcd fg = 9 = contains 4

    // size 7 = abcdefg = 8
}