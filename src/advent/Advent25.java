package advent;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Advent25 {
    public static void main(String[] args) throws IOException {
        solve();
    }

    private static void solve() throws IOException {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent25"))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toCollection(Lists::newArrayList));

        Computer mudDroid = new Computer(program);
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        mudDroid.solve();
        while (mudDroid.isRunning()) {
            while (mudDroid.hasOutput()) {
                System.out.print((char) mudDroid.getOutput());
            }

            String instruction = console.readLine();
            addInstruction(mudDroid, instruction);
            mudDroid.solve();
        }
    }

    private static void addInstruction(Computer robot, String instruction) {
        for (char c : instruction.toCharArray()) {
            robot.addInput(c);
        }
        robot.addInput('\n');
    }
}
