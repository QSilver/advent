package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;

@Slf4j
public class Advent21Dynamic {
    static final int[][] MOVES = {
            {4, 5, 6, 7, 8, 9, 10},
            {5, 6, 7, 8, 9, 10, 1},
            {6, 7, 8, 9, 10, 1, 2},
            {7, 8, 9, 10, 1, 2, 3},
            {8, 9, 10, 1, 2, 3, 4},
            {9, 10, 1, 2, 3, 4, 5},
            {10, 1, 2, 3, 4, 5, 6},
            {1, 2, 3, 4, 5, 6, 7},
            {2, 3, 4, 5, 6, 7, 8},
            {3, 4, 5, 6, 7, 8, 9}};
    static final int[] OCCUR = {1, 3, 6, 7, 6, 3, 1};

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        List<String> inputs = Util.fileStream("advent2021/advent21")
                                  .toList();

        int pos1 = Integer.parseInt(inputs.get(0)
                                          .split(" ")[4]);
        int pos2 = Integer.parseInt(inputs.get(1)
                                          .split(" ")[4]);

        long[][][][][] gameStates = new long[22][22][10][10][2];
        gameStates[0][0][pos1 - 1][pos2 - 1][0] = 1;

        long p1_wins = 0;
        long p2_wins = 0;

        for (int score1 = 0; score1 <= 20; score1++) {
            for (int score2 = 0; score2 <= 20; score2++) {
                for (int player1 = 0; player1 < 10; player1++) {
                    for (int player2 = 0; player2 < 10; player2++) {
                        for (int dice = 3; dice <= 9; dice++) {
                            int np1 = MOVES[player1][dice - 3] - 1;
                            int np2 = MOVES[player2][dice - 3] - 1;
                            if (score1 + np1 + 1 >= 21) {
                                p1_wins += OCCUR[dice - 3] * gameStates[score1][score1][player1][player2][0];
                            } else {
                                gameStates[score1 + np1 + 1][score2][np1][player2][1] += OCCUR[dice - 3] * gameStates[score1][score2][player1][player2][0];
                            }
                            if (score2 + np2 + 1 >= 21) {
                                p2_wins += OCCUR[dice - 3] * gameStates[score1][score2][player1][player2][1];
                            } else {
                                gameStates[score1][score2 + np2 + 1][player1][np2][0] += OCCUR[dice - 3] * gameStates[score1][score2][player1][player2][1];
                            }
                        }
                    }
                }
            }
        }
        log.info("P2: {}", Math.max(p1_wins, p2_wins));
        log.info("{} ms", System.currentTimeMillis() - start);
    }
}
