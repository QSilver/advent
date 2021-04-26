package advent2015;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.stream.Collectors;

@Slf4j
public class Advent1 {

    public static void main(String[] args) {
        String s = Util.fileStream("advent2015/advent1")
                       .collect(Collectors.toList())
                       .get(0);

        log.info("{}", s.chars()
                        .filter(value -> value == '(')
                        .count() - s.chars()
                                    .filter(value -> value == ')')
                                    .count());
    }
}
