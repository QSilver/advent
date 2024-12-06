package util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class Extensions {
    public static <T> T neighbour(T[][] map, Util2D.Point2D current) {
        return map[(int) current.row()][(int) current.col()];
    }

    public static void print(Object toPrint) {
        System.out.println(toPrint);
    }

    public static <T> Stream<T> stream(T[] toStream) {
        return Arrays.stream(toStream);
    }

    public static <T> void forEach(T[] toStream, Consumer<? super T> action) {
        stream(toStream).forEach(action);
    }
}