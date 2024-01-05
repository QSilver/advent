package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.stream.Collectors;

// 291856 - too high
@Slf4j
public class Advent8 {

    static int[][] trees;

    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2022/advent8").collect(Collectors.toList());
        trees = new int[input.size()][input.get(0).length()];

        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(i).length(); j++) {
                trees[i][j] = Integer.parseInt(input.get(i).charAt(j) + "");
            }
        }

        int visibleTrees = 0;
        int maxScenicScore = 0;
        for (int i = 0; i < trees.length; i++) {
            for (int j = 0; j < trees[i].length; j++) {
                int scenicScore = checkTreeVisible(i, j);
                if (scenicScore > maxScenicScore) {
                    maxScenicScore = scenicScore;
                }
                if (scenicScore != 0) {
                    visibleTrees++;
                }
            }
        }
        log.info("Visible Trees: {}", visibleTrees);
        log.info("Max Scenic Score: {}", maxScenicScore);
    }

    static int checkTreeVisible(int row, int col) {
        if (row == 0 || col == 0 || row == trees.length || col == trees[row].length) {
            return 0;
        }

        // up
        int visibleUp = 0;
        for (int i = row - 1; i >= 0; i--) {
            visibleUp++;
            if (trees[i][col] >= trees[row][col]) {
                break;
            }
        }

        // down
        int visibleDown = 0;
        for (int i = row + 1; i < trees.length; i++) {
            visibleDown++;
            if (trees[i][col] >= trees[row][col]) {
                break;
            }
        }

        // left
        int visibleLeft = 0;
        for (int j = col - 1; j >= 0; j--) {
            visibleLeft++;
            if (trees[row][j] >= trees[row][col]) {
                break;
            }
        }

        // right
        int visibleRight = 0;
        for (int j = col + 1; j < trees[row].length; j++) {
            visibleRight++;
            if (trees[row][j] >= trees[row][col]) {
                break;
            }
        }

        return visibleUp * visibleDown * visibleLeft * visibleRight;
    }
}
