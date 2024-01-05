package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Advent6 {
    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2022/advent6").collect(Collectors.toList());
        String message = input.get(0);

        checkDistinct(4, message);
        checkDistinct(14, message);
    }

    private static void checkDistinct(int window, String message) {
        for (int i = window; i < message.length(); i++) {
            String substring = message.substring(i - window, i);
            if (substring.chars().distinct().count() == window) {
                log.info("{} distinct after: {}", window, i);
                break;
            }
        }
    }
}
