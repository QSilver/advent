package util;

import com.google.common.collect.Lists;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Long.parseLong;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.stream.IntStream.range;

@Slf4j
public class Util {
    public static <T> void forEachTwo(List<T> list, BiConsumer<T, T> consumer) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                consumer.accept(list.get(i), list.get(j));
            }
        }
    }

    public static <T> Optional<T> optionalTry(Supplier<T> callable) {
        try {
            return Optional.of(callable.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Set<Integer> rangeToSet(int startInclusive, int endExclusive) {
        return range(startInclusive, endExclusive).boxed().collect(Collectors.toSet());
    }

    public static boolean doubleIsZero(double val, double precision) {
        return val >= -precision && val <= precision;
    }

    @With
    public record Point3D(long x, long y, long z) {
        public Point3D(String[] split) {
            this(parseLong(split[0]), parseLong(split[1]), parseLong(split[2]));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Point3D point3D = (Point3D) o;
            return x == point3D.x && y == point3D.y && z == point3D.z;
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(x, y, z);
        }

        public Point3D cross(Point3D other) {
            return new Point3D(
                    this.y * other.z - other.y * this.z,
                    this.z * other.x - other.z * this.x,
                    this.x * other.y - other.x * this.y);
        }

        public double distance(Point3D other) {
            return sqrt(pow(this.x - other.x, 2) +
                    pow(this.y - other.y, 2) +
                    pow(this.z - other.z, 2));
        }

        public Point3D minus(Point3D other) {
            return new Point3D(x - other.x, y - other.y, z - other.z);
        }
    }

    public static List<Long> primeFactors(long number) {
        List<Long> factors = newArrayList();
        for (long i = 2; i <= sqrt(number); i++) {
            while (number % i == 0) {
                factors.add(i);
                number /= i;
            }
        }
        if (number > 1) {
            factors.add(number);
        }
        return factors;
    }

    public static <T> List<List<T>> generateCombinations(int number, List<T> values, boolean withRepeats) {
        List<List<T>> combinations = values.stream()
                .map(Lists::newArrayList)
                .collect(Collectors.toList());

        for (int i = 1; i < number; i++) {
            List<List<T>> newValues = newArrayList();
            for (List<T> combination : combinations) {
                for (T value : values) {
                    ArrayList<T> temp = newArrayList(combination);
                    temp.add(value);
                    if (withRepeats || !combination.contains(value)) {
                        newValues.add(temp);
                    }
                }
            }
            combinations = newValues;
        }

        return combinations;
    }

    public static <T> Set<Set<T>> generateSubsets(int size, Collection<T> values) {
        return generateSubsets(size, values.stream());
    }

    public static <T> Set<Set<T>> generateSubsets(int size, Stream<T> values) {
        Supplier<Stream<T>> valuesSupplier = () -> values;

        Set<Set<T>> combinations = valuesSupplier.get()
                .map(t -> {
                    Set<T> objects = newHashSet();
                    objects.add(t);
                    return objects;
                })
                .collect(Collectors.toSet());

        for (int i = 1; i < size; i++) {
            Set<Set<T>> newValues = newHashSet();
            for (Set<T> combination : combinations) {
                valuesSupplier.get().forEach(value -> {
                    Set<T> temp = newHashSet(combination);
                    temp.add(value);
                    newValues.add(temp);
                });
            }
            combinations = newValues;
        }

        return combinations.stream()
                .filter(set -> set.size() == size)
                .collect(Collectors.toSet());
    }

    public static EQSystemResult solve2System(long ax, long ay, long bx, long by, long px, long py) {
        /*
         ax A + bx B = px
         ay A + by B = py

         ax A = px - bx B
         A = (px - bx B) / ax

         ay (px - bx B) / ax + by B = py
         ay (px - bx B) + ax by B = py ax
         ay px - ay bx B + ax by B = py ax
         B (ax by - ay bx) = py ax - px ay
         B = (py ax - px ay) / (ax by - ay bx)
         */

        long B = (py * ax - px * ay) / (ax * by - ay * bx);
        long A = (px - bx * B) / ax;

        if (ax * A + bx * B != px || ay * A + by * B != py) {
            return new EQSystemResult(false, 0, 0);
        }
        return new EQSystemResult(true, A, B);
    }

    public record EQSystemResult(boolean solvable, long A, long B) {
    }
}
