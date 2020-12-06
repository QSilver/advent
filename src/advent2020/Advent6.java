package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Advent6 {
    static List<List<String>> forms = new ArrayList<>();

    public static void main(String[] args) {
        AtomicInteger counter = new AtomicInteger(0);
        Util.fileStream("advent2020/advent6")
            .forEach(s -> {
                if (s.isEmpty()) {
                    counter.incrementAndGet();
                } else {
                    if (counter.get() <= forms.size()) {
                        forms.add(new ArrayList<>());
                    }
                    forms.get(counter.get())
                         .add(s);
                }
            });
        forms.removeIf(strings -> strings.size() == 0);

        Long anyone = forms.stream()
                           .map(strings -> String.join("", strings))
                           .map(line -> line.chars()
                                            .distinct()
                                            .count())
                           .reduce(Long::sum)
                           .get();
        log.info("{}", anyone);

        Long everyone = forms.stream()
                             .map(strings -> {
                                 String join = String.join("", strings);
                                 return "abcdefghijklmnopqrstuvwxyz".chars()
                                                                    .mapToObj(c -> String.valueOf((char) c))
                                                                    .filter(value -> join.length() - join.replace(value, "")
                                                                                                         .length() == strings.size())
                                                                    .count();
                             })
                             .reduce(Long::sum)
                             .get();
        log.info("{}", everyone);
    }
}
