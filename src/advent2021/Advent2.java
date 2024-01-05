package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class Advent2 {
    public static void main(String[] args) {
        List<Move> moves = InputUtils.fileStream("advent2021/advent2")
                               .map(s -> {
                                   String[] s1 = s.split(" ");
                                   return new Move(s1[0], Integer.parseInt(s1[1]));
                               })
                               .collect(Collectors.toList());

        solve(moves);
    }

    private static void solve(List<Move> moves) {
        AtomicInteger depth = new AtomicInteger();
        AtomicInteger horizontal = new AtomicInteger();
        AtomicInteger aim = new AtomicInteger();

        moves.forEach(move -> {
            switch (move.direction) {
                case "down" -> aim.addAndGet(move.step);
                case "up" -> aim.addAndGet(-move.step);
                case "forward" -> {
                    horizontal.addAndGet(move.step);
                    depth.addAndGet(aim.get() * move.step);
                }
            }
        });
        log.info("Final: {}", depth.get() * horizontal.get());
    }

    @AllArgsConstructor
    static class Move {
        String direction;
        int step;
    }
}
