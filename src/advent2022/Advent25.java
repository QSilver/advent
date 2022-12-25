package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.stream.Collectors;

import static java.lang.Math.pow;

@Slf4j
public class Advent25 {

    public static void main(String[] args) {
        long sum = Util.fileStream("advent2022/advent25")
                .collect(Collectors.toList()).stream()
                .map(Advent25::fromSNAFU)
                .mapToLong(value -> value).sum();
        log.info("{}", toSNAFU(sum));
    }

    static long fromSNAFU(String snafu) {
        long number = 0;
        for (int c = 0; c < snafu.length(); c++) {
            final double power = pow(5, snafu.length() - 1 - c);
            switch (snafu.charAt(c)) {
                case '2' -> number += 2 * power;
                case '1' -> number += 1 * power;
                case '0' -> number += 0 * power;
                case '-' -> number += -1 * power;
                case '=' -> number += -2 * power;
            }
        }
        return number;
    }

    static String toSNAFU(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % 5);
            number /= 5;
            switch (remainder) {
                case 0 -> sb.append('0');
                case 1 -> sb.append('1');
                case 2 -> sb.append('2');
                case 3 -> {
                    sb.append('=');
                    number++;
                }
                case 4 -> {
                    sb.append('-');
                    number++;
                }
            }
        }
        return sb.reverse().toString();
    }
}