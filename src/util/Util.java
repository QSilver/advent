package util;

import com.google.common.collect.Lists;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.IntStream.range;

@Slf4j
public
class Util {
    public static Set<Integer> rangeToSet(int startInclusive, int endExclusive) {
        return range(startInclusive, endExclusive).boxed().collect(Collectors.toSet());
    }

    public static boolean doubleIsZero(double val, double precision) {
        return val >= -precision && val <= precision;
    }

    @With
    public record Point3D(long x, long y, long z) {
        public Point3D cross(Point3D other) {
            return new Point3D(
                    this.y * other.z - other.y * this.z,
                    this.z * other.x - other.z * this.x,
                    this.x * other.y - other.x * this.y);
        }

        public Point3D minus(Point3D other) {
            return new Point3D(x - other.x, y - other.y, z - other.z);
        }
    }

    public static <T> List<List<T>> generateCombinations(int number, List<T> values) {
        List<List<T>> combinations = values.stream()
                .map(Lists::newArrayList)
                .collect(Collectors.toList());

        for (int i = 1; i < number; i++) {
            List<List<T>> newValues = newArrayList();
            for (List<T> combination : combinations) {
                for (T value : values) {
                    ArrayList<T> temp = newArrayList(combination);
                    temp.add(value);
                    newValues.add(temp);
                }
            }
            combinations = newValues;
        }

        return combinations;
    }

    public enum Direction {
        UP, RIGHT, DOWN, LEFT;

        public Direction clockwise() {
            Direction[] values = Direction.values();
            int next = (this.ordinal() + 1) % values.length;
            return values[next];
        }

        public Direction counterclockwise() {
            Direction[] values = Direction.values();
            int next = (this.ordinal() - 1) % values.length;
            return values[next];
        }
    }
}
