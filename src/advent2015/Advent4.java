package advent2015;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.InputUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class Advent4 {
    public static void main(String[] args) {
        String input = InputUtils.fileStream("advent2015/advent4")
                .toList()
                .get(0);

        List<Pair<Integer, String>> collect = IntStream.range(1, 10000000)
                .mapToObj(i -> Pair.create(i, md5(input + "" + i)))
                .filter(pair -> pair.getSecond().startsWith("000000"))
                .toList();
        log.info("Found: {}", collect);
    }

    @SneakyThrows
    private static String md5(String input) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        StringBuilder stringBuilder = new StringBuilder(no.toString(16));
        while (stringBuilder.length() < 32) {
            stringBuilder.insert(0, "0");
        }
        return stringBuilder.toString();
    }
}
