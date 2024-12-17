package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Math.pow;
import static util.InputUtils.readDoubleNewlineBlocks;

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
        String[] strings = readDoubleNewlineBlocks(file);

        long A = parseLong(strings[0].split("\n")[0].split(" ")[2]);
        long B = parseLong(strings[0].split("\n")[1].split(" ")[2]);
        long C = parseLong(strings[0].split("\n")[2].split(" ")[2]);
        String[] program = strings[1].split(" ")[1].split(",");

        for (long i = (long) pow(8, 3); i < pow(8, 4); i += 1) {
            System.out.println(STR."\{i}: \{executeP2(program, i, B, C)}");
        }

        return -1L;
    }

    private static String executeP2(String[] program, long A, long B, long C) {
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
            B = (A % 8) ^ 1;
            B = (B ^ (long) (A / pow(2, B))) ^ 4;
            A = A / 8;
            output.append(STR."\{B % 8},");
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
                case 5 -> output.append(STR."\{getCombo(A, B, C, operand) % 8},");
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