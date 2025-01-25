package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Long.toBinaryString;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.joining;
import static util.InputUtils.readDoubleNewlineBlocks;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent24 {
    // https://adventofcode.com/2024/day/24

    Map<String, Boolean> circuitMap = newHashMap();
    List<Gate> gates = newArrayList();

    public Long runP1(String file) {
        parseInput(file);
        runGates();
        return parseLong(parseGateOutput(), 2);
    }

    public String runP2(String file) {
        parseInput(file);

//        seedBinaryNumberAndRun();
//        printGateErrors();

        // swap fph and z15
        gates.remove(new Gate("ccp", "XOR", "hhw", "fph"));
        gates.add(new Gate("ccp", "XOR", "hhw", "z15"));
        gates.remove(new Gate("snp", "OR", "mnh", "z15"));
        gates.add(new Gate("snp", "OR", "mnh", "fph"));

        // swap z21 and gds
        gates.remove(new Gate("x21", "AND", "y21", "z21"));
        gates.add(new Gate("x21", "AND", "y21", "gds"));
        gates.remove(new Gate("nsp", "XOR", "tqh", "gds"));
        gates.add(new Gate("nsp", "XOR", "tqh", "z21"));

        // swap cqk and z34
        gates.remove(new Gate("ksm", "XOR", "fcv", "cqk"));
        gates.add(new Gate("ksm", "XOR", "fcv", "z34"));
        gates.remove(new Gate("ksm", "AND", "fcv", "z34"));
        gates.add(new Gate("ksm", "AND", "fcv", "cqk"));

        // swap wrk and jrs
        gates.remove(new Gate("y30", "AND", "x30", "wrk"));
        gates.add(new Gate("y30", "AND", "x30", "jrs"));
        gates.remove(new Gate("y30", "XOR", "x30", "jrs"));
        gates.add(new Gate("y30", "XOR", "x30", "wrk"));

        seedBinaryNumberAndRun();
        printGateErrors();

        return "";
    }

    private void seedBinaryNumberAndRun() {
        StringBuilder x = new StringBuilder("0".repeat(44));
        StringBuilder y = new StringBuilder("0".repeat(44));

        circuitMap.keySet().forEach(key -> {
            if (key.startsWith("x")) {
                int pos = parseInt(key.substring(1));
                x.replace(pos, pos + 1, circuitMap.get(key) ? "1" : "0");
            } else if (key.startsWith("y")) {
                int pos = parseInt(key.substring(1));
                y.replace(pos, pos + 1, circuitMap.get(key) ? "1" : "0");
            }
        });

        long xl = parseLong(x.reverse().toString(), 2);
        System.out.println(STR." \{x} + (\{xl})");
        long yl = parseLong(y.reverse().toString(), 2);
        System.out.println(STR." \{y}   (\{yl})");

        runGates();

        String output = parseGateOutput();
        System.out.println(STR."\{output}   (\{Long.parseLong(output, 2)} result)");
        long sum = xl + yl;
        System.out.println(STR."\{toBinaryString(sum)}   (\{sum} expected)");

        System.out.println();
    }

    Set<Gate> wrongGates = newHashSet();

    private void printGateErrors() {
        gates.forEach(gate -> {
            if (gate.hasInput("x00")) {
                return;
            }

            // https://upload.wikimedia.org/wikipedia/commons/5/57/Fulladder.gif
//            checkOR(gate);
//            checkAND(gate);
//            checkXOR(gate);
        });
        String output = wrongGates.stream().map(gate -> gate.output).sorted().collect(joining(","));
        System.out.println(output);
    }

    private void checkOR(Gate gate) {
        // OR gate should have 2 AND as input
        if (gate.symbol.equals("OR")) {
            List<Gate> inputGates = gates.stream()
                    .filter(other -> other.output.equals(gate.left) || other.output.equals(gate.right))
                    .filter(other -> !other.symbol.equals("AND")).toList();

            if (!inputGates.isEmpty()) {
                System.out.println(STR."OR gate does not have 2 AND inputs: \{gate}");
                System.out.println(STR."==================== WRONG GATE: \{inputGates.getFirst()}");
                wrongGates.add(inputGates.getFirst());
                System.out.println();
            }
        }
    }

    private void checkAND(Gate gate) {
        // if XY enter an AND gate, this should follow into another OR gate
        if (gate.symbol.equals("AND") && gate.hasInputsXY()) {
            List<Gate> or = gates.stream().filter(other -> other.hasInput(gate.output) && other.symbol.equals("OR")).toList();
            if (or.isEmpty()) {
                System.out.println(STR."in AND not connected to an OR: \{gate}");
                Gate downStream = gates.stream().filter(other -> other.hasInput(gate.output)).findFirst().orElse(null);
                System.out.println(downStream);
                System.out.println(STR."==================== WRONG GATE: \{gate}");
                wrongGates.add(gate);
                System.out.println();
            }
        }
    }

    private void checkXOR(Gate gate) {
        // if XY enters an XOR gate, this should follow into another XOR gate and into Z
        if (gate.symbol.equals("XOR") && gate.hasInputsXY()) {
            // get all XOR gates that have an XYinput XOR as input
            List<Gate> xor = gates.stream()
                    .filter(other -> other.hasInput(gate.output))
                    .filter(other -> other.symbol().equals("XOR")).toList();
            if (!xor.isEmpty() && !xor.getFirst().output.startsWith("z")) {
                // this XOR should go into Z
                System.out.println("in XOR connected to another XOR without Z output");
                System.out.println(gate);
                System.out.println(xor.getFirst());
                System.out.println(STR."==================== WRONG GATE: \{xor.getFirst()}");

                // the Z with the same number as the XY input must also be wrong
                Gate wrong = gates.stream().filter(other -> other.output.substring(1).equals(gate.left.substring(1))).findFirst().orElse(null);
                System.out.println(STR."==================== WRONG GATE: \{wrong}");
                wrongGates.add(xor.getFirst());
                wrongGates.add(wrong);
                System.out.println();
            }
        }
    }

    private String parseGateOutput() {
        return circuitMap.keySet().stream()
                .filter(key1 -> key1.startsWith("z"))
                .sorted(reverseOrder())
                .map(key -> circuitMap.get(key))
                .map(key -> key ? "1" : "0")
                .collect(joining());
    }

    private void runGates() {
        List<Gate> toRun = newArrayList(gates);
        while (!toRun.isEmpty()) {
            Gate toProcess = toRun.removeFirst();

            Boolean left = circuitMap.get(toProcess.left);
            Boolean right = circuitMap.get(toProcess.right);
            if (left != null && right != null) {
                switch (toProcess.symbol) {
                    case "AND" -> circuitMap.put(toProcess.output, left & right);
                    case "OR" -> circuitMap.put(toProcess.output, left | right);
                    case "XOR" -> circuitMap.put(toProcess.output, left ^ right);
                }
            } else {
                toRun.add(toProcess);
            }
        }
    }

    private void parseInput(String file) {
        String[] strings = readDoubleNewlineBlocks(file);
        strings[0].split("\n").stream()
                .forEach(wire -> {
                    String[] split = wire.split(": ");
                    circuitMap.put(split[0], split[1].equals("1"));
                });
        strings[1].split("\n").stream()
                .forEach(line -> {
                    String[] split = line.split(" ");
                    gates.add(new Gate(split[0], split[1], split[2], split[4]));
                });
    }

    record Gate(String left, String symbol, String right, String output) {
        public boolean hasInput(String input) {
            return input.equals(left) || input.equals(right);
        }

        public boolean hasInputsXY() {
            return (left.startsWith("x") && right.startsWith("y")) || (left.startsWith("y") && right.startsWith("x"));
        }
    }
}
