package advent2019;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static advent2019.Advent6.processNode;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent14 {
    private static final long ONE_TRILLION = 1000000000000L;
    private static final String FUEL = "FUEL";
    private static final String ORE = "ORE";

    public static Map<String, ElementPair> globalElements = newHashMap();
    public static Map<String, Reaction> reactionLookup = newHashMap();
    public static Map<String, Long> waste = newHashMap();

    public static void main(String[] args) {
        setUp();
        long oreFor1Fuel = oreForFuel(1);
        log.info(ORE + " required {}", oreFor1Fuel);

        long baseFuel = ONE_TRILLION / oreFor1Fuel;

        long prevFuel = 0;
        while (baseFuel != prevFuel) {
            long ore = oreForFuel(baseFuel);
            log.info("Iterating {} ORE {}", baseFuel, ore);
            if (ore < ONE_TRILLION) {
                prevFuel = baseFuel;
                baseFuel *= 2;
            } else {
                baseFuel = (prevFuel + baseFuel) / 2;
            }
        }

        log.info("prevFuel {} ORE {}", prevFuel, oreForFuel(prevFuel));
        log.info("baseFuel {} ORE {}", baseFuel, oreForFuel(baseFuel));
    }

    private static long oreForFuel(long fuelRequired) {
        waste = newHashMap();
        List<ElementPair> required = newArrayList();
        required.add(new ElementPair(fuelRequired, FUEL, 0));

        do {
            List<ElementPair> toAdd = newArrayList();
            List<ElementPair> toRemove = newArrayList();
            List<ElementPair> finalRequired = required;
            required.stream()
                    .filter(elementPair -> isMostComplexElement(finalRequired, elementPair))
                    .forEach(elementPair -> {
                        Reaction reaction = reactionLookup.get(elementPair.element);
                        long multiplier = getMultiplierAndTrackWaste(elementPair, reaction);
                        toAdd.addAll(reaction.getInput(multiplier));
                        toRemove.add(elementPair);
                    });
            required.removeAll(toRemove);
            required.addAll(toAdd);
            required.removeIf(elementPair -> elementPair.element.equals(ORE));

            required = collapseList(required);
        } while (!containsOnlyPrimitives(required));

        AtomicLong requiredOre = new AtomicLong();
        required.forEach(elementPair -> {
            Reaction reaction = reactionLookup.get(elementPair.element);
            long multiplier = getMultiplierAndTrackWaste(elementPair, reaction);
            requiredOre.getAndAdd(reaction.getInput(multiplier)
                                          .get(0).number);
        });

        return requiredOre.get();
    }

    private static boolean isMostComplexElement(List<ElementPair> finalRequired, ElementPair elementPair) {
        OptionalLong maxDepth = finalRequired.stream()
                                             .mapToLong(elements -> elements.depth)
                                             .max();
        return elementPair.depth == (maxDepth.isPresent() ? maxDepth.getAsLong() : 0);
    }

    private static long getMultiplierAndTrackWaste(ElementPair elementPair, Reaction reaction) {
        long wasteForElement = waste.getOrDefault(reaction.output.element, 0L);
        long multiplier = (long) Math.ceil((elementPair.number - wasteForElement) / (1.0 * reaction.output.number));
        long multiplierWithoutWaste = (long) Math.ceil(elementPair.number / (1.0 * reaction.output.number));
        long remainder = reaction.output.number * multiplier - elementPair.number;

        if (multiplierWithoutWaste < multiplier) {
            long diff = multiplier - multiplierWithoutWaste;
            waste.put(reaction.output.element, wasteForElement - diff * reaction.output.number);
        } else {
            waste.put(reaction.output.element, remainder + wasteForElement);
        }
        return multiplier;
    }

    private static void setUp() {
        List<Reaction> reactions = InputUtils.fileStream("advent2019/advent14")
                                       .map(Reaction::new)
                                       .collect(Collectors.toList());

        Set<Node> danglingRoots = newHashSet();
        Node root = new Node(ORE, null);
        reactions.forEach(reaction -> {
            reactionLookup.put(reaction.output.element, reaction);
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
    }

    private static long getMaxDepth(Node root, String toFind, long depth) {
        if (root.name.equals(toFind)) {
            return depth;
        } else {
            OptionalLong max = root.children.stream()
                                            .map(child -> getMaxDepth(child, toFind, depth + 1))
                                            .mapToLong(value -> value)
                                            .max();
            return max.isPresent() ? max.getAsLong() : 0;
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
        Map<String, Long> collapsingMap = newHashMap();
        inList.forEach(elementPair -> collapsingMap.put(elementPair.element, collapsingMap.getOrDefault(elementPair.element, 0L) + elementPair.number));
        return collapsingMap.entrySet()
                            .stream()
                            .map(stringIntegerEntry -> new ElementPair(stringIntegerEntry.getValue(), stringIntegerEntry.getKey(), globalElements.get(stringIntegerEntry.getKey()).depth))
                            .collect(Collectors.toList());
    }
}

class Reaction {
    private final List<ElementPair> input;
    ElementPair output;

    public Reaction(String line) {
        String[] lineElements = line.split(" => ");
        String[] elements = lineElements[0].split(", ");

        this.input = Arrays.stream(elements)
                           .map(element -> Advent14.globalElements.getOrDefault(element.split(" ")[1], new ElementPair(element, 0)))
                           .collect(Collectors.toList());
        this.output = new ElementPair(lineElements[1], 0);
    }

    public List<ElementPair> getInput(long multiplier) {
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
    long number;
    String element;
    long depth;

    public ElementPair(long number, String element, long depth) {
        this.number = number;
        this.element = element;
        this.depth = depth;
    }

    public ElementPair(String input, long depth) {
        String[] elementPair = input.split(" ");
        this.number = Long.parseLong(elementPair[0]);
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
