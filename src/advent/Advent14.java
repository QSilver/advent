package advent;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static advent.Advent6.processNode;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

// 574594 - too high

@Slf4j
public class Advent14 {
    private static final String FUEL = "FUEL";
    private static final String ORE = "ORE";

    public static Map<String, ElementPair> globalElements = newHashMap();

    public static void main(String[] args) {
        log.info(ORE + " required {}", solve());
    }

    private static int solve() {
        List<Reaction> reactions = Util.fileStream("advent14")
                                       .map(Reaction::new)
                                       .collect(Collectors.toList());

        Set<Node> danglingRoots = newHashSet();
        Node root = new Node(ORE, null);
        Map<String, Reaction> inverse = newHashMap();
        reactions.forEach(reaction -> {
            inverse.put(reaction.output.element, reaction);
            globalElements.put(reaction.output.element, reaction.output);

            reaction.getInput(1)
                    .forEach(elementPair -> processNode(danglingRoots, root, new Node(reaction.output.element, elementPair.element), newHashSet()));
        });

        while (!danglingRoots.isEmpty()) {
            HashSet<Node> toRemove = newHashSet();
            danglingRoots.forEach(node -> processNode(danglingRoots, root, node, toRemove));
            danglingRoots.removeAll(toRemove);
        }

        globalElements.values()
                      .forEach(elementPair -> elementPair.depth = getMaxDepth(root, elementPair.element, 0));

        List<ElementPair> required = newArrayList();
        required.add(new ElementPair(1, FUEL, 0));

        do {
            List<ElementPair> toAdd = newArrayList();
            List<ElementPair> toRemove = newArrayList();
            List<ElementPair> finalRequired = required;
            required.stream()
                    .filter(elementPair -> elementPair.depth == finalRequired.stream()
                                                                             .mapToInt(elements -> elements.depth)
                                                                             .max()
                                                                             .getAsInt())
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
        } while (!containsOnlyPrimitives(required));

        AtomicInteger requiredOre = new AtomicInteger();
        required.forEach(elementPair -> {
            Reaction reaction = inverse.get(elementPair.element);
            int multiplier = (int) Math.ceil(elementPair.number / (1.0 * reaction.output.number));
            requiredOre.getAndAdd(reaction.getInput(multiplier)
                                          .get(0).number);
        });

        return requiredOre.get();
    }

    private static int getMaxDepth(Node root, String toFind, int depth) {
        if (root.name.equals(toFind)) {
            return depth;
        } else {
            OptionalInt max = root.children.stream()
                                           .map(child -> getMaxDepth(child, toFind, depth + 1))
                                           .mapToInt(value -> value)
                                           .max();
            return max.isPresent() ? max.getAsInt() : 0;
        }
    }

    private static boolean containsOnlyPrimitives(List<ElementPair> inList) {
        for (ElementPair elementPair : inList) {
            if (elementPair.depth > 1) {
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
                            .map(stringIntegerEntry -> new ElementPair(stringIntegerEntry.getValue(), stringIntegerEntry.getKey(), globalElements.get(stringIntegerEntry.getKey()).depth))
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
                           .map(element -> Advent14.globalElements.getOrDefault(element.split(" ")[1], new ElementPair(element, 0)))
                           .collect(Collectors.toList());
        this.output = new ElementPair(lineElements[1], 0);
    }

    public List<ElementPair> getInput(int multiplier) {
        return input.stream()
                    .map(elementPair -> new ElementPair(elementPair.number * multiplier, elementPair.element, elementPair.depth))
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
    int depth;

    public ElementPair(int number, String element, int depth) {
        this.number = number;
        this.element = element;
        this.depth = depth;
    }

    public ElementPair(String input, int depth) {
        String[] elementPair = input.split(" ");
        this.number = Integer.parseInt(elementPair[0]);
        this.element = elementPair[1];
        this.depth = depth;
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
        return number + " " + element + " d" + depth;
    }
}