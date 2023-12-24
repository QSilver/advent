package advent2023;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;
import static util.Util.doubleIsZero;
import static util.Util.fileStream;

@Slf4j
public class Advent24 {
    // https://adventofcode.com/2023/day/24

    public Long runP1(String file, long min, long max) {
        List<Vector> vectorList = fileStream(file)
                .map(line -> extractLine(line, true))
                .collect(Collectors.toList());

        return countIntersectingLinePairs(vectorList, min, max);
    }

    public Long runP2(String file) {
        List<Vector> vectorList = fileStream(file)
                .map(line -> extractLine(line, false))
                .collect(Collectors.toList());

        String pythonCode = getPythonCode(vectorList);
        return runPython(pythonCode);
    }

    private static String getPythonCode(List<Vector> vectorList) {
        return """
                from sympy import symbols, Eq, solve
                                
                x, y, z, vx, vy, vz, t0, t1, t2 = symbols('x y z vx vy vz t0 t1 t2')
                equations = (
                    Eq(%d + %d * t0, x + vx * t0),
                    Eq(%d + %d * t0, y + vy * t0),
                    Eq(%d + %d * t0, z + vz * t0),
                    Eq(%d + %d * t1, x + vx * t1),
                    Eq(%d + %d * t1, y + vy * t1),
                    Eq(%d + %d * t1, z + vz * t1),
                    Eq(%d + %d * t2, x + vx * t2),
                    Eq(%d + %d * t2, y + vy * t2),
                    Eq(%d + %d * t2, z + vz * t2)
                )
                                
                print(solve(equations, (x, y, z, vx, vy, vz, t0, t1, t2), dict=True))
                """.formatted(
                (long) vectorList.get(0).point.x, (long) vectorList.get(0).direction.x,
                (long) vectorList.get(0).point.y, (long) vectorList.get(0).direction.y,
                (long) vectorList.get(0).point.z, (long) vectorList.get(0).direction.z,
                (long) vectorList.get(1).point.x, (long) vectorList.get(1).direction.x,
                (long) vectorList.get(1).point.y, (long) vectorList.get(1).direction.y,
                (long) vectorList.get(1).point.z, (long) vectorList.get(1).direction.z,
                (long) vectorList.get(2).point.x, (long) vectorList.get(2).direction.x,
                (long) vectorList.get(2).point.y, (long) vectorList.get(2).direction.y,
                (long) vectorList.get(2).point.z, (long) vectorList.get(2).direction.z);
    }

    private static long runPython(String pythonCode) {
        try {
            String fileName = "2023advent24p2.py";
            FileWriter writer = new FileWriter(fileName);
            writer.write(pythonCode);
            writer.close();

            Process process = new ProcessBuilder("python", fileName).start();
            String results = CharStreams.toString(new InputStreamReader(process.getInputStream(), Charsets.UTF_8));

            process.waitFor();
            String[] split = results.replace(" ", "")
                    .replace("[", "")
                    .replace("{", "")
                    .replace("}", "")
                    .replace("]", "")
                    .replace("\r\n", "")
                    .split(",");

            long tar_x = parseLong(split[6].split(":")[1]);
            long tar_y = parseLong(split[7].split(":")[1]);
            long tar_z = parseLong(split[8].split(":")[1]);
            return tar_x + tar_y + tar_z;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Vector extractLine(String line, boolean ignoreZ) {
        line = line.replace(" ", "");
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

    // https://stackoverflow.com/questions/2316490/the-algorithm-to-find-the-point-of-intersection-of-two-3d-line-segment
    private static long countIntersectingLinePairs(List<Vector> vectorList, long min, long max) {
        long count = 0;
        for (int line_1 = 0; line_1 < vectorList.size() - 1; line_1++) {
            for (int line_2 = line_1 + 1; line_2 < vectorList.size(); line_2++) {
                Vector vector1 = vectorList.get(line_1);
                Vector vector2 = vectorList.get(line_2);

                Point3D d1 = vector1.direction;
                Point3D d2 = vector2.direction;
                double denominator = d1.cross(d2).z; // normal vector wrt to Z-plane

                if (doubleIsZero(denominator, 0.001)) {
                    continue;
                }

                Point3D perpendicular = vector2.point.minus(vector1.point);
                double s = (perpendicular.x * d2.y() - perpendicular.y * d2.x()) / denominator;
                double t = (-1 * perpendicular.x * d1.y() - (-1 * perpendicular.y * d1.x())) / denominator;

                double intersectionX = s * d1.x() + vector1.point.x;
                double intersectionY = s * d1.y() + vector1.point.y;

                if (intersectionX >= min && intersectionX <= max && intersectionY >= min && intersectionY <= max) {
                    if (s > 0 && t < 0) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    record Point3D(double x, double y, double z) {
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

    record Vector(Point3D point, Point3D direction, String text) {
    }
}
