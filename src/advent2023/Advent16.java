package advent2023;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static advent2023.Advent16.Direction.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent16 {
    // https://adventofcode.com/2023/day/16
    Map<Key, List<Beam>> beamCache = newHashMap();
    static Map<Direction, Point> directionDelta = newHashMap();
    static Map<Pair<Direction, Character>, List<Direction>> directionRouting = newHashMap();

    static {
        directionDelta.put(UP, new Point(-1, 0));
        directionDelta.put(DOWN, new Point(+1, 0));
        directionDelta.put(LEFT, new Point(0, -1));
        directionDelta.put(RIGHT, new Point(0, +1));

        directionRouting.put(Pair.create(UP, '.'), newArrayList(UP));
        directionRouting.put(Pair.create(UP, '|'), newArrayList(UP));
        directionRouting.put(Pair.create(UP, '-'), newArrayList(LEFT, RIGHT));
        directionRouting.put(Pair.create(UP, '\\'), newArrayList(LEFT));
        directionRouting.put(Pair.create(UP, '/'), newArrayList(RIGHT));

        directionRouting.put(Pair.create(DOWN, '.'), newArrayList(DOWN));
        directionRouting.put(Pair.create(DOWN, '|'), newArrayList(DOWN));
        directionRouting.put(Pair.create(DOWN, '-'), newArrayList(LEFT, RIGHT));
        directionRouting.put(Pair.create(DOWN, '\\'), newArrayList(RIGHT));
        directionRouting.put(Pair.create(DOWN, '/'), newArrayList(LEFT));

        directionRouting.put(Pair.create(LEFT, '.'), newArrayList(LEFT));
        directionRouting.put(Pair.create(LEFT, '|'), newArrayList(UP, DOWN));
        directionRouting.put(Pair.create(LEFT, '-'), newArrayList(LEFT));
        directionRouting.put(Pair.create(LEFT, '\\'), newArrayList(UP));
        directionRouting.put(Pair.create(LEFT, '/'), newArrayList(DOWN));

        directionRouting.put(Pair.create(RIGHT, '.'), newArrayList(RIGHT));
        directionRouting.put(Pair.create(RIGHT, '|'), newArrayList(UP, DOWN));
        directionRouting.put(Pair.create(RIGHT, '-'), newArrayList(RIGHT));
        directionRouting.put(Pair.create(RIGHT, '\\'), newArrayList(DOWN));
        directionRouting.put(Pair.create(RIGHT, '/'), newArrayList(UP));
    }

    char[][] mirrorMap;

    public Long runP1(String file) {
        List<String> list = Util.fileStream(file).toList();

        mirrorMap = new char[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            for (int col = 0; col < list.get(row).length(); col++) {
                mirrorMap[row][col] = list.get(row).charAt(col);
            }
        }

        Beam starting = new Beam(newArrayList(new Point(0, -1)), RIGHT, false);
        return fireBeamAndGetEnergy(starting);
    }

    public Long runP2(String file) {
        List<String> list = Util.fileStream(file).toList();

        mirrorMap = new char[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            for (int col = 0; col < list.get(row).length(); col++) {
                mirrorMap[row][col] = list.get(row).charAt(col);
            }
        }

        long maxEnergy = 0;
        for (int row = 0; row < list.size(); row++) {
            Beam starting = new Beam(newArrayList(new Point(row, -1)), RIGHT, false);
            log.info("Firing beam from {},{} - {}", row, -1, RIGHT);
            long energy = fireBeamAndGetEnergy(starting);
            if (energy > maxEnergy) {
                maxEnergy = energy;
            }
        }
        for (int row = 0; row < list.size(); row++) {
            Beam starting = new Beam(newArrayList(new Point(row, list.getFirst().length())), LEFT, false);
            log.info("Firing beam from {},{} - {}", row, list.getFirst().length(), LEFT);
            long energy = fireBeamAndGetEnergy(starting);
            if (energy > maxEnergy) {
                maxEnergy = energy;
            }
        }
        for (int col = 0; col < list.getFirst().length(); col++) {
            Beam starting = new Beam(newArrayList(new Point(-1, col)), DOWN, false);
            log.info("Firing beam from {},{} - {}", -1, col, DOWN);
            long energy = fireBeamAndGetEnergy(starting);
            if (energy > maxEnergy) {
                maxEnergy = energy;
            }
        }
        for (int col = 0; col < list.getFirst().length(); col++) {
            Beam starting = new Beam(newArrayList(new Point(list.size(), col)), UP, false);
            log.info("Firing beam from {},{} - {}", list.size(), col, UP);
            long energy = fireBeamAndGetEnergy(starting);
            if (energy > maxEnergy) {
                maxEnergy = energy;
            }
        }

        return maxEnergy;
    }


    private long fireBeamAndGetEnergy(Beam starting) {
        beamCache.clear();
        List<Beam> beams = newArrayList(starting);
        Set<Point> energised = newHashSet();

        while (true) {
            List<Beam> notDone = beams.stream().filter(beam -> !beam.done()).toList();
            if (notDone.isEmpty()) {
                break;
            }
            beams = notDone.stream().flatMap(beam -> calculateBeam(beam).stream()).toList();

            energised.addAll(beams.stream().flatMap(beam -> beam.points.stream()).toList());
        }

        return energised.size() - 1;
    }

    List<Beam> calculateBeam(Beam incoming) {
        Key memoKey = new Key(incoming.points.getLast(), incoming.direction());
        if (beamCache.containsKey(memoKey)) {
            return newArrayList(new Beam(incoming.points, incoming.direction, true));
        }

        List<Beam> resulting = newArrayList();
        List<Point> oldPath = incoming.points;
        Point last = oldPath.getLast();

        final Point delta = directionDelta.get(incoming.direction);
        final Point newPoint = new Point(last.row + delta.row, last.col + delta.col);

        if (isInBounds(last, incoming.direction)) {
            directionRouting.get(Pair.create(incoming.direction, mirrorMap[newPoint.row][newPoint.col]))
                    .forEach(direction -> fireBeam(oldPath, newPoint, resulting, direction));
        } else {
            resulting.add(new Beam(oldPath, incoming.direction, true));
        }

        beamCache.put(memoKey, resulting);
        return resulting;
    }

    boolean isInBounds(Point last, Direction direction) {
        switch (direction) {
            case UP -> {
                return last.row > 0;
            }
            case DOWN -> {
                return last.row < mirrorMap.length - 1;
            }
            case LEFT -> {
                return last.col > 0;
            }
            case RIGHT -> {
                return last.col < mirrorMap[0].length - 1;
            }
        }
        return false;
    }

    private static void fireBeam(List<Point> oldPath, Point newPoint, List<Beam> resulting, Direction direction) {
        List<Point> newPath = newArrayList(oldPath);
        newPath.add(newPoint);
        resulting.add(new Beam(newPath, direction, false));
    }

    record Key(Point point, Direction direction) {

    }

    record Point(int row, int col) {

    }

    record Beam(List<Point> points, Direction direction, boolean done) {

    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}