package util;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public
class Util {
    public static Stream<String> fileStream(String fileName) {
        try {
            return Files.lines(Paths.get(".\\resources", fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newArrayList("").stream();
    }

    static ArrayList<String> splitLine(Stream<String> stream) {
        Optional<String> first = stream.findFirst();
        return first.map(s -> newArrayList(s.split(",")))
                    .orElseGet(Lists::newArrayList);
    }

    @SneakyThrows
    public static void clearConsole() {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO()
                                              .start()
                                              .waitFor();
        log.debug("{}", System.in.read());
    }
}
