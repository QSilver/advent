package advent2022;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.abs;

@Slf4j
public class Advent15 {
    public static final int MAX_COORDS = 4000000;

    public static void main(String[] args) {
        List<Sensor> sensors = InputUtils.fileStream("advent2022/advent15")
                .map(Sensor::new).collect(Collectors.toList());

        part1(sensors);
        part2(sensors);
    }

    private static void part2(List<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            long bDist = sensor.getBeaconDistance() + 1;

            Set<Point> perimeter = newHashSet();
            for (int i = 0; i <= bDist; i++) {
                perimeter.add(new Point(sensor.coords.down - bDist + i, sensor.coords.across + i));
                perimeter.add(new Point(sensor.coords.down - bDist + i, sensor.coords.across + i));
                perimeter.add(new Point(sensor.coords.down - bDist + i, sensor.coords.across + i));
                perimeter.add(new Point(sensor.coords.down - bDist + i, sensor.coords.across + i));
            }

            for (Point point : perimeter) {
                if (checkPointNotInsideSensors(sensors, point)) {
                    log.info("Found {},{} - {}", point.across, point.down, point.across * 4000000 + point.down);
                    return;
                }
            }
        }
    }

    private static boolean checkPointNotInsideSensors(List<Sensor> sensors, Point point) {
        if (point.across >= 0 && point.across <= MAX_COORDS && point.down >= 0 && point.down <= MAX_COORDS) {
            return sensors.stream().noneMatch(otherSensor -> otherSensor.contains(point));
        }
        return false;
    }

    private static void part1(List<Sensor> sensors) {
        Set<Point> coveredPoints = newHashSet();

        long lineOfInterest = 2000000;
        sensors.forEach(sensor -> {
            long beaconDistance = sensor.getBeaconDistance();
            long remainder = beaconDistance - abs(sensor.coords.down - lineOfInterest);

            for (int i = 0; i <= remainder; i++) {
                coveredPoints.add(new Point(lineOfInterest, sensor.coords.across - i));
                coveredPoints.add(new Point(lineOfInterest, sensor.coords.across + i));
            }
        });

        sensors.forEach(sensor -> coveredPoints.remove(sensor.beacon));
        log.info("Covered Points: {}", coveredPoints.size());
    }

    @AllArgsConstructor
    static class Sensor {
        Point coords;
        private Point beacon;

        public Sensor(String coords) {
            String[] split = coords.split(":");
            long sensorAcross = Long.parseLong(split[0].split("x=")[1].split(",")[0]);
            long sensorDown = Long.parseLong(split[0].split("y=")[1].split(":")[0]);
            this.coords = new Point(sensorDown, sensorAcross);

            long beaconAcross = Long.parseLong(split[1].split("x=")[1].split(",")[0]);
            long beaconDown = Long.parseLong(split[1].split("y=")[1]);
            this.beacon = new Point(beaconDown, beaconAcross);
        }

        long getBeaconDistance() {
            return abs(coords.down - beacon.down) + abs(coords.across - beacon.across);
        }

        boolean contains(Point other) {
            return abs(coords.down - other.down) + abs(coords.across - other.across) <= getBeaconDistance();
        }

        @Override
        public String toString() {
            return "S " + coords + ", B" + beacon;
        }
    }

    @AllArgsConstructor
    static class Point {
        long down;
        long across;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return down == point.down && across == point.across;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(down, across);
        }

        @Override
        public String toString() {
            return "{" + down + "," + across + "}";
        }
    }
}
