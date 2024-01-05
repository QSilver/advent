package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Character.isDigit;
import static java.lang.Math.max;
import static java.lang.Math.min;

@Slf4j
public class Advent3 {
    // https://adventofcode.com/2023/day/3
    List<String> map = newArrayList();

    public Integer runP1(String file) {
        map = InputUtils.fileStream(file).toList();

        int sum = 0;

        for (int i = 0; i < map.size(); i++) {
            String line = map.get(i);
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                if (isDigit(c)) {
                    int end = line.length() - j;
                    for (int k = j + 1; k < line.length(); k++) {
                        if (!isDigit(line.charAt(k))) {
                            end = k - j;
                            break;
                        }
                    }

                    String number = line.substring(j, j + end);
                    boolean adjacent = adjacent(i, j, line, end);

                    if (adjacent) {
                        sum += Integer.parseInt(number);
                    }

                    j += end - 1;
                }
            }
        }

        return sum;
    }

    public Integer runP2(String file) {
        map = InputUtils.fileStream(file).toList();

        List<Point> gears = newArrayList();
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).length(); j++) {
                if (map.get(i).charAt(j) == '*') {
                    gears.add(new Point(i, j));
                }
            }
        }

        return gears.stream().mapToInt(this::extractNumbers).sum();
    }

    private boolean adjacent(int i, int j, String line, int end) {
        boolean adj = false;
        int startOfLine = max(0, j - 1);
        int endOfLine = min(line.length(), j + end + 1);

        if (i > 0) {
            for (int test = startOfLine; test < endOfLine; test++) {
                char c1 = map.get(i - 1).charAt(test);
                if (c1 != '.') {
                    adj = true;
                    break;
                }
            }
        }
        if (j > 0) {
            char c1 = map.get(i).charAt(j - 1);
            if (c1 != '.') {
                adj = true;
            }
        }
        if (j + end < line.length() - 1) {
            char c1 = map.get(i).charAt(j + end);
            if (c1 != '.') {
                adj = true;
            }
        }
        if (i < map.size() - 1) {
            for (int test = startOfLine; test < endOfLine; test++) {
                char c1 = map.get(i + 1).charAt(test);
                if (c1 != '.') {
                    adj = true;
                    break;
                }
            }
        }
        return adj;
    }

    private int extractNumbers(Point gear) {
        int mul = 1;
        int verify = 0;

        int start = gear.col - 3;
        int end = gear.col + 1;


        for (int i = -1; i <= 1; i++) {
            for (int j = start; j <= end; j++) {

                String line = map.get(gear.row + i);
                char c = line.charAt(j);
                if (isDigit(c)) {
                    int endOfNumber = line.length() - j;
                    for (int k = j + 1; k < line.length(); k++) {
                        char ch = line.charAt(k);
                        if (!isDigit(ch)) {
                            endOfNumber = k - j;
                            break;
                        }
                    }

                    String number = line.substring(j, j + endOfNumber);
                    j += endOfNumber - 1;

                    if (j >= start + 2) {
                        verify++;
                        mul *= Integer.parseInt(number);
                    }
                }
            }
        }

        return verify == 2 ? mul : 0;
    }

    record Point(int row, int col) {
        @Override
        public String toString() {
            return row + "," + col;
        }
    }
}
