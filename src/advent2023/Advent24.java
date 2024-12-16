package advent2023;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.SympySolver.Equation;
import util.Util.Point3D;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Long.parseLong;
import static util.InputUtils.fileStream;
import static util.SympySolver.Equation.build;
import static util.SympySolver.solveGeneric;
import static util.Util.doubleIsZero;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent24 {
    // https://adventofcode.com/2023/day/24

    public Long runP1(String file, long min, long max) {
        List<Vector> vectorList = getVectors(file, true);

        return countIntersectingLinePairs(vectorList, min, max);
    }

    public Long runP2(String file) {
        List<Vector> vectorList = getVectors(file, false);

        List<String> symbolList = newArrayList("x", "y", "z", "vx", "vy", "vz", "t0", "t1", "t2");
        List<Equation> equationList = newArrayList();
        for (int i = 0; i <= 2; i++) {
            Vector v = vectorList.get(i);
            equationList.add(build(v.point.x(), v.direction.x(), "y", "vy", "t" + i));
            equationList.add(build(v.point.y(), v.direction.y(), "z", "vz", "t" + i));
            equationList.add(build(v.point.z(), v.direction.z(), "x", "vx", "t" + i));
        }

        Map<String, Long> solution = solveGeneric(symbolList, equationList);
        return solution.get("x") + solution.get("y") + solution.get("z");
    }


    // https://stackoverflow.com/questions/2316490/the-algorithm-to-find-the-point-of-intersection-of-two-3d-line-segment
    private static long countIntersectingLinePairs(List<Vector> vectorList, long min, long max) {
        long count = 0;
        for (int line_1 = 0; line_1 < vectorList.size() - 1; line_1++) {
            for (int line_2 = line_1 + 1; line_2 < vectorList.size(); line_2++) {
                Vector vector1 = vectorList.get(line_1);
                Vector vector2 = vectorList.get(line_2);

                Point3D d1 = vector1.direction;
                Point3D d2 = vector2.direction;
                double denominator = d1.cross(d2).z(); // normal vector wrt to Z-plane

                if (doubleIsZero(denominator, 0.001)) {
                    continue;
                }

                Point3D perpendicular = vector2.point.minus(vector1.point);
                double s = (perpendicular.x() * d2.y() - perpendicular.y() * d2.x()) / denominator;
                double t = (-1 * perpendicular.x() * d1.y() - (-1 * perpendicular.y() * d1.x())) / denominator;

                double intersectionX = s * d1.x() + vector1.point.x();
                double intersectionY = s * d1.y() + vector1.point.y();

                if (intersectionX >= min && intersectionX <= max && intersectionY >= min && intersectionY <= max) {
                    if (s > 0 && t < 0) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static List<Vector> getVectors(String file, boolean ignoreZ) {
        return fileStream(file)
                .map(line -> getVectorFromLine(line, ignoreZ))
                .toList();
    }

    private static Vector getVectorFromLine(String line, boolean ignoreZ) {
        line = line.stringRemove(" ");
        String[] split = line.split("@");
        String[] first = split[0].split(",");
        String[] second = split[1].split(",");

        long x = parseLong(first[0]);
        long y = parseLong(first[1]);
        long z = ignoreZ ? 0 : parseLong(first[2]);
        Point3D from = new Point3D(x, y, z);

        long v_x = parseLong(second[0]);
        long v_y = parseLong(second[1]);
        long v_z = ignoreZ ? 0 : parseLong(second[2]);
        Point3D vector = new Point3D(v_x, v_y, v_z);

        return new Vector(from, vector, line);
    }

    record Vector(Point3D point, Point3D direction, String text) {
    }
}
