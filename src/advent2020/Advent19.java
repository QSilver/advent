package advent2020;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent19 {
    private static final Map<Integer, Rule> ruleMap = newHashMap();

    public static void main(String[] args) {
        List<String> ruleInput = InputUtils.fileStream("advent2020/advent19-rules")
                                     .collect(Collectors.toList());

        List<String> textInput = InputUtils.fileStream("advent2020/advent19-text")
                                     .collect(Collectors.toList());

        List<Rule> rules = ruleInput.stream()
                                    .map(line -> line.split(":"))
                                    .map(split -> new Rule(Integer.parseInt(split[0]),
                                            split[1].replace("\"", "") + " ",
                                            split[1].contains("\"")))
                                    .collect(Collectors.toList());
        rules.forEach(rule -> ruleMap.put(rule.ruleNumber, rule));

        while (containsPrimitive() && ruleMap.size() != 1) {
            Rule primitive = getPrimitive();
            ruleMap.values()
                   .forEach(rule -> replaceRule(primitive, rule));
            ruleMap.remove(primitive.ruleNumber);
        }

        ruleMap.get(0).pattern = ruleMap.get(0).pattern.replace(" ", "");
        long count = textInput.stream()
                              .filter(s -> s.matches(ruleMap.get(0).pattern))
                              .count();
        log.info("Messages matching: {}", count);
    }

    private static void replaceRule(Rule r, Rule rule) {
        while (rule.pattern.contains(" " + r.ruleNumber + " ")) {
            rule.pattern = rule.pattern.replace(" " + r.ruleNumber + " ", " (" + r.pattern + ") ");
        }
        rule.isPrimitive = !rule.containsDigit();
    }

    private static boolean containsPrimitive() {
        return ruleMap.values()
                      .stream()
                      .anyMatch(rule -> rule.isPrimitive);
    }

    private static Rule getPrimitive() {
        return ruleMap.values()
                      .stream()
                      .filter(rule -> rule.isPrimitive)
                      .findFirst()
                      .orElse(null);
    }
}

@ToString
@AllArgsConstructor
class Rule {
    int ruleNumber;
    String pattern;
    boolean isPrimitive;

    public boolean containsDigit() {
        return this.pattern.matches(".*\\d.*");
    }
}
