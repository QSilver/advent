package advent2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.InputUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent17 {
    static int minX, maxX, minY, maxY;
    static Set<Pair<Integer, Integer>> solutions = newHashSet();

    public static void main(String[] args) {
        String collect = InputUtils.fileStream("advent2021/advent17")
                             .collect(Collectors.toList())
                             .get(0);

        String[] split = collect.split(" ");
        String[] x = split[2].split("=")[1].split("\\.\\.");
        minX = Integer.parseInt(x[0]);
        maxX = Integer.parseInt(x[1].replace(",", ""));
        String[] y = split[3].split("=")[1].split("\\.\\.");
        minY = Integer.parseInt(y[0]);
        maxY = Integer.parseInt(y[1].replace(",", ""));

        int maxY_velocity = Math.abs(minY) - 1;
        log.info("Max Height: {}", (Math.abs(minY) * maxY_velocity) / 2);

        int minX_velocity = (int) Math.ceil((Math.sqrt(8 * minX + 1) - 1) / 2);
        for (int i = minX_velocity; i <= maxX; i++) {
            for (int j = minY; j <= maxY_velocity; j++) {
                simulateThrow(i, j);
            }
        }
        log.info("Solutions: {}", solutions.size());
    }

    private static void simulateThrow(int i, int j) {
        int posX = 0, posY = 0;
        int velX = i, velY = j;
        while (posX <= maxX && posY >= minY) {
            if (minX <= posX && maxY >= posY) {
                solutions.add(Pair.create(i, j));
                break;
            }
            posX += velX;
            posY += velY;
            velX = velX > 0 ? velX - 1 : 0;
            velY -= 1;
        }
    }
}
