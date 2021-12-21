package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

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

    static Map<GameNode, Long> GAME_CACHE = newHashMap();

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<String> inputs = Util.fileStream("advent2021/advent21")
                                  .toList();

        int pos1 = Integer.parseInt(inputs.get(0)
                                          .split(" ")[4]);
        int pos2 = Integer.parseInt(inputs.get(1)
                                          .split(" ")[4]);

        //solveP1(pos1, pos2, p1_Score, p2_Score);

        GameNode node = new GameNode(new Player(0, pos1), new Player(0, pos2), true);
        GAME_CACHE.put(node, 1L);

        while (unfinishedGames()) {
            Map<GameNode, Long> temp = newHashMap();
            GAME_CACHE.keySet()
                      .forEach(game -> dive(game, temp));
            GAME_CACHE = temp;
        }
        log.info("P2: {}", getWinner());
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    private static long getWinner() {
        long p1_wins = GAME_CACHE.entrySet()
                                 .stream()
                                 .filter(entry -> entry.getKey().p1.score >= 21)
                                 .map(Map.Entry::getValue)
                                 .reduce(Long::sum)
                                 .orElse(-1L);

        long p2_wins = GAME_CACHE.entrySet()
                                 .stream()
                                 .filter(entry -> entry.getKey().p2.score >= 21)
                                 .map(Map.Entry::getValue)
                                 .reduce(Long::sum)
                                 .orElse(-1L);
        long winner = Math.max(p1_wins, p2_wins);
        log.info("P1 wins: {}", p1_wins);
        log.info("P2 wins: {}", p2_wins);
        return winner;
    }

    private static boolean unfinishedGames() {
        return GAME_CACHE.keySet()
                         .stream()
                         .anyMatch(game -> !game.isFinished());
    }

    private static void dive(GameNode node, Map<GameNode, Long> temp) {
        long count = GAME_CACHE.getOrDefault(node, 0L);
        if (node.isFinished()) {
            temp.merge(node, count, Long::sum);
            return;
        }
        for (int d = 3; d <= 9; d++) {
            temp.merge(node.move(d), count * OCCUR[d - 3], Long::sum);
        }
    }

    private record GameNode(Player p1, Player p2, boolean p1_turn) {
        public boolean isFinished() {
            return p1.score >= 21 || p2.score >= 21;
        }

        public GameNode move(int dice) {
            if (p1_turn) {
                return new GameNode(p1.move(dice), p2, false);
            }
            return new GameNode(p1, p2.move(dice), true);
        }
    }

    private record Player(int score, int pos) {
        public Player move(int dice) {
            var newPosition = MOVES[pos - 1][dice - 3];
            var newPoints = score + newPosition;
            return new Player(newPoints, newPosition);
        }
    }

    private static void solveP1(int p1_Pos, int p2_Pos, int p1_Score, int p2_Score) {
        int rolls = 1;
        while (p1_Score < 1000 && p2_Score < 1000) {
            int p1_rolledNumber = (3 * rolls + 3) % 10;
            p1_Pos = (p1_Pos + p1_rolledNumber) % 10 == 0 ? 10 : (p1_Pos + p1_rolledNumber) % 10;
            p1_Score += p1_Pos;
            log.info("P1 rolls {} and moves to {} for total score of {}", p1_rolledNumber, p1_Pos, p1_Score);

            rolls += 3;
            if (p1_Score < 1000) {
                int p2_rolledNumber = (3 * rolls + 3) % 10;
                p2_Pos = (p2_Pos + p2_rolledNumber) % 10 == 0 ? 10 : (p2_Pos + p2_rolledNumber) % 10;
                p2_Score += p2_Pos;
                log.info("P2 rolls {} and moves to {} for total score of {}", p2_rolledNumber, p2_Pos, p2_Score);
                rolls += 3;
            }
        }
        log.info("rolls {} score {}", rolls, p2_Score);
        log.info("P1: {}", (rolls - 1) * p2_Score);
    }
}
