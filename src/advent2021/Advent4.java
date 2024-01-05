package advent2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.InputUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent4 {
    public static void main(String[] args) {
        List<String> collect = InputUtils.fileStream("advent2021/advent4")
                                   .collect(Collectors.toList());

        List<Integer> bingoNumbers = Arrays.stream(collect.get(0)
                                                          .split(","))
                                           .map(Integer::parseInt)
                                           .collect(Collectors.toList());
        collect.remove(0);

        List<BingoBoard> boards = setUpBoards(collect);

        bingoNumbers.forEach(bingoNumber -> boards.forEach(bingoBoard -> processNumberForBoard(bingoNumber, bingoBoard)));
    }

    private static List<BingoBoard> setUpBoards(List<String> collect) {
        List<BingoBoard> boards = newArrayList();
        AtomicInteger row = new AtomicInteger();
        collect.forEach(line -> {
            if (line.equals("")) {
                BingoBoard board = new BingoBoard();
                board.inPlay = true;
                boards.add(board);
                row.set(0);
            } else {
                AtomicInteger col = new AtomicInteger();
                Arrays.stream(line.replace("  ", " ")
                                  .split(" "))
                      .filter(s1 -> !s1.equals(""))
                      .map(Integer::parseInt)
                      .forEach(integer -> {
                          BingoBoard bingoBoard = boards.get(boards.size() - 1);
                          bingoBoard.board.put(integer, Pair.create(row.get(), col.getAndIncrement()));
                      });
                row.getAndIncrement();
                col.set(0);
            }
        });
        return boards;
    }

    private static void processNumberForBoard(Integer bingoNumber, BingoBoard bingoBoard) {
        if (!bingoBoard.inPlay) {
            return;
        }

        Pair<Integer, Integer> rowCol = bingoBoard.board.remove(bingoNumber);
        if (rowCol == null) {
            return;
        }

        bingoBoard.rowMatch[rowCol.getFirst()]++;
        bingoBoard.columnMatch[rowCol.getSecond()]++;

        if (bingoBoard.rowMatch[rowCol.getFirst()] == 5 || bingoBoard.columnMatch[rowCol.getSecond()] == 5) {
            log.info("BINGO");
            int sum = bingoBoard.board.keySet()
                                      .stream()
                                      .mapToInt(value -> value)
                                      .sum();
            log.info("Score: {}", sum * bingoNumber);
            bingoBoard.inPlay = false;
        }
    }

    static class BingoBoard {
        Map<Integer, Pair<Integer, Integer>> board = newHashMap();
        int[] rowMatch = new int[5];
        int[] columnMatch = new int[5];

        boolean inPlay;
    }
}
