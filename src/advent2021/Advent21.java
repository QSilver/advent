package advent2021;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class Advent21 {
    static final int[][] MOVES = {
            {0, 0, 0, 0, 0, 0, 0},
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
    private static final int TARGET = 21;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<String> inputs = Util.fileStream("advent2021/advent21")
                                  .toList();

        int pos1 = Integer.parseInt(inputs.get(0)
                                          .split(" ")[4]);
        int pos2 = Integer.parseInt(inputs.get(1)
                                          .split(" ")[4]);

        ScorePair finalScore = play(new GameNode(0, 0, pos1, pos2));
        log.info("P2: {}", finalScore.score1.max(finalScore.score2));
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    static final LoadingCache<GameNode, ScorePair> CACHE = CacheBuilder.newBuilder()
                                                                       .build(CacheLoader.from(Advent21::play));

    private static ScorePair play(GameNode node) {
        if (node.current_score >= TARGET) {
            return new ScorePair(new BigDecimal(1), new BigDecimal(0));
        }
        if (node.other_score >= TARGET) {
            return new ScorePair(new BigDecimal(0), new BigDecimal(1));
        }

        BigDecimal score1 = new BigDecimal(0);
        BigDecimal score2 = new BigDecimal(0);
        for (int d = 0; d <= 6; d++) {
            int new_pos = MOVES[node.current_pos][d];
            int new_score = node.current_score + new_pos;
            ScorePair newGame = CACHE.getUnchecked(new GameNode(node.other_score, new_score, node.other_pos, new_pos));
            BigDecimal multiplicand = BigDecimal.valueOf(OCCUR[d]);
            score2 = score2.add(newGame.score1.multiply(multiplicand));
            score1 = score1.add(newGame.score2.multiply(multiplicand));
        }
        return new ScorePair(score1, score2);
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    static class ScorePair {
        BigDecimal score1;
        BigDecimal score2;
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    static class GameNode {
        int current_score;
        int other_score;
        int current_pos;
        int other_pos;
    }
}
