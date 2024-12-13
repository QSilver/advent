package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent13 {
    // https://adventofcode.com/2024/day/13

    public Long runP1(String file) {
        return fileStream(file).collect(Collectors.joining("SEPARATOR")).split("SEPARATORSEPARATOR").stream()
                .mapToLong(clawMachine -> {
                    String[] split = clawMachine.split("SEPARATOR");

                    Pattern pattern = Pattern.compile("\\d+");

                    String[] string = split[0].split(" ");
                    int ax = Integer.parseInt(string[2].substring(2, string[2].length() - 1));
                    int ay = Integer.parseInt(string[3].substring(2, string[3].length()));

                    string = split[1].split(" ");
                    int bx = Integer.parseInt(string[2].substring(2, string[2].length() - 1));
                    int by = Integer.parseInt(string[3].substring(2, string[3].length()));

                    string = split[2].split(" ");
                    int px = Integer.parseInt(string[1].substring(2, string[1].length() - 1));
                    int py = Integer.parseInt(string[2].substring(2, string[2].length()));

                    for (int buttonA = 0; buttonA < 100; buttonA++) {
                        for (int buttonB = 0; buttonB < 100; buttonB++) {
                            if (buttonA * ax + buttonB * bx == px && buttonA * ay + buttonB * by == py) {
                                System.out.println(STR."Winning in A:\{buttonA} B:\{buttonB}");
                                return 3 * buttonA + buttonB;
                            }
                        }
                    }

                    return 0;
                }).sum();
    }

    public Long runP2(String file) {
        return fileStream(file).collect(Collectors.joining("SEPARATOR")).split("SEPARATORSEPARATOR").stream()
                .mapToLong(clawMachine -> {
                    String[] split = clawMachine.split("SEPARATOR");

                    Pattern pattern = Pattern.compile("\\d+");

                    String[] string = split[0].split(" ");
                    long ax = Integer.parseInt(string[2].substring(2, string[2].length() - 1));
                    long ay = Integer.parseInt(string[3].substring(2, string[3].length()));

                    string = split[1].split(" ");
                    long bx = Integer.parseInt(string[2].substring(2, string[2].length() - 1));
                    long by = Integer.parseInt(string[3].substring(2, string[3].length()));

                    string = split[2].split(" ");
                    long px = Integer.parseInt(string[1].substring(2, string[1].length() - 1)) + 10000000000000L;
                    long py = Integer.parseInt(string[2].substring(2, string[2].length())) + 10000000000000L;

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

                    long B = (py * ax - px * ay) / (ax * by - bx * ay);
                    long A = (px - bx * B) / ax;

                    if (ax * A + bx * B != px || ay * A + by * B != py) {
                        return 0;
                    }
                    return A * 3 + B;
                }).sum();
    }
}
