package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Long.parseLong;
import static org.springframework.data.util.StreamUtils.zip;
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
                .collect(Collectors.toMap(equation -> equation, equation -> generateCombinations(equation.operands.size() - 1, values, true)))
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
        AtomicLong runningTotal = new AtomicLong(operands.removeFirst());
        zip(operands.stream(), operators.stream(), (operand, operator) -> apply(operand, operator, runningTotal));
        return runningTotal.get();
    }

    private static int apply(Long operand, String operator, AtomicLong runningTotal) {
        switch (operator) {
            case "+" -> runningTotal.addAndGet(operand);
            case "*" -> runningTotal.updateAndGet(v -> v * operand);
            case "|" -> runningTotal.set(parseLong(String.format("%s%s", runningTotal, operand)));
        }
        return 0;
    }

    private record Equation(long result, List<Long> operands) {
        public static Equation parse(String line) {
            String[] split = line.split(": ");
            List<Long> operands = split[1].split(" ").stream().mapToLong(Long::parseLong).boxed().toList();
            return new Equation(parseLong(split[0]), operands);
        }
    }
}
