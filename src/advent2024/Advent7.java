package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static util.InputUtils.fileStream;
import static util.Util.generateCombinations;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent7 {
    // https://adventofcode.com/2024/day/7

    public Long runP1(String file) {
        return run(file, newArrayList("+", "*"));
    }

    public Long runP2(String file) {
        return run(file, newArrayList("+", "*", "|"));
    }

    private static long run(String file, ArrayList<String> values) {
        return fileStream(file)
                .map(Equation::parse)
                .collect(Collectors.toMap(equation -> equation, equation -> generateCombinations(equation.operands.size() - 1, values)))
                .entrySet().stream()
                .mapToLong(entry -> isEquationValid(entry.getKey(), entry.getValue()))
                .sum();
    }

    private static long isEquationValid(Equation equation, List<List<String>> operatorCombinations) {
        return operatorCombinations.stream()
                .map(operators -> evaluate(equation.operands, operators))
                .anyMatch(aLong -> aLong == equation.result())
                ? equation.result() : 0;
    }

    private static long evaluate(List<Long> operands, List<String> operators) {
        long runningTotal = operands.getFirst();
        for (int i = 0; i < operators.size(); i++) {
            if ("+".equals(operators.get(i))) {
                runningTotal += operands.get(i + 1);
            } else if ("*".equals(operators.get(i))) {
                runningTotal *= operands.get(i + 1);
            } else if ("|".equals(operators.get(i))) {
                runningTotal = Long.parseLong(STR."\{runningTotal}\{operands.get(i + 1)}");
            }
        }
        return runningTotal;
    }

    private record Equation(long result, List<Long> operands) {
        public static Equation parse(String line) {
            String[] split = line.split(": ");
            List<Long> operands = split[1].split(" ").stream().mapToLong(Long::parseLong).boxed().toList();
            return new Equation(Long.parseLong(split[0]), operands);
        }
    }
}
