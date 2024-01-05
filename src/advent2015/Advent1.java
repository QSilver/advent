package advent2015;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

@Slf4j
public class Advent1 {
    public static void main(String[] args) {
        String s = InputUtils.fileStream("advent2015/advent1")
                .toList()
                .get(0);

        log.info("{}", s.chars()
                .filter(value -> value == '(')
                .count() - s.chars()
                .filter(value -> value == ')')
                .count());

        int step = 0;
        int floor = 0;
        for (int i = 1; i <= s.length(); i++) {
            if (s.charAt(i - 1) == '(') {
                floor++;
            } else {
                floor--;
            }
            if (floor < 0) {
                step = i;
                break;
            }
        }
        log.info("Step {}", step);
    }
}
