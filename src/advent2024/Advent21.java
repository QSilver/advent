package advent2024;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.With;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Direction;
import util.Extensions;
import util.Point2D;
import util.Util2D.Node;

import java.util.*;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Long.parseLong;
import static java.lang.String.join;
import static java.util.HashSet.newHashSet;
import static util.Direction.*;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent21 {
    // https://adventofcode.com/2024/day/21

    public Advent21() {
        for (int button = 1; button <= 9; button++) {
            numberPad.put(new Point2D(2 - (button - 1) / 3, (button - 1) % 3), STR."\{button}".charAt(0));
        }
        numberPad.put(new Point2D(3, 1), '0');
        numberPad.put(new Point2D(3, 2), 'A');

        directionPad.put(new Point2D(1, 0), '<');
        directionPad.put(new Point2D(1, 1), 'v');
        directionPad.put(new Point2D(1, 2), '>');
        directionPad.put(new Point2D(0, 1), '^');
        directionPad.put(new Point2D(0, 2), 'A');
    }

    BiMap<Point2D, Character> numberPad = HashBiMap.create();
    BiMap<Point2D, Character> directionPad = HashBiMap.create();

    public Long runP1(String file) {
        return run(file, 2);
    }

    public Long runP2(String file) {
        return run(file, 25);
    }

    private long run(String file, int maxLayer) {
        return fileStream(file)
                .mapToLong(code -> {
                    long dfs = executeSequence(code, maxLayer, true);
                    long numericCode = parseLong(code.substring(0, code.length() - 1));
                    return dfs * numericCode;
                }).sum();
    }

    private List<String> bfs(Point2D from, Function<Point2D, Boolean> endCondition, boolean isNumpad) {
        List<String> paths = newArrayList();

        Queue<TempNode> toVisit = new ArrayDeque<>();

        Character fromChar = translate(from, isNumpad);

        Set<Character> seen = newHashSet(fromChar);
        toVisit.add(new TempNode(fromChar, ""));

        long shortest = Long.MAX_VALUE;

        while (!toVisit.isEmpty()) {
            TempNode pop = toVisit.remove();
            char current = pop.c;
            String path = pop.path;

            if (endCondition.apply(translate(current, isNumpad))) {
                if (shortest == Long.MAX_VALUE) {
                    shortest = path.length();
                }
                if (path.length() == shortest) {
                    paths.add(STR."\{join("", path)}A");
                }
                continue;
            }
            if (path.length() >= shortest) {
                continue;
            }

            List<TempNode> neighbours = getNeighbours(current, isNumpad);
            neighbours.forEach(tempNode -> {
                seen.add(tempNode.c);
                toVisit.add(tempNode.withPath(path + tempNode.path));
            });
        }
        return paths;
    }

    private List<TempNode> getNeighbours(char current, boolean isNumpad) {
        BiMap<Point2D, Character> source = isNumpad ? numberPad : directionPad;
        Point2D currentPoint = source.inverse().get(current);

        List<TempNode> result = newArrayList();
        Character up = source.get(currentPoint.UP());
        if (up != null) {
            result.add(new TempNode(up, STR."\{UP.toChar()}"));
        }
        Character down = source.get(currentPoint.DOWN());
        if (down != null) {
            result.add(new TempNode(down, STR."\{DOWN.toChar()}"));
        }
        Character left = source.get(currentPoint.LEFT());
        if (left != null) {
            result.add(new TempNode(left, STR."\{LEFT.toChar()}"));
        }
        Character right = source.get(currentPoint.RIGHT());
        if (right != null) {
            result.add(new TempNode(right, STR."\{RIGHT.toChar()}"));
        }

        return result;
    }

    private List<Node> getNeighbours(Node current, boolean isNumpad) {
        BiMap<Point2D, Character> source = isNumpad ? numberPad : directionPad;
        return Direction.values().stream()
                .map(direction -> {
                    Character c = source.get(current.point().neighbour(direction));
                    if (c != null) {
                        return new Node(source.inverse().get(c), direction, current.distance() + 1, current);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .toList();
    }

    @With
    record TempNode(char c, String path) {

    }

    private Character translate(Point2D from, boolean isNumpad) {
        return isNumpad ? numberPad.get(from) : directionPad.get(from);
    }

    private Point2D translate(Character from, boolean isNumpad) {
        return isNumpad ? numberPad.inverse().get(from) : directionPad.inverse().get(from);
    }

    LoadingCache<CacheKey, Long> distanceCache = CacheBuilder.newBuilder().build(CacheLoader.from(key -> executeSequence(key.sequence, key.layer, key.isNumpad)));

    record CacheKey(String sequence, int layer, boolean isNumpad) {

    }

    private long executeSequence(String sequence, int layer, boolean isNumpad) {
        long shortest = 0;

        sequence = STR."A\{sequence}";
        for (int index = 1; index < sequence.length(); index++) {
            Point2D from = translate(sequence.charAt(index - 1), isNumpad);
            Point2D to = translate(sequence.charAt(index), isNumpad);

            Function<Point2D, Boolean> endCondition = current -> current.equals(to);
            List<String> paths = bfs(from, endCondition, isNumpad);
            if (layer == 0) {
                shortest += paths.stream().mapToLong(String::length).min().orElse(0);
            } else {
                shortest += paths.stream().mapToLong(path -> getParentPath(layer, path)).min().orElse(0);
            }
        }

        return shortest;
    }

    private Long getParentPath(int layer, String path) {
        return distanceCache.getUnchecked(new CacheKey(path, layer - 1, false));
    }

}
