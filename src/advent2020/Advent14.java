package advent2020;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent14 {
    static Map<Long, Long> memory = newHashMap();

    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2020/advent14")
                                 .collect(Collectors.toList());
        part2(input);
    }

    private static void part1(List<String> input) {
        Mask mask = new Mask();
        input.forEach(s -> {
            String[] s1 = s.split(" ");
            if (s1[0].equals("mask")) {
                mask.mask0 = Long.parseLong(s1[2].replace("X", "1"), 2);
                mask.mask1 = Long.parseLong(s1[2].replace("X", "0"), 2);
            } else {
                long value = Long.parseLong(s1[2]);
                value = value & mask.mask0;
                value = value | mask.mask1;
                long address = Long.parseLong(s1[0].split("\\[")[1].split("]")[0]);
                memory.put(address, value);
            }
        });

        Long sum = memory.values()
                         .stream()
                         .reduce(Long::sum)
                         .get();
        log.info("Sum: {}", sum);
    }

    private static void part2(List<String> input) {
        AtomicReference<String> mask = new AtomicReference<>();
        input.forEach(s -> {
            String[] s1 = s.split(" ");
            if (s1[0].equals("mask")) {
                mask.set(s1[2]);
            } else {
                long count = mask.get()
                                 .chars()
                                 .filter(ch -> ch == 'X')
                                 .count();

                List<Long> addresses = newArrayList();
                for (long i = 0; i < Math.pow(2, count); i++) {
                    long address = Integer.parseInt(s1[0].split("\\[")[1].split("]")[0]);
                    address = address | Long.parseLong(mask.get()
                                                           .replace("X", "0"), 2);
                    addresses.add(maskAddress(mask, i, address));
                }
                addresses.forEach(address -> memory.put(address, Long.parseLong(s1[2])));
            }
        });

        Long sum = memory.values()
                         .stream()
                         .reduce(Long::sum)
                         .get();
        log.info("Sum: {}", sum);
    }

    private static long maskAddress(AtomicReference<String> mask, long i, long address) {
        long replacement = i;
        for (int j = mask.get()
                         .length() - 1; j >= 0; j--) {
            if (mask.get()
                    .charAt(j) == 'X') {
                address = changeBit(address, replacement % 2, 35 - j);
                replacement /= 2;
            }
        }
        return address;
    }

    private static long changeBit(long address, long replacement, long counter) {
        long temp = 1L << counter;
        return (address & ~temp) | ((replacement << counter) & temp);
    }
}

@Builder
@NoArgsConstructor
@AllArgsConstructor
class Mask {
    long mask0;
    long mask1;
}
