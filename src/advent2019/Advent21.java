package advent2019;

import com.google.common.collect.Lists;
import util.Util;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Advent21 {
    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent21"))
                                      .stream()
                                      .map(Long::parseLong)
                                      .collect(Collectors.toCollection(Lists::newArrayList));

        Computer springDroid = new Computer(program);
        generateInput(springDroid);

        springDroid.solve();

        while (springDroid.hasOutput()) {
            long output = springDroid.getOutput();
            if (output < 255) {
                System.out.print((char) output);
            } else {
                System.out.print(output);
            }
        }
    }

    private static void generateInput(Computer robot) {
        addInstruction(robot, "OR B J");
        addInstruction(robot, "AND C J");
        addInstruction(robot, "NOT J J");
        addInstruction(robot, "AND D J");
        addInstruction(robot, "AND H J");
        addInstruction(robot, "NOT A T");
        addInstruction(robot, "OR T J");
        addInstruction(robot, "RUN");
    }

    private static void addInstruction(Computer robot, String instruction) {
        for (char c : instruction.toCharArray()) {
            robot.addInput(c);
        }
        robot.addInput('\n');
    }
}
