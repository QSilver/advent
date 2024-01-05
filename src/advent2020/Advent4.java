package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent4 {
    public static void main(String[] args) {
        long count = countPassports(InputUtils.fileStream("advent2020/advent4"));
        log.info("Valid: {}", count);
    }

    private static long countPassports(Stream<String> input) {
        return input.map(Passport::new)
                    .filter(Passport::isValid)
                    .count();
    }
}

@Slf4j
class Passport {
    private static final String[] expected = {"byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"};

    Map<String, String> details = newHashMap();

    Passport(String input) {
        Arrays.stream(input.split(" "))
              .forEach(s -> {
                  String[] split = s.split(":");
                  details.put(split[0], split[1]);
              });
        log.info("Input: {}", input);
    }

    boolean isValid() {
        boolean b = details.keySet()
                           .containsAll(Arrays.asList(expected));
        try {
            int bry = Integer.parseInt(details.get("byr"));
            boolean b1 = bry >= 1920 && bry <= 2002;

            int iyr = Integer.parseInt(details.get("iyr"));
            boolean b2 = iyr >= 2010 && iyr <= 2020;

            int eyr = Integer.parseInt(details.get("eyr"));
            boolean b3 = eyr >= 2020 && eyr <= 2030;

            int hgt = details.get("hgt")
                             .contains("cm")
                    ? Integer.parseInt(details.get("hgt")
                                              .replace("c", "")
                                              .replace("m", ""))
                    : Integer.parseInt(details.get("hgt")
                                              .replace("i", "")
                                              .replace("n", ""));
            boolean b4 = details.get("hgt")
                                .contains("cm") ? hgt >= 150 && hgt <= 193 : hgt >= 59 && hgt <= 76;

            boolean hcl = "ghijklmnopqrstuvwxyz".chars()
                                                .mapToObj(c -> details.get("hcl")
                                                                      .contains(String.valueOf((char) c)))
                                                .reduce((left, right) -> left || right)
                                                .orElse(false);
            boolean b5 = details.get("hcl")
                                .charAt(0) == '#' && details.get("hcl")
                                                            .length() == 7 && !hcl;

            boolean b6 = newArrayList("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(details.get("ecl"));

            boolean b7 = Integer.parseInt(details.get("pid")) > 0 && details.get("pid")
                                                                            .length() == 9;

            log.info("byr:{}, iyr:{}, eyr:{}, hgt:{}, hcl:{}, ecl:{}, pid:{}", b1, b2, b3, b4, b5, b6, b7);
            return b && b1 && b2 && b3 && b4 && b5 && b6 && b7;
        } catch (Exception e) {
            return false;
        }
    }
}
