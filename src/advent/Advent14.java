package advent;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

// 749096 - too high

// TODO - need to start counting extras for each reaction

@Slf4j
public class Advent14 {
    private static final String FUEL = "FUEL";
    private static final String ORE = "ORE";

    public static Set<String> primitives = newHashSet();

    public static void main(String[] args) {
        log.info(ORE + " required={}", solve());
    }

    private static int solve() {
        List<Reaction> reactions = Util.fileStream("advent14")
                                       .map(Reaction::new)
                                       .collect(Collectors.toList());

        Map<String, Reaction> inverse = newHashMap();
        reactions.forEach(reaction -> inverse.put(reaction.output.element, reaction));

        List<ElementPair> required = newArrayList();
        required.add(new ElementPair(1, FUEL));

        do {
            List<ElementPair> toAdd = newArrayList();
            List<ElementPair> toRemove = newArrayList();
            required.stream()
                    .filter(elementPair -> !primitives.contains(elementPair.element))
                    .forEach(elementPair -> {
                        Reaction reaction = inverse.get(elementPair.element);
                        int multiplier = (int) Math.ceil(elementPair.number / (1.0 * reaction.output.number));
                        toAdd.addAll(reaction.getInput(multiplier));
                        toRemove.add(elementPair);
                    });
            required.removeAll(toRemove);
            required.addAll(toAdd);
            required.removeIf(elementPair -> elementPair.element.equals(ORE));

            required = collapseList(required);
        } while (!isPrimitiveList(required));

        AtomicInteger requiredOre = new AtomicInteger();
        required.forEach(elementPair -> {
            Reaction reaction = inverse.get(elementPair.element);
            int multiplier = (int) Math.ceil(elementPair.number / (1.0 * reaction.output.number));
            requiredOre.getAndAdd(reaction.getInput(multiplier)
                                          .get(0).number);
        });

        return requiredOre.get();
    }

    private static boolean isPrimitiveList(List<ElementPair> inList) {
        for (ElementPair elementPair : inList) {
            if (!primitives.contains(elementPair.element)) {
                return false;
            }
        }
        return true;
    }

    private static List<ElementPair> collapseList(List<ElementPair> inList) {
        Map<String, Integer> collapsingMap = newHashMap();
        inList.forEach(elementPair -> collapsingMap.put(elementPair.element, collapsingMap.getOrDefault(elementPair.element, 0) + elementPair.number));
        return collapsingMap.entrySet()
                            .stream()
                            .map(stringIntegerEntry -> new ElementPair(stringIntegerEntry.getValue(), stringIntegerEntry.getKey()))
                            .collect(Collectors.toList());
    }
}

class Reaction {
    private List<ElementPair> input;
    ElementPair output;

    public Reaction(String line) {
        String[] lineElements = line.split(" => ");
        String[] elements = lineElements[0].split(", ");

        this.input = Arrays.stream(elements)
                           .map(ElementPair::new)
                           .collect(Collectors.toList());
        this.output = new ElementPair(lineElements[1]);

        if (input.stream()
                 .anyMatch(elementPair -> elementPair.element.equals("ORE"))) {
            Advent14.primitives.add(output.element);
        }
    }

    public List<ElementPair> getInput(int multiplier) {
        return input.stream()
                    .map(elementPair -> new ElementPair(elementPair.number * multiplier, elementPair.element))
                    .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return input.stream()
                    .map(ElementPair::toString)
                    .collect(Collectors.joining(",")) + " => " + output.toString();
    }
}

class ElementPair {
    int number;
    String element;

    public ElementPair(int number, String element) {
        this.number = number;
        this.element = element;
    }

    public ElementPair(String input) {
        String[] elementPair = input.split(" ");
        this.number = Integer.parseInt(elementPair[0]);
        this.element = elementPair[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ElementPair that = (ElementPair) o;
        return number == that.number && Objects.equal(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number, element);
    }

    @Override
    public String toString() {
        return number + " " + element;
    }
}
