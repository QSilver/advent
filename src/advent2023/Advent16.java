package advent2023;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util2D.Point2D;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static advent2023.Advent16.Direction.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.loadCharMatrix;

@Slf4j
public class Advent16 {
    // https://adventofcode.com/2023/day/16
    Map<Key, List<Beam>> beamCache = newHashMap();
    static Map<Direction, Point2D> directionDelta = newHashMap();
    static Map<Pair<Direction, Character>, List<Direction>> directionRouting = newHashMap();

    static {
        directionDelta.put(UP, new Point2D(-1, 0));
        directionDelta.put(DOWN, new Point2D(+1, 0));
        directionDelta.put(LEFT, new Point2D(0, -1));
        directionDelta.put(RIGHT, new Point2D(0, +1));

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

    Character[][] mirrorMap;

    public Long runP1(String file) {
        mirrorMap = loadCharMatrix(file);
        Beam starting = new Beam(newArrayList(new Point2D(0, -1)), RIGHT, false);
        return fireBeamAndGetEnergy(starting);
    }

    public Long runP2(String file) {
        mirrorMap = loadCharMatrix(file);

        long maxEnergy = 0;
        for (int row = 0; row < mirrorMap.length; row++) {
            Beam starting = new Beam(newArrayList(new Point2D(row, -1)), RIGHT, false);
            log.info("Firing beam from {},{} - {}", row, -1, RIGHT);
            long energy = fireBeamAndGetEnergy(starting);
            if (energy > maxEnergy) {
                maxEnergy = energy;
            }
        }
        for (int row = 0; row < mirrorMap.length; row++) {
            Beam starting = new Beam(newArrayList(new Point2D(row, mirrorMap[0].length)), LEFT, false);
            log.info("Firing beam from {},{} - {}", row, mirrorMap[0].length, LEFT);
            long energy = fireBeamAndGetEnergy(starting);
            if (energy > maxEnergy) {
                maxEnergy = energy;
            }
        }
        for (int col = 0; col < mirrorMap[0].length; col++) {
            Beam starting = new Beam(newArrayList(new Point2D(-1, col)), DOWN, false);
            log.info("Firing beam from {},{} - {}", -1, col, DOWN);
            long energy = fireBeamAndGetEnergy(starting);
            if (energy > maxEnergy) {
                maxEnergy = energy;
            }
        }
        for (int col = 0; col < mirrorMap[0].length; col++) {
            Beam starting = new Beam(newArrayList(new Point2D(mirrorMap.length, col)), UP, false);
            log.info("Firing beam from {},{} - {}", mirrorMap.length, col, UP);
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
        Set<Point2D> energised = newHashSet();

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
        Direction from = incoming.direction;

        if (beamCache.containsKey(memoKey)) {
            return newArrayList(new Beam(incoming.points, from, true));
        }

        List<Beam> resulting = newArrayList();
        List<Point2D> oldPath = incoming.points;
        Point2D last = oldPath.getLast();

        final Point2D delta = directionDelta.get(from);
        final Point2D newPoint = new Point2D(last.row() + delta.row(), last.col() + delta.col());

        if (isInBounds(last, from)) {
            char encounters = mirrorMap[(int) newPoint.row()][(int) newPoint.col()];
            List<Direction> newDirections = directionRouting.get(Pair.create(from, encounters));
            newDirections.forEach(newDirection -> fireBeam(oldPath, newPoint, resulting, newDirection));
        } else {
            resulting.add(new Beam(oldPath, from, true));
        }

        beamCache.put(memoKey, resulting);
        return resulting;
    }

    boolean isInBounds(Point2D last, Direction direction) {
        switch (direction) {
            case UP -> {
                return last.row() > 0;
            }
            case DOWN -> {
                return last.row() < mirrorMap.length - 1;
            }
            case LEFT -> {
                return last.col() > 0;
            }
            case RIGHT -> {
                return last.col() < mirrorMap[0].length - 1;
            }
        }
        return false;
    }

    private static void fireBeam(List<Point2D> oldPath, Point2D newPoint, List<Beam> resulting, Direction direction) {
        List<Point2D> newPath = newArrayList(oldPath);
        newPath.add(newPoint);
        resulting.add(new Beam(newPath, direction, false));
    }

    record Key(Point2D point, Direction direction) {

    }

    record Beam(List<Point2D> points, Direction direction, boolean done) {

    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}