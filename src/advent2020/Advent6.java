package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

@Slf4j
public class Advent6 {
    public static void main(String[] args) {
        Long anyone = Util.fileStream("advent2020/advent6")
                          .map(line -> line.replace(",", "")
                                           .chars()
                                           .distinct()
                                           .count())
                          .reduce(Long::sum)
                          .get();
        log.info("{}", anyone);

        Long everyone = Util.fileStream("advent2020/advent6")
                            .map(strings -> strings.chars()
                                                   .distinct()
                                                   .mapToObj(c -> String.valueOf((char) c))
                                                   .filter(value -> strings.replace(value, "")
                                                                           .length() == strings.replace(",", "")
                                                                                               .length() - 1)
                                                   .count())
                            .reduce(Long::sum)
                            .get();
        log.info("{}", everyone);
    }
}