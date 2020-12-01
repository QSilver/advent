package advent2019;

import com.google.common.collect.Lists;
import org.paukov.combinatorics3.Generator;
import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class Advent25 {
    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent2019/advent25"))
                                      .stream()
                                      .map(Long::parseLong)
                                      .collect(Collectors.toCollection(Lists::newArrayList));

        Computer mudDroid = new Computer(program);

        pickItemsAndGoToCheckpoint(mudDroid);
        dropAllItems(mudDroid);
        mudDroid.solve();

        List<List<Items>> lists = permuteItems();
        int permutation = 0;
        while (mudDroid.isRunning()) {
            while (mudDroid.hasOutput()) {
                System.out.print((char) mudDroid.getOutput());
            }

            dropAllItems(mudDroid);
            System.out.println(permutation + " : " + lists.get(permutation));
            generateCommands(mudDroid, lists.get(permutation++));
            mudDroid.solve();
        }

        while (mudDroid.hasOutput()) {
            System.out.print((char) mudDroid.getOutput());
        }
    }

    private static void dropAllItems(Computer mudDroid) {
        for (Items item : Items.values()) {
            addInstruction(mudDroid, "drop " + item.label);
        }
    }

    private static void generateCommands(Computer computer, List<Items> itemsNeeded) {
        for (Items item : itemsNeeded) {
            addInstruction(computer, "take " + item.label);
        }
        addInstruction(computer, "west");
    }

    private static void addInstruction(Computer computer, String instruction) {
        for (char c : instruction.toCharArray()) {
            computer.addInput(c);
        }
        computer.addInput('\n');
    }

    private static List<List<Items>> permuteItems() {
        List<List<Items>> combinations = newArrayList();
        for (int items = 1; items <= Items.values().length; items++) {
            Generator.combination(Items.values())
                     .simple(items)
                     .forEach(combinations::add);
            System.out.println("Generating combinations of " + items);
        }

        return combinations;
    }

    private static void pickItemsAndGoToCheckpoint(Computer computer) {
        addInstruction(computer, "north");
        addInstruction(computer, "take dark matter");
        addInstruction(computer, "east");
        addInstruction(computer, "south");
        addInstruction(computer, "take dehydrated water");
        addInstruction(computer, "north");
        addInstruction(computer, "east");
        addInstruction(computer, "take bowl of rice");
        addInstruction(computer, "west");
        addInstruction(computer, "west");
        addInstruction(computer, "north");
        addInstruction(computer, "east");
        addInstruction(computer, "south");
        addInstruction(computer, "take antenna");
        addInstruction(computer, "west");
        addInstruction(computer, "take hypercube");
        addInstruction(computer, "east");
        addInstruction(computer, "north");
        addInstruction(computer, "west");
        addInstruction(computer, "north");
        addInstruction(computer, "take manifold");
        addInstruction(computer, "west");
        addInstruction(computer, "take jam");
        addInstruction(computer, "east");
        addInstruction(computer, "east");
        addInstruction(computer, "take candy cane");
        addInstruction(computer, "west");
        addInstruction(computer, "south");
        addInstruction(computer, "south");
        addInstruction(computer, "south");
        addInstruction(computer, "west");
        addInstruction(computer, "south");
        addInstruction(computer, "west");
        addInstruction(computer, "inv");
    }
}

enum Items {
    dark_matter("dark matter"),
    dehydrated_water("dehydrated water"),
    bowl_of_rice("bowl of rice"),
    antenna("antenna"),
    hypercube("hypercube"),
    manifold("manifold"),
    jam("jam"),
    candy_cane("candy cane");

    public final String label;

    Items(String label) {
        this.label = label;
    }
}