package util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class Extensions {
    public static <T> T atPos(T[][] map, Util2D.Point2D current) {
        return map[(int) current.row()][(int) current.col()];
    }

    public static <T> Stream<T> stream(T[] toStream) {
        return Arrays.stream(toStream);
    }

    public static <T> void forEach(T[] toStream, Consumer<? super T> action) {
        stream(toStream).forEach(action);
    }

    public static String stringRemove(String string, String... toRemove) {
        AtomicReference<String> output = new AtomicReference<>(string);
        stream(toRemove).forEach(s -> output.set(output.get().replace(s, "")));
        return output.get();
    }
}