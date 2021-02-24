package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Advent13 {
    public static void main(String[] args) {
        List<String> input = Util.fileStream("advent2020/advent13")
                                 .collect(Collectors.toList());

        List<Integer> busses = Arrays.stream(input.get(1)
                                                  .split(","))
                                     .map(s -> {
                                         if (s.equals("x")) {
                                             return 0;
                                         }
                                         return Integer.parseInt(s);
                                     })
                                     .collect(Collectors.toList());
        int start = Integer.parseInt(input.get(0));
        part1(start, busses);
        part2(busses);
    }

    private static void part1(int start, List<Integer> input) {
        AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger selectedBus = new AtomicInteger();
        input.stream()
             .filter(integer -> integer != 0)
             .forEach(bus -> {
                 int closest = start / bus + 1;
                 if (closest * bus - start < min.get()) {
                     min.set(closest * bus - start);
                     selectedBus.set(bus);
                 }
             });

        log.info("Result: {}", selectedBus.get() * min.get());
    }

    private static void part2(List<Integer> input) {
        List<BigInteger> busses = input.stream()
                                       .map(BigInteger::valueOf)
                                       .collect(Collectors.toList());
        BigInteger primeProduct = busses.stream()
                                        .filter(i -> !i.equals(BigInteger.ZERO))
                                        .reduce((integer, integer2) -> integer.multiply(integer2))
                                        .get();
        log.info("Product: {}", primeProduct);

        BigInteger finalSum = IntStream.range(0, input.size())
                                       .filter(value -> !busses.get(value)
                                                               .equals(BigInteger.ZERO))
                                       .mapToObj(i -> {
                                           BigInteger n = busses.get(i);
                                           BigInteger partial = primeProduct.divide(n);
                                           return partial.multiply(partial.modInverse(n))
                                                         .multiply(n.subtract(BigInteger.valueOf(i)));
                                       })
                                       .reduce(BigInteger::add)
                                       .get();
        log.info("MinSum: {}", finalSum.mod(primeProduct));
    }
}
