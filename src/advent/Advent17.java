package advent;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.stream.Collectors;

// 41315 - too low

@Slf4j
public class Advent17 {
    private static final int HEIGHT = 107;
    private static final int WIDTH = 43;

    private static char[][] map = new char[HEIGHT][WIDTH];

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent17"))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toCollection(Lists::newArrayList));

        program.add(0, 2L);
        program.remove(1);

        Computer vacuumRobot = new Computer(program);
        generateInput(vacuumRobot);

        vacuumRobot.solve();
        int i = 0;
        int j = 0;
        while (vacuumRobot.hasOutput()) {
            long output = vacuumRobot.getOutput();

            if (output == 35) {
                map[j][i++] = (char) output;
            } else if (output == 46) {
                map[j][i++] = (char) output;
            } else if (output == 10) {
                j++;
                i = 0;
            } else {
                if (output > 300) {
                    System.out.println("Score: " + output);
                } else {
                    map[j][i++] = (char) output;
                }
            }
        }

        int sum = 0;
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (isIntersection(row, col)) {
                    sum += row * col;
                    map[row][col] = 'O';
                }
            }
        }

        for (char[] chars : map) {
            System.out.println(chars);
        }

        System.out.println(sum);
    }

    private static boolean isIntersection(int row, int col) {
        if (row < 1 || col < 1 || col > WIDTH - 2 || row > HEIGHT - 2) {
            return false;
        }
        return map[row][col] == '#' && map[row - 1][col] == '#' && map[row + 1][col] == '#' && map[row][col - 1] == '#' && map[row][col + 1] == '#';
    }

    private static void generateInput(Computer robot) {
        robot.addInput('C');
        robot.addInput(',');
        robot.addInput('A');
        robot.addInput(',');
        robot.addInput('C');
        robot.addInput(',');
        robot.addInput('B');
        robot.addInput(',');
        robot.addInput('C');
        robot.addInput(',');
        robot.addInput('A');
        robot.addInput(',');
        robot.addInput('B');
        robot.addInput(',');
        robot.addInput('A');
        robot.addInput(',');
        robot.addInput('B');
        robot.addInput(',');
        robot.addInput('A');
        robot.addInput('\n');
        //===================
        robot.addInput('R');
        robot.addInput(',');
        robot.addInput('8');
        robot.addInput(',');
        robot.addInput('L');
        robot.addInput(',');
        robot.addInput('1');
        robot.addInput('2');
        robot.addInput(',');
        robot.addInput('R');
        robot.addInput(',');
        robot.addInput('4');
        robot.addInput(',');
        robot.addInput('R');
        robot.addInput(',');
        robot.addInput('4');
        robot.addInput('\n');
        //===================
        robot.addInput('R');
        robot.addInput(',');
        robot.addInput('8');
        robot.addInput(',');
        robot.addInput('L');
        robot.addInput(',');
        robot.addInput('1');
        robot.addInput('0');
        robot.addInput(',');
        robot.addInput('R');
        robot.addInput(',');
        robot.addInput('8');
        robot.addInput('\n');
        //===================
        robot.addInput('R');
        robot.addInput(',');
        robot.addInput('8');
        robot.addInput(',');
        robot.addInput('L');
        robot.addInput(',');
        robot.addInput('1');
        robot.addInput('0');
        robot.addInput(',');
        robot.addInput('L');
        robot.addInput(',');
        robot.addInput('1');
        robot.addInput('2');
        robot.addInput(',');
        robot.addInput('R');
        robot.addInput(',');
        robot.addInput('4');
        robot.addInput('\n');
        //===================
        robot.addInput('n');
        robot.addInput('\n');
    }
}

// R8 L10 L12 R4 R8 L12 R4 R4 R8 L10 L12 R4 R8 L10 R8 R8 L10 L12 R4 R8 L12 R4 R4 R8 L10 R8 R8 L12 R4 R4 R8 L10 R8 R8 L12 R4 R4
// C A C B C A B A B A

// A = R8 L12 R4 R4
// B = R8 L10 R8
// C = R8 L10 L12 R4