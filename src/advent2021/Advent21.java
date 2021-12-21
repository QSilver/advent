package advent2021;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;

@Slf4j
public class Advent21 {
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

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<String> inputs = Util.fileStream("advent2021/advent21")
                                  .toList();

        int pos1 = Integer.parseInt(inputs.get(0)
                                          .split(" ")[4]);
        int pos2 = Integer.parseInt(inputs.get(1)
                                          .split(" ")[4]);

        ScorePair dive = dive(new GameNode(0, 0, pos1, pos2));
        log.info("P2: {}", Math.max(dive.score1, dive.score2));
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    static LoadingCache<GameNode, ScorePair> cache = CacheBuilder.newBuilder()
                                                                 .build(CacheLoader.from(Advent21::dive));

    private static ScorePair dive(GameNode node) {
        if (node.current_score >= 21) {
            return new ScorePair(1, 0);
        }
        if (node.other_score >= 21) {
            return new ScorePair(0, 1);
        }

        long score1 = 0;
        long score2 = 0;
        for (int d = 3; d <= 9; d++) {
            int new_pos = MOVES[node.current_pos - 1][d - 3];
            int new_score = node.current_score + new_pos;

            GameNode newNode = new GameNode(node.other_score, new_score, node.other_pos, new_pos);
            ScorePair newGame = cache.getUnchecked(newNode);
            score2 += newGame.score1 * OCCUR[d - 3];
            score1 += newGame.score2 * OCCUR[d - 3];
        }
        return new ScorePair(score1, score2);
    }

    private record ScorePair(long score1, long score2) {
    }

    private record GameNode(int current_score, int other_score, int current_pos, int other_pos) {

    }
}
