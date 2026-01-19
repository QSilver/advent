package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Math.pow;
import static java.lang.String.join;
import static util.InputUtils.readDoubleNewlineBlocks;
import static util.Util.generateCombinations;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent17 {
    // https://adventofcode.com/2024/day/17

    public String runP1(String file) {
        String[] strings = readDoubleNewlineBlocks(file);

        long A = parseLong(strings[0].split("\n")[0].split(" ")[2]);
        long B = parseLong(strings[0].split("\n")[1].split(" ")[2]);
        long C = parseLong(strings[0].split("\n")[2].split(" ")[2]);
        String[] program = strings[1].split(" ")[1].split(",");

        return execute(program, A, B, C);
    }

    public Long runP2(String file) {
        List<List<String>> combinations = generateCombinations(5, newArrayList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), true);

        List<Long> valids = newArrayList();
        combinations.forEach(combination -> {
            long a1 = (long) (parseLong(join("", combination)) * 1e10);
            String output = executeP2(a1);
            if (output.endsWith("3,5,5,3,0")) {
                System.out.printf("%s = %s%n", a1, output);
                valids.add(a1);
            }
        });
        System.out.println();

        List<Long> valids2 = newArrayList();
        valids.forEach(a1 -> combinations.forEach(combination -> {
            long a2 = (long) (a1 + (parseLong(join("", combination)) * 1e5));

            String output = executeP2(a2);
            if (output.endsWith("4,1,4,0,3,5,5,3,0")) {
                System.out.printf("%s = %s%n", a2, output);
                valids2.add(a2);
            }
        }));
        System.out.println();

        List<Long> valids3 = newArrayList();
        valids2.forEach(a2 -> combinations.forEach(combination -> {
            long a3 = (long) (a2 + (parseLong(join("", combination)) * 1e0));

            String output = executeP2(a3);
            if (output.endsWith("2,4,1,1,7,5,4,4,1,4,0,3,5,5,3,0")) {
                System.out.printf("%s = %s%n", a3, output);
                valids3.add(a3);
            }
        }));
        System.out.println();

        return valids3.getFirst();
    }


    private static String executeP2(long A) {
        if (A == 0) return "";
        /*
            B = A % 8
            B = B ^ 1
            C = A / B
            B = B ^ C
            B = B ^ A
            A = A / 8
            out B
            jnz
         */
        StringBuilder output = new StringBuilder();
        while (A != 0) {
            long B = (A % 8) ^ 1;
            B = (B ^ (long) (A / pow(2, B))) ^ 4;
            A = A / 8;
            output.append(String.format("%s,", B % 8));
        }
        return output.substring(0, output.length() - 1);
    }

    private static String execute(String[] program, long A, long B, long C) {
        StringBuilder output = new StringBuilder();

        int instruction = 0;
        while (instruction < program.length) {
            int instr = parseInt(program[instruction++]);
            int operand = parseInt(program[instruction++]);

            switch (instr) {
                case 0 -> A /= (long) pow(2, getCombo(A, B, C, operand));
                case 1 -> B ^= operand;
                case 2 -> B = getCombo(A, B, C, operand) % 8;
                case 3 -> {
                    if (A != 0) {
                        instruction = operand;
                    }
                }
                case 4 -> B ^= C;
                case 5 -> output.append(String.format("%s,", getCombo(A, B, C, operand) % 8));
                case 6 -> B = A / (long) pow(2, getCombo(A, B, C, operand));
                case 7 -> C = A / (long) pow(2, getCombo(A, B, C, operand));
            }
        }

        return output.substring(0, output.length() - 1);
    }

    private static long getCombo(long A, long B, long C, int operand) {
        long combo = operand;
        if (combo == 4) {
            combo = A;
        } else if (combo == 5) {
            combo = B;
        } else if (combo == 6) {
            combo = C;
        }
        return combo;
    }
}