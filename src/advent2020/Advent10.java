package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Advent10 {
    public static void main(String[] args) {
        List<Integer> collect = InputUtils.fileStream("advent2020/advent10")
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());

        collect.add(0);
        int phone = collect.stream()
                           .mapToInt(Integer::intValue)
                           .max()
                           .orElse(-1) + 3;
        collect.add(phone);
        collect = collect.stream()
                         .sorted()
                         .collect(Collectors.toList());

        int diff1 = 0;
        int diff3 = 0;
        for (int i = 0; i < collect.size() - 1; i++) {
            if (collect.get(i + 1) - collect.get(i) == 3) {
                diff3++;
            } else if (collect.get(i + 1) - collect.get(i) == 1) {
                diff1++;
            }
        }
        log.info("{}", diff1 * diff3);

        int prev3 = 0;
        List<List<Integer>> combinations = new ArrayList<>();
        for (int i = 0; i < collect.size() - 1; i++) {
            if (collect.get(i + 1) - collect.get(i) == 3) {
                combinations.add(new ArrayList<>(collect.subList(prev3, i + 1)));
                prev3 = i;
            }
        }

        long reduce = combinations.stream()
                                  .map(Advent10::countValidSubsets)
                                  .mapToLong(Number::longValue)
                                  .reduce(1, (left, right) -> left *= right);

        log.info("valid combinations {}", reduce);
    }

    private static long countValidSubsets(List<Integer> chunk) {
        if (chunk.size() >= 3) {
            return subsets(chunk.subList(1, chunk.size() - 1)).stream()
                                                              .map(subset -> isSubsetValid(chunk, subset))
                                                              .filter(aBoolean -> aBoolean)
                                                              .count();
        }
        return 1;
    }

    private static Boolean isSubsetValid(List<Integer> chunk, List<Integer> subset) {
        if (subset.size() == 0) {
            return chunk.get(0) >= chunk.get(chunk.size() - 1) - 3;
        }
        if (chunk.get(0) < subset.get(0) - 3) {
            return false;
        }
        if (chunk.get(chunk.size() - 1) > subset.get(subset.size() - 1) + 3) {
            return false;
        }
        for (int i = 0; i < chunk.size() - 1; i++) {
            if (chunk.get(i + 1) - chunk.get(i) > 3) {
                return false;
            }
        }
        return true;
    }

    static List<List<Integer>> subsets(List<Integer> input) {
        List<List<Integer>> list = new ArrayList<>();
        subsetsHelper(list, new ArrayList<>(), input, 0);
        return list;
    }

    static void subsetsHelper(List<List<Integer>> powerSet, List<Integer> resultList, List<Integer> input, int start) {
        powerSet.add(new ArrayList<>(resultList));
        for (int i = start; i < input.size(); i++) {
            resultList.add(input.get(i));
            subsetsHelper(powerSet, resultList, input, i + 1);
            resultList.remove(resultList.size() - 1);
        }
    }
}
