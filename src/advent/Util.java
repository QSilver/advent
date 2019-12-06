package advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

public class Util {
    public static Stream<String> fileStream(String fileName) {
        try {
            return Files.lines(Paths.get(".\\resources", fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newArrayList("").stream();
    }
}
