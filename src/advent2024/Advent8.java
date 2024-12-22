package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Point2D;
import util.Util2D.PointWithLabel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.*;
import static util.InputUtils.fileStream;
import static util.InputUtils.get2DPointsIgnore;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent8 {
    // https://adventofcode.com/2024/day/8

    public Long runP1(String file) {
        return run(file, false);
    }

    public Long runP2(String file) {
        return run(file, true);
    }

    private static long run(String file, boolean withResonance) {
        Set<Point2D> antinodes = newHashSet();

        Map<Character, List<Point2D>> antennasByFrequency = get2DPointsIgnore(file, '.').stream()
                .collect(groupingBy(PointWithLabel::label, mapping(PointWithLabel::point2D, toList())));

        antennasByFrequency.values().forEach((antennas) -> {
            // sort by row so we can ignore row comparison when calculating antinodes
            antennas = antennas.stream()
                    .sorted(Comparator.comparingLong(Point2D::row))
                    .toList();

            for (int first = 0; first < antennas.size() - 1; first++) {
                for (int second = first + 1; second < antennas.size(); second++) {
                    for (int resonance = (withResonance ? 0 : 1); resonance < (withResonance ? 100 : 2); resonance++) {
                        antinodes.addAll(getAntinodes(antennas.get(first), antennas.get(second), resonance));
                    }
                }
            }
        });

        int size = (int) fileStream(file).count();
        return antinodes.stream()
                .filter(point2D -> point2D.row() >= 0 && point2D.row() < size && point2D.col() >= 0 && point2D.col() < size)
                .count();
    }

    private static List<Point2D> getAntinodes(Point2D first, Point2D second, int resonance) {
        List<Point2D> toAdd = newArrayList();

        long deltaRow = abs(first.row() - second.row());
        long deltaCol = abs(first.col() - second.col());

        if (first.col() < second.col()) {
            toAdd.add(new Point2D(first.row() - (deltaRow * resonance), first.col() - (deltaCol * resonance)));
            toAdd.add(new Point2D(second.row() + (deltaRow * resonance), second.col() + (deltaCol * resonance)));
        } else {
            toAdd.add(new Point2D(first.row() - (deltaRow * resonance), first.col() + (deltaCol * resonance)));
            toAdd.add(new Point2D(second.row() + (deltaRow * resonance), second.col() - (deltaCol * resonance)));
        }
        return toAdd;
    }
}
