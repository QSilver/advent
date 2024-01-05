package advent2015;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class Advent2 {
    public static void main(String[] args) {
        List<Box> boxList = InputUtils.fileStream("advent2015/advent2")
                .map(Box::new)
                .toList();
        int paper = boxList.stream()
                .map(Box::paper)
                .mapToInt(value -> value)
                .sum();
        int ribbon = boxList.stream()
                .map(Box::ribbon)
                .mapToInt(value -> value)
                .sum();

        log.info("Paper: {}", paper);
        log.info("Ribbon: {}", ribbon);
    }

    static class Box {
        int l;
        int h;
        int w;

        public Box(String input) {
            String[] split = input.split("x");
            this.l = Integer.parseInt(split[0]);
            this.w = Integer.parseInt(split[1]);
            this.h = Integer.parseInt(split[2]);
        }

        private int minFace() {
            return Stream.of(l * h, h * w, w * l)
                    .min(Integer::compareTo)
                    .get();
        }

        private int area() {
            return 2 * l * w + 2 * w * h + 2 * h * l;
        }

        private int volume() {
            return l * h * w;
        }

        private int minPerimeter() {
            return Stream.of(l + h, h + w, l + w)
                    .min(Integer::compareTo)
                    .get() * 2;
        }

        private int ribbon() {
            return minPerimeter() + volume();
        }

        public int paper() {
            return area() + minFace();
        }
    }
}
