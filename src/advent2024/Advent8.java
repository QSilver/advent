package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util2D.Point2D;
import util.Util2D.PointWithLabel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.abs;
import static util.InputUtils.fileStream;
import static util.Util2D.get2DPointsIgnore;

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
        Map<Character, List<PointWithLabel>> pointWithLabels = get2DPointsIgnore(file, '.').stream()
                .collect(Collectors.groupingBy(PointWithLabel::label,
                        Collectors.mapping(pointWithLabel -> pointWithLabel, Collectors.toList())));

        Set<Point2D> antinodes = newHashSet();

        pointWithLabels.forEach((label, pointList) -> {
            for (int i = 0; i < pointList.size() - 1; i++) {
                for (int j = i + 1; j < pointList.size(); j++) {

                    Point2D first = pointList.get(i).point2D();
                    Point2D second = pointList.get(j).point2D();

                    int deltaRow = (int) abs(first.row() - second.row());
                    int deltaCol = (int) abs(first.col() - second.col());

                    List<Point2D> toAdd = newArrayList();
                    for (int resonance = (withResonance ? 0 : 1); resonance < (withResonance ? 100 : 2); resonance++) {
                        if (first.row() < second.row()) {
                            if (first.col() < second.col()) {
                                toAdd.add(new Point2D(first.row() - ((long) deltaRow * resonance), first.col() - ((long) deltaCol * resonance)));
                                toAdd.add(new Point2D(second.row() + ((long) deltaRow * resonance), second.col() + ((long) deltaCol * resonance)));
                            } else {
                                toAdd.add(new Point2D(first.row() - ((long) deltaRow * resonance), first.col() + ((long) deltaCol * resonance)));
                                toAdd.add(new Point2D(second.row() + ((long) deltaRow * resonance), second.col() - ((long) deltaCol * resonance)));
                            }
                        } else {
                            if (first.col() < second.col()) {
                                toAdd.add(new Point2D(first.row() + ((long) deltaRow * resonance), first.col() - ((long) deltaCol * resonance)));
                                toAdd.add(new Point2D(second.row() - ((long) deltaRow * resonance), second.col() + ((long) deltaCol * resonance)));
                            } else {
                                toAdd.add(new Point2D(first.row() + ((long) deltaRow * resonance), first.col() + ((long) deltaCol * resonance)));
                                toAdd.add(new Point2D(second.row() - ((long) deltaRow * resonance), second.col() - ((long) deltaCol * resonance)));
                            }
                        }
                    }

                    antinodes.addAll(toAdd);
                }
            }
        });

        int size = (int) fileStream(file).count();
        return antinodes.stream()
                .filter(point2D -> point2D.row() >= 0 && point2D.row() < size && point2D.col() >= 0 && point2D.col() < size)
                .count();
    }
}
