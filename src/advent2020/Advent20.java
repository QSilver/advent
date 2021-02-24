package advent2020;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static advent2020.Advent20.MAP;
import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent20 {
    private static final List<Tile> tiles = newArrayList();
    private static final Monster MONSTER = new Monster();

    private static final int TILE_SIZE = 10;
    private static final int SIZE = 31;
    public static final Tile[][] MAP = new Tile[SIZE][SIZE];

    public static void main(String[] args) {
        List<String> strings = Util.fileStream("advent2020/advent20")
                                   .collect(Collectors.toList());

        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i)
                       .equals("")) {
                tiles.add(new Tile(extractId(strings, i), extractMap(strings, i)));
            }
        }

        tiles.get(0)
             .flipV();
        placeTile(tiles.get(0), SIZE / 2, SIZE / 2);

        while (notAllPlaced()) {
            Tile newTile = tiles.stream()
                                .filter(tile -> !tile.placed)
                                .filter(tile -> !tile.used)
                                .findFirst()
                                .orElseThrow();
            List<Tile> placed = tiles.stream()
                                     .filter(tile -> tile.placed)
                                     .collect(Collectors.toList());

            fitTile(newTile, placed);

            if (tiles.stream()
                     .allMatch(tile -> tile.used || tile.placed)) {
                log.info("Reset");
                tiles.forEach(tile -> tile.used = false);
            }
        }

        Tile finalImage = merge(tiles.toArray(new Tile[0]));

        int monsters = parseImageCombinations(finalImage, 0, 0);
        long chars = Arrays.stream(finalImage.image)
                           .map(s -> s.chars()
                                      .filter(c -> c == '#')
                                      .count())
                           .reduce(Long::sum)
                           .orElse(-1L);
        long monsterPieces = MONSTER.getPosition()
                                    .stream()
                                    .map(List::size)
                                    .reduce(Integer::sum)
                                    .orElse(-1);
        log.info("Roughness: {}", chars - (monsterPieces * monsters));
    }

    private static int parseImageCombinations(Tile finalImage, int rotate, int flip) {
        if (flip == 2) {
            return 0;
        }
        if (rotate == 4) {
            finalImage.flipH();
            return parseImageCombinations(finalImage, 0, flip + 1);
        }
        int monsters = getMonsters(finalImage);
        if (monsters != 0) {
            return monsters;
        }
        finalImage.r90();
        return parseImageCombinations(finalImage, rotate + 1, flip);
    }

    private static int getMonsters(Tile finalImage) {
        int monsters = 0;
        for (int y = 0; y < finalImage.image.length - MONSTER.height; y++) {
            for (int x = 0; x < finalImage.image[y].length() - MONSTER.length; x++) {
                monsters += foundMonster(finalImage.image, x, y) ? 1 : 0;
            }
        }
        return monsters;
    }

    private static boolean foundMonster(String[] image, int x, int y) {
        List<List<Integer>> position = MONSTER.getPosition();
        for (int i = 0; i < position.size(); i++) {
            for (int j = 0; j < position.get(i)
                                        .size(); j++) {
                if (image[y + i].charAt(x + position.get(i)
                                                    .get(j)) != '#') {
                    return false;
                }
            }
        }
        return true;
    }

    private static void fitTile(Tile newTile, List<Tile> placed) {
        for (Tile placedTile : placed) {
            if (fitAroundPlaced(newTile, placedTile)) {
                return;
            }
        }
        newTile.used = true;
    }

    private static boolean fitAroundPlaced(Tile newTile, Tile placedTile) {
        if (newTile.fitsPos(placedTile.x, placedTile.y - 1)) {
            return placeTile(newTile, placedTile.x, placedTile.y - 1);
        } else if (newTile.fitsPos(placedTile.x, placedTile.y + 1)) {
            return placeTile(newTile, placedTile.x, placedTile.y + 1);
        } else if (newTile.fitsPos(placedTile.x - 1, placedTile.y)) {
            return placeTile(newTile, placedTile.x - 1, placedTile.y);
        } else if (newTile.fitsPos(placedTile.x + 1, placedTile.y)) {
            return placeTile(newTile, placedTile.x + 1, placedTile.y);
        }
        return false;
    }

    private static boolean placeTile(Tile tile, int x, int y) {
        log.info("Fit {} at pos {}:{}", tile.id, x, y);
        tile.x = x;
        tile.y = y;
        tile.placed = true;
        MAP[y][x] = tile;
        return true;
    }

    private static boolean notAllPlaced() {
        return tiles.stream()
                    .anyMatch(tile -> !tile.placed);
    }

    private static int extractId(List<String> strings, int i) {
        String s = strings.get(i - 11)
                          .split(" ")[1];
        return Integer.parseInt(s.substring(0, s.length() - 1));
    }

    private static String[] extractMap(List<String> strings, int i) {
        String[] map = new String[10];
        for (int j = i - 10; j < i; j++) {
            map[10 - (i - j)] = strings.get(j);
        }
        return map;
    }

    public static Tile merge(Tile... tiles) {
        int minX = Arrays.stream(tiles)
                         .mapToInt(tile -> tile.x)
                         .min()
                         .orElse(-1);
        int minY = Arrays.stream(tiles)
                         .mapToInt(tile -> tile.y)
                         .min()
                         .orElse(-1);
        int maxX = Arrays.stream(tiles)
                         .mapToInt(tile -> tile.x)
                         .max()
                         .orElse(-1);
        int maxY = Arrays.stream(tiles)
                         .mapToInt(tile -> tile.y)
                         .max()
                         .orElse(-1);
        String[] newImage = new String[(maxY - minY + 1) * (TILE_SIZE - 2)];

        int line = 0;
        for (int j = minY; j <= maxY; j++) {
            for (int i = minX; i <= maxX; i++) {
                for (Tile tile : tiles) {
                    if (tile.x == i && tile.y == j) {
                        for (int l = 0; l < TILE_SIZE - 2; l++) {
                            int pos = l + (TILE_SIZE - 2) * line;
                            if (newImage[pos] == null) {
                                newImage[pos] = "";
                            }
                            newImage[pos] += tile.image[l + 1].substring(1, tile.image[l].length() - 1);
                        }
                    }
                }
            }
            line++;
        }

        log.info("Corners {} {} {} {}", MAP[minY][minX].id, MAP[minY][maxX].id, MAP[maxY][minX].id, MAP[maxY][maxX].id);
        List<Integer> corners = newArrayList(MAP[minY][minX].id, MAP[minY][maxX].id, MAP[maxY][minX].id, MAP[maxY][maxX].id);
        log.info("Result: {}", corners.stream()
                                      .mapToLong(value -> value)
                                      .reduce((left, right) -> left * right)
                                      .orElse(-1));

        return new Tile(0, newImage);
    }
}

class Monster {
    int length = 20;
    int height = 3;

    public List<List<Integer>> getPosition() {
        List<Integer> index1 = getIndex("                  # ");
        List<Integer> index2 = getIndex("#    ##    ##    ###");
        List<Integer> index3 = getIndex(" #  #  #  #  #  #   ");
        return newArrayList(index1, index2, index3);
    }

    private List<Integer> getIndex(String line) {
        List<Integer> list = newArrayList();
        int index = line.indexOf("#");
        while (index >= 0) {
            list.add(index);
            index = line.indexOf("#", index + 1);
        }
        return list;
    }
}

@Slf4j
@ToString
class Tile {
    int id;
    int x;
    int y;
    String top;
    String bottom;
    String left;
    String right;
    boolean placed;
    boolean used;
    String[] image;

    public Tile(int id, String[] map) {
        this.id = id;
        this.top = map[0];
        this.bottom = map[map.length - 1];
        this.left = Arrays.stream(map)
                          .map(s -> s.charAt(0) + "")
                          .collect(Collectors.joining());
        this.right = Arrays.stream(map)
                           .map(s -> s.charAt(map.length - 1) + "")
                           .collect(Collectors.joining());
        this.image = map;
    }

    void flipH() {
        this.top = reverse(this.top);
        this.bottom = reverse(this.bottom);
        String temp = this.left;
        this.left = this.right;
        this.right = temp;

        image = Arrays.stream(image)
                      .map(this::reverse)
                      .collect(Collectors.toList())
                      .toArray(image);
    }

    void flipV() {
        this.left = reverse(this.left);
        this.right = reverse(this.right);
        String temp = this.top;
        this.top = this.bottom;
        this.bottom = temp;

        List<String> list = Arrays.asList(image.clone());
        Collections.reverse(list);
        image = list.toArray(image);
    }

    void r90() {
        String temp = this.top;
        this.top = this.right + "";
        this.right = reverse(this.bottom);
        this.bottom = this.left + "";
        this.left = reverse(temp);

        image = rotateClockWise();
    }

    boolean fitsPos(int x, int y) {
        return fitsPos(x, y, 0, 0);
    }

    private boolean fitsPos(int x, int y, int rotate, int flip) {
        if (flip == 2) {
            return false;
        }
        if (rotate == 4) {
            flipH();
            return fitsPos(x, y, 0, flip + 1);
        }
        if (checkFit(x, y)) {
            return true;
        }
        r90();
        return fitsPos(x, y, rotate + 1, flip);
    }

    private boolean checkFit(int x, int y) {
        return checkEdges(MAP[y - 1][x],
                MAP[y + 1][x],
                MAP[y][x - 1],
                MAP[y][x + 1]);
    }

    private boolean checkEdges(Tile top, Tile bottom, Tile left, Tile right) {
        return (top == null || this.top.equals(top.bottom))
                && (bottom == null || this.bottom.equals(bottom.top))
                && (left == null || this.left.equals(left.right))
                && (right == null || this.right.equals(right.left));
    }

    private String reverse(String s) {
        return new StringBuilder(s).reverse()
                                   .toString();
    }

    private String[] rotateClockWise() {
        char[][] processed = Arrays.stream(image)
                                   .map(String::toCharArray)
                                   .collect(Collectors.toList())
                                   .toArray(new char[0][0]);

        int size = processed.length;
        char[][] result = new char[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                result[i][j] = processed[j][size - i - 1];
            }
        }

        return Arrays.stream(result)
                     .map(String::new)
                     .toArray(String[]::new);
    }
}
