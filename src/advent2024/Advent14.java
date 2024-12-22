package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Point2D;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigInteger.*;
import static java.util.regex.Pattern.compile;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent14 {
    // https://adventofcode.com/2024/day/14

    public Long runP1(String file, BigInteger wide, BigInteger tall) {
        return run(file, wide, tall, valueOf(100));
    }

    public Long runP2(String file, BigInteger wide, BigInteger tall) {
        Pattern pattern = compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");

        List<Robot> robots = fileStream(file)
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    return new Robot(valueOf(Long.parseLong(matcher.group(1))),
                            valueOf(Long.parseLong(matcher.group(2))),
                            valueOf(Long.parseLong(matcher.group(3))),
                            valueOf(Long.parseLong(matcher.group(4))));
                })
                .collect(Collectors.toList());

        long iter = 0;
        while (iter < 224357412) {
            iter++;

            List<Robot> afterMove = robots.stream()
                    .map(robot -> {
                        BigInteger newCol = robot.colV.multiply(ONE).add(robot.col).mod(wide);
                        BigInteger newRow = robot.rowV.multiply(ONE).add(robot.row).mod(tall);
                        return new Robot(newCol, newRow, robot.colV, robot.rowV);
                    }).toList();

            Set<Point2D> unique = afterMove.stream()
                    .map(robot -> new Point2D(robot.row.intValue(), robot.col.intValue()))
                    .collect(Collectors.toSet());

            robots.clear();
            robots.addAll(afterMove);

            if (robots.size() == unique.size()) {
                for (int i = 0; i < wide.intValue(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < tall.intValue(); j++) {
                        if (unique.contains(new Point2D(i, j))) {
                            line.append("#");
                        } else {
                            line.append(".");
                        }
                    }
                    System.out.println(line);
                }
                return iter;
            }
        }

        return 0L;
    }

    private static long run(String file, BigInteger wide, BigInteger tall, BigInteger steps) {
        Pattern pattern = compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");

        List<Robot> robots = fileStream(file)
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    return new Robot(valueOf(Long.parseLong(matcher.group(1))),
                            valueOf(Long.parseLong(matcher.group(2))),
                            valueOf(Long.parseLong(matcher.group(3))),
                            valueOf(Long.parseLong(matcher.group(4))));
                })
                .toList();

        List<Robot> afterMove = robots.stream()
                .map(robot -> {
                    BigInteger newCol = robot.colV.multiply(steps).add(robot.col).mod(wide);
                    BigInteger newRow = robot.rowV.multiply(steps).add(robot.row).mod(tall);
                    return new Robot(newCol, newRow, robot.colV, robot.rowV);
                }).toList();

        final List<BigInteger> quadrants = newArrayList(ZERO, ZERO, ZERO, ZERO);

        afterMove.forEach(robot -> {
            if (robot.col.compareTo(wide.divide(TWO)) < 0) {
                if (robot.row.compareTo(tall.divide(TWO)) < 0) {
                    quadrants.set(0, quadrants.get(0).add(ONE));
                }
            }
            if (robot.col.compareTo(wide.divide(TWO)) < 0) {
                if (robot.row.compareTo(tall.divide(TWO)) > 0) {
                    quadrants.set(1, quadrants.get(1).add(ONE));
                }
            }
            if (robot.col.compareTo(wide.divide(TWO)) > 0) {
                if (robot.row.compareTo(tall.divide(TWO)) < 0) {
                    quadrants.set(2, quadrants.get(2).add(ONE));
                }
            }
            if (robot.col.compareTo(wide.divide(TWO)) > 0) {
                if (robot.row.compareTo(tall.divide(TWO)) > 0) {
                    quadrants.set(3, quadrants.get(3).add(ONE));
                }
            }
        });

        return quadrants.get(0)
                .multiply(quadrants.get(1))
                .multiply(quadrants.get(2))
                .multiply(quadrants.get(3))
                .longValue();
    }

    record Robot(BigInteger col, BigInteger row, BigInteger colV, BigInteger rowV) {
    }
}
