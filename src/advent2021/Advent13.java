package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent13 {
    public static void main(String[] args) {
        Set<Advent5.Point> points = Util.fileStream("advent2021/advent13-map")
                                        .map(s -> {
                                            String[] split = s.split(",");
                                            return new Advent5.Point(Integer.parseInt(split[1]), Integer.parseInt(split[0]));
                                        })
                                        .collect(Collectors.toSet());

        List<Fold> folds = Util.fileStream("advent2021/advent13-fold")
                               .map(s -> {
                                   String[] split = s.split(" ")[2].split("=");
                                   return new Fold(Integer.parseInt(split[1]), "y".equals(split[0]));
                               })
                               .collect(Collectors.toList());

        folds.forEach(fold -> {
            Set<Advent5.Point> toAdd = newHashSet();
            Set<Advent5.Point> toRemove = newHashSet();
            points.forEach(point -> {
                if (!fold.horizontal) {
                    if (point.Y > fold.pos) {
                        int delta = (point.Y - fold.pos) * 2;
                        toAdd.add(new Advent5.Point(point.X, point.Y - delta));
                        toRemove.add(point);
                    }
                } else {
                    if (point.X > fold.pos) {
                        int delta = (point.X - fold.pos) * 2;
                        toAdd.add(new Advent5.Point(point.X - delta, point.Y));
                        toRemove.add(point);
                    }
                }
            });
            points.removeAll(toRemove);
            points.addAll(toAdd);
            log.info("Points Left: {}", points.size());
        });

        int maxX = points.stream()
                         .mapToInt(point -> point.X)
                         .max()
                         .orElse(0);
        int maxY = points.stream()
                         .mapToInt(point -> point.Y)
                         .max()
                         .orElse(0);

        for (int i = 0; i <= maxX; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j <= maxY; j++) {
                if (points.contains(new Advent5.Point(i, j))) {
                    sb.append("X ");
                } else {
                    sb.append("  ");
                }
            }
            log.info("{}", sb);
        }
    }

    @AllArgsConstructor
    static class Fold {
        int pos;
        boolean horizontal;
    }
}
