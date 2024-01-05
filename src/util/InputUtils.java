package util;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Slf4j
public class InputUtils {
    public static String[] readDoubleNewlineBlocks(String fileName) {
        return fileStream(fileName)
                .collect(joining("\n"))
                .split("\n\n");
    }

    public static Stream<String> fileStream(String fileName) {
        try {
            return lines(get(".\\resources", fileName));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return newArrayList("").stream();
    }

    public static ArrayList<String> splitLine(Stream<String> stream) {
        Optional<String> first = stream.findFirst();
        return first.map(s -> newArrayList(s.split(",")))
                .orElseGet(Lists::newArrayList);
    }

    public static Stream<String> getFirstLineSplit(String file, String delimiter) {
        return stream(fileStream(file).findFirst().orElseThrow().split(delimiter));
    }

    public static String stringRemove(String string, String... toRemove) {
        AtomicReference<String> output = new AtomicReference<>(string);
        stream(toRemove).forEach(s -> output.set(output.get().replace(s, "")));
        return output.get();
    }

    @SneakyThrows
    public static void clearConsole() {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO()
                .start()
                .waitFor();
        log.debug("{}", System.in.read());
    }

    public static void main(String[] args) {
        fileStream("advent2023/advent2.in");
    }
}
