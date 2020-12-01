package advent2019;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent10 {
    public static void main(String[] args) {
        log.info("Max number of asteroids: {}", solve());
    }

    static boolean[][] asteroidMap;

    private static int solve() {
        List<String> advent10 = Util.fileStream("advent10")
                                    .collect(Collectors.toList());
        asteroidMap = new boolean[advent10.size()][advent10.get(0)
                                                           .length()];


        List<Asteroid> asteroids = populateAsteroids(advent10);
        calculateVisibility(asteroids);

        int max = Integer.MIN_VALUE;
        Asteroid save = asteroids.get(0);

        for (Asteroid asteroid : asteroids) {
            if (asteroid.visible.size() > max) {
                max = asteroid.visible.size();
                save = asteroid;
            }
        }

        log.info("Laser position: x={}, y={}", save.x, save.y);

        List<Fraction> walkingOrder = getSweep(save);

        List<Asteroid> purged = newArrayList();
        while (!isMapCLear()) {
            List<Asteroid> toRemove = newArrayList();
            final Asteroid laser = save;
            walkingOrder.forEach(fraction -> {
                Asteroid potential = new Asteroid(' ', fraction.x, fraction.y);
                if (areAsteroidsInLos(laser, potential)) {
                    toRemove.add(potential);
                }
            });
            toRemove.forEach(asteroid -> {
                boolean isAsteroid = asteroidMap[asteroid.y][asteroid.x];
                if (isAsteroid) {
                    purged.add(asteroid);
                    asteroidMap[asteroid.y][asteroid.x] = false;
                }
            });
        }

        log.info("200th Asteroid: {}", purged.get(199));

        return max;
    }

    private static boolean isMapCLear() {
        int count = 0;
        for (boolean[] line : asteroidMap) {
            for (boolean pos : line) {
                if (pos) {
                    count++;
                }
            }
        }
        return count == 1;
    }

    private static List<Fraction> getSweep(Asteroid save) {
        List<Fraction> fractions1 = newArrayList();
        for (int i = 0; i < asteroidMap[0].length - save.x; i++) {
            for (int j = 1; j <= save.y; j++) {
                fractions1.add(new Fraction(i, j, save.x + i, save.y - j)); // x + i, y - j
            }
        }
        fractions1.sort(Fraction::compareQ1);

        List<Fraction> fractions2 = newArrayList();
        for (int i = 1; i < asteroidMap[0].length - save.x; i++) {
            for (int j = 0; j < asteroidMap.length - save.y; j++) {
                fractions2.add(new Fraction(i, j, save.x + i, save.y + j)); // x + i, y + j
            }
        }
        fractions2.sort(Fraction::compareQ2);

        List<Fraction> fractions3 = newArrayList();
        for (int i = save.x; i >= 0; i--) {
            for (int j = 1; j < asteroidMap.length - save.y; j++) {
                fractions3.add(new Fraction(i, j, save.x - i, save.y + j)); // x + i, y + j
            }
        }
        fractions3.sort(Fraction::compareQ1);

        List<Fraction> fractions4 = newArrayList();
        for (int i = save.x; i > 0; i--) {
            for (int j = save.y; j >= 0; j--) {
                fractions4.add(new Fraction(i, j, save.x - i, save.y - j)); // x - i, y - j
            }
        }
        fractions4.sort(Fraction::compareQ2);

        List<Fraction> walkingOrder = newArrayList();
        walkingOrder.addAll(fractions1);
        walkingOrder.addAll(fractions2);
        walkingOrder.addAll(fractions3);
        walkingOrder.addAll(fractions4);

        return walkingOrder;
    }

    private static void calculateVisibility(List<Asteroid> asteroids) {
        for (Asteroid asteroid1 : asteroids) {
            for (Asteroid asteroid2 : asteroids) {
                if (asteroid1 != asteroid2 && areAsteroidsInLos(asteroid1, asteroid2)) {
                    asteroid1.visible.add(asteroid2);
                }
            }
        }
    }

    private static List<Asteroid> populateAsteroids(List<String> advent10) {
        List<Asteroid> asteroids = newArrayList();
        int i = 0;
        for (String line : advent10) {
            int j = 0;
            String[] split = line.split("");
            for (String s : split) {
                if (!s.equals(".")) {
                    asteroidMap[i][j] = true;
                    asteroids.add(new Asteroid(s.charAt(0), j, i));
                }
                j++;
            }
            i++;
        }
        return asteroids;
    }

    private static boolean areAsteroidsInLos(Asteroid asteroid1, Asteroid asteroid2) {
        int deltaX = Math.abs(asteroid1.x - asteroid2.x);
        int deltaY = Math.abs(asteroid1.y - asteroid2.y);

        int minX = Math.min(asteroid1.x, asteroid2.x);
        int maxX = Math.max(asteroid1.x, asteroid2.x);
        int minY = Math.min(asteroid1.y, asteroid2.y);
        int maxY = Math.max(asteroid1.y, asteroid2.y);

        if (deltaY == 0) {
            for (int index = minX + 1; index < maxX; index++) {
                if (asteroidMap[asteroid1.y][index]) {
                    //log.info("Blocked at x:{} y:{}", index, asteroid1.y);
                    return false;
                }
            }
        }

        if (deltaX == 0) {
            for (int index = minY + 1; index < maxY; index++) {
                if (asteroidMap[index][asteroid1.x]) {
                    //log.info("Blocked at x:{} y:{}", asteroid1.x, index);
                    return false;
                }
            }
        }

        int gcd = gcd(deltaX, deltaY);

        int gradientX = asteroid1.x < asteroid2.x ? 1 : -1;
        int gradientY = asteroid1.y < asteroid2.y ? 1 : -1;

        int xStep = deltaX / gcd;
        int yStep = deltaY / gcd;

        int count = 1;
        while (true) {
            int xPos = asteroid1.x + count * xStep * gradientX;
            int yPos = asteroid1.y + count * yStep * gradientY;
            if (!(xPos < maxX && xPos > minX && yPos < maxY && yPos > minY)) {
                break;
            }
            if (asteroidMap[yPos][xPos]) {
                //log.info("Blocked at x:{} y:{}", xPos, yPos);
                return false;
            }
            count++;
        }
        return true;
    }

    private static int gcd(int a, int b) {
        return a == 0 ? b : gcd(b % a, a);
    }
}

class Fraction {
    int num;
    int div;
    double value;
    int x;
    int y;

    public Fraction(int num, int div, int x, int y) {
        this.num = num;
        this.div = div;
        this.value = num / (div * 1.0);
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Fraction{" + x + "/" + y + "}";
    }

    public int compareQ1(Fraction that) {
        if (this.value < that.value) {
            return -1;
        }
        if (this.value > that.value) {
            return 1;
        }
        return this.num - that.num;
    }

    public int compareQ2(Fraction that) {
        if (this.value < that.value) {
            return 1;
        }
        if (this.value > that.value) {
            return -1;
        }
        return this.num - that.num;
    }
}

class Asteroid {
    char name;
    int x;
    int y;
    List<Asteroid> visible = newArrayList();

    @Override
    public String toString() {
        return "Asteroid{" + "x=" + x + ", y=" + y + '}';
    }

    public Asteroid(char name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}