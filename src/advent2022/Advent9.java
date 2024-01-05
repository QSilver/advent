package advent2022;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent9 {
    static Set<Point> visited = newHashSet();

    public static void main(String[] args) {
        List<Point> points = newArrayList();
        Point head = new Point(0, 0);
        points.add(head);

        for (int i = 1; i <= 9; i++) {
            Point p = new Point(0, 0);
            points.add(p);
            Point pointInFront = points.get(i - 1);
            pointInFront.pointBehind = p;
            p.pointInFront = pointInFront;
        }

        visited.add(new Point(0, 0, null, null));
        InputUtils.fileStream("advent2022/advent9").forEach(head::move);
        log.info("Points tail visited: {}", visited.size());
    }

    @AllArgsConstructor
    static class Point {
        int x;
        int y;
        Point pointInFront;
        Point pointBehind;

        void move(String move) {
            String[] s = move.split(" ");
            String direction = s[0];
            int size = Integer.parseInt(s[1]);

            for (int i = 0; i < size; i++) {
                switch (direction) {
                    case "U" -> this.y++;
                    case "D" -> this.y--;
                    case "L" -> this.x--;
                    case "R" -> this.x++;
                }
                pointBehind.follow();
            }
        }

        void follow() {
//            int hDistance = pointInFront.x - this.x;
//            int vDistance = pointInFront.y - this.y;
//
//            if (abs(hDistance) > 1 || abs(vDistance) > 1) {
//                if (abs(hDistance) > 1) {
//                    this.x += hDistance / 2;
//                }
//                if (abs(vDistance) > 1) {
//                    this.y += vDistance / 2;
//                }
//            }

            if (pointInFront.x == this.x + 2 && pointInFront.y == this.y) {
                this.x++;
            } else if (pointInFront.x == this.x - 2 && pointInFront.y == this.y) {
                this.x--;
            } else if (pointInFront.x == this.x && pointInFront.y == this.y + 2) {
                this.y++;
            } else if (pointInFront.x == this.x && pointInFront.y == this.y - 2) {
                this.y--;
            } else if ((pointInFront.x == this.x + 1 && pointInFront.y == this.y + 2) ||
                    (pointInFront.x == this.x + 2 && pointInFront.y == this.y + 1) ||
                    (pointInFront.x == this.x + 2 && pointInFront.y == this.y + 2)) {
                this.x++;
                this.y++;
            } else if ((pointInFront.x == this.x - 1 && pointInFront.y == this.y + 2) ||
                    (pointInFront.x == this.x - 2 && pointInFront.y == this.y + 1) ||
                    (pointInFront.x == this.x - 2 && pointInFront.y == this.y + 2)) {
                this.x--;
                this.y++;
            } else if ((pointInFront.x == this.x - 1 && pointInFront.y == this.y - 2) ||
                    (pointInFront.x == this.x - 2 && pointInFront.y == this.y - 1) ||
                    (pointInFront.x == this.x - 2 && pointInFront.y == this.y - 2)) {
                this.x--;
                this.y--;
            } else if ((pointInFront.x == this.x + 1 && pointInFront.y == this.y - 2) ||
                    (pointInFront.x == this.x + 2 && pointInFront.y == this.y - 1) ||
                    (pointInFront.x == this.x + 2 && pointInFront.y == this.y - 2)) {
                this.x++;
                this.y--;
            }

            if (pointBehind == null) {
                visited.add(new Point(this.x, this.y));
            } else {
                pointBehind.follow();
            }
        }


        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(x, y);
        }

        @Override
        public String toString() {
            return x + "," + y + "[" + pointBehind + "]";
        }
    }
}
