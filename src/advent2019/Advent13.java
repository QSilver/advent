package advent2019;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
public class Advent13 {
    private static final int SIZE_X = 37;
    private static final int SIZE_Y = 24;

    private static int[][] board = new int[SIZE_Y][SIZE_X];
    private static Point paddle = new Point(18, 22, 0);
    private static int score = 0;

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent13"))
                                      .stream()
                                      .map(Long::parseLong)
                                      .collect(Collectors.toCollection(Lists::newArrayList));
        program.add(0, 2L);
        program.remove(1);
        Computer game = new Computer(program);

        updateGameState(game);
        log.info("Blocks: {}", countBlocks());
        //draw();

        while (game.isRunning()) {
            game.addInput(movePaddle());
            updateGameState(game);
            //draw();
        }

        log.info("Score: {}", score);
    }

    private static int movePaddle() {
        int joystick = 0;
        Point ball = findBall();
        if (ball.x < paddle.x) {
            joystick -= 1;
            paddle.x--;
        } else if (ball.x > paddle.x) {
            joystick += 1;
            paddle.x++;
        }
        return joystick;
    }

    private static void updateGameState(Computer game) {
        game.solve();
        while (game.hasOutput()) {
            int x = (int) game.getOutput();
            int y = (int) game.getOutput();
            int tile_id = (int) game.getOutput();

            if (x == -1 && y == 0) {
                score = tile_id;
            } else {
                board[y][x] = tile_id;
            }
        }
    }

    @SneakyThrows
    private static Point findBall() {
        for (int y = 0; y < SIZE_Y; y++) {
            for (int x = 0; x < SIZE_X; x++) {
                if (board[y][x] == 4) {
                    return new Point(x, y, 0);
                }
            }
        }
        return new Point(0, 0, 0);
    }

    private static int countBlocks() {
        int blockCount = 0;
        for (int[] ints : board) {
            for (int i : ints) {
                if (i == 2) {
                    blockCount++;
                }
            }
        }
        return blockCount;
    }

    private static void draw() {
        for (int y = 0; y < SIZE_Y; y++) {
            for (int x = 0; x < SIZE_X; x++) {
                switch (board[y][x]) {
                    case 0:
                        System.out.print(" ");
                        break;
                    case 1:
                        System.out.print("▓");
                        break;
                    case 2:
                        System.out.print("#");
                        break;
                    case 3:
                        System.out.print("=");
                        break;
                    case 4:
                        System.out.print("o");
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            System.out.println();
        }
        Util.clearConsole();
    }
}
