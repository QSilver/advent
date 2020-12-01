package advent2020;

import util.Util;

import java.util.HashSet;
import java.util.Set;

public class Advent1 {
    static Set<Integer> haveSeen = new HashSet<>();

    public static void main(String[] args) {
        solveFor3();
    }

    private static void solveFor2() {
        Util.fileStream("advent2020/advent1")
            .map(Integer::parseInt)
            .peek(integer -> haveSeen.add(2020 - integer))
            .forEach(integer -> {
                if (haveSeen.contains(integer)) {
                    System.out.println((2020 - integer) * integer);
                }
            });
    }

    private static void solveFor3() {
        Util.fileStream("advent2020/advent1")
            .map(Integer::parseInt)
            .forEach(integer -> haveSeen.add(integer));

        haveSeen.forEach(integer1 ->
                haveSeen.forEach(integer2 -> {
                    if (haveSeen.contains(2020 - integer1 - integer2)) {
                        System.out.println(integer1 * integer2 * (2020 - integer1 - integer2));
                    }
                }));
    }
}
