package advent2020;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent22 {
    public static final Set<State> savedStates = newHashSet();
    private static int game = 1;

    public static void main(String[] args) {
        Queue<Integer> player1 = Util.fileStream("advent2020/advent22-p1")
                                     .map(Integer::parseInt)
                                     .collect(Collectors.toCollection(LinkedList::new));

        Queue<Integer> player2 = Util.fileStream("advent2020/advent22-p2")
                                     .map(Integer::parseInt)
                                     .collect(Collectors.toCollection(LinkedList::new));

        runGame(player1, player2);

        log.info("FINAL");
        log.info("Deck1: {}", player1);
        log.info("Deck2: {}", player2);

        long score;
        if (player1.size() == 0) {
            score = calculateScore(player2);
        } else {
            score = calculateScore(player1);
        }
        log.info("Score: {}", score);
    }

    private static void runGame(Queue<Integer> player1, Queue<Integer> player2) {
        int round = 1;
        while (player1.size() > 0 && player2.size() > 0) {
            log.info("Game: {} Round: {}", game, round++);
            log.info("Deck1: {}", player1);
            log.info("Deck2: {}", player2);
            State state = new State(player1, player2);

            int p1 = player1.poll();
            int p2 = player2.poll();

            if (savedStates.contains(state)) {
                player1.add(p1);
                player1.add(p2);
                return;
            } else {
                savedStates.add(state);
            }

            if (p1 <= player1.size() && p2 <= player2.size()) {
                Queue<Integer> newPlayer1 = newLinkedList(newArrayList(player1).subList(0, p1));
                Queue<Integer> newPlayer2 = newLinkedList(newArrayList(player2).subList(0, p2));
                game++;
                runGame(newPlayer1, newPlayer2);

                if (newPlayer1.size() == 0) {
                    player2.add(p2);
                    player2.add(p1);
                } else {
                    player1.add(p1);
                    player1.add(p2);
                }
            } else {
                if (player2won(p1, p2)) {
                    player2.add(p2);
                    player2.add(p1);
                } else {
                    player1.add(p1);
                    player1.add(p2);
                }
            }
        }
    }

    private static boolean player2won(int p1, int p2) {
        return p1 < p2;
    }

    static long calculateScore(Queue<Integer> player) {
        int sum = 0;
        int i = player.size();
        Integer poll;
        while ((poll = player.poll()) != null) {
            sum += poll * i--;
        }
        return sum;
    }
}

class State {
    Queue<Integer> p1;
    Queue<Integer> p2;

    public State(Queue<Integer> p1, Queue<Integer> p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        State state = (State) o;
        return Objects.equal(p1, state.p1) && Objects.equal(p2, state.p2);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(p1, p2);
    }
}
