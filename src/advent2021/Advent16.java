package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent16 {
    static int VERSION_SUM = 0;

    public static void main(String[] args) {
        List<String> hexStrings = Util.fileStream("advent2021/advent16")
                                      .collect(Collectors.toList());

        String result = hexToBin(hexStrings.get(0)
                                           .toUpperCase(Locale.ROOT));
        Pair<Package, String> finalPackage = processPackage(result);
        log.info("P1 {}", VERSION_SUM);
        long value = getValue(finalPackage.getFirst());
        log.info("P2 {}", value);
    }

    static Pair<Package, String> processPackage(String bin) {
        String copy = bin;
        int version = Integer.parseInt(copy.substring(0, 3), 2);
        VERSION_SUM += version;
        copy = copy.substring(3);
        int type = Integer.parseInt(copy.substring(0, 3), 2);
        copy = copy.substring(3);

        StringBuilder output = new StringBuilder();
        if (type == 4) {
            char c = 'x';
            while (c != '0') {
                c = copy.substring(0, 1)
                        .charAt(0);
                copy = copy.substring(1);
                copy = readBits(copy, output, 4);
            }
            return Pair.create(new Package(Long.parseLong(output.toString(), 2), type, newArrayList()), copy);
        } else {
            copy = readBits(copy, output, 1);

            StringBuilder sub = new StringBuilder();
            if (output.charAt(0) == '0') {
                StringBuilder val = new StringBuilder();
                copy = readBits(copy, val, 15);
                copy = readBits(copy, sub, Integer.parseInt(val.toString(), 2));

                List<Package> packageList = getPackages(sub);
                return Pair.create(new Package(0, type, packageList), copy);
            } else {
                copy = readBits(copy, sub, 11);

                List<Package> subPackageList = newArrayList();
                for (int p = 0; p < Integer.parseInt(sub.toString(), 2); p++) {
                    Pair<Package, String> packageStringPair = processPackage(copy);
                    subPackageList.add(packageStringPair.getFirst());
                    copy = packageStringPair.getSecond();
                }
                return Pair.create(new Package(0, type, subPackageList), copy);
            }
        }
    }

    private static List<Package> getPackages(StringBuilder sub) {
        List<Package> packageList = newArrayList();
        String copy = sub.toString();
        while (copy.contains("1")) {
            Pair<Package, String> packageStringPair = processPackage(copy);
            copy = packageStringPair.getSecond();
            packageList.add(packageStringPair.getFirst());
        }
        return packageList;
    }

    private static long getValue(Package aPackage) {
        aPackage.subPackageList.forEach(p -> p.value = getValue(p));

        final LongStream longStream = aPackage.subPackageList.stream()
                                                             .map(p -> p.value)
                                                             .mapToLong(value -> value);
        switch (aPackage.type) {
            case 0 -> {
                return longStream.reduce(0L, Long::sum);
            }
            case 1 -> {
                return longStream.reduce(1L, (left, right) -> left * right);
            }
            case 2 -> {
                return longStream.min()
                                 .getAsLong();
            }
            case 3 -> {
                return longStream.max()
                                 .getAsLong();
            }
            case 4 -> {
                return aPackage.value;
            }
            case 5 -> {
                return aPackage.subPackageList.get(0).value > aPackage.subPackageList.get(1).value ? 1 : 0;
            }
            case 6 -> {
                return aPackage.subPackageList.get(0).value < aPackage.subPackageList.get(1).value ? 1 : 0;
            }
            case 7 -> {
                return aPackage.subPackageList.get(0).value == aPackage.subPackageList.get(1).value ? 1 : 0;
            }
        }
        return -1;
    }

    private static String readBits(String from, StringBuilder into, int bits) {
        into.append(from, 0, bits);
        return from.substring(bits);
    }

    static String hexToBin(String hex) {
        hex = hex.replaceAll("0", "0000");
        hex = hex.replaceAll("1", "0001");
        hex = hex.replaceAll("2", "0010");
        hex = hex.replaceAll("3", "0011");
        hex = hex.replaceAll("4", "0100");
        hex = hex.replaceAll("5", "0101");
        hex = hex.replaceAll("6", "0110");
        hex = hex.replaceAll("7", "0111");
        hex = hex.replaceAll("8", "1000");
        hex = hex.replaceAll("9", "1001");
        hex = hex.replaceAll("A", "1010");
        hex = hex.replaceAll("B", "1011");
        hex = hex.replaceAll("C", "1100");
        hex = hex.replaceAll("D", "1101");
        hex = hex.replaceAll("E", "1110");
        hex = hex.replaceAll("F", "1111");
        return hex;
    }

    @AllArgsConstructor
    static class Package {
        long value;
        int type;
        List<Package> subPackageList;

        @Override
        public String toString() {
            return "v=" + value + " t=" + type + " {" + subPackageList + '}';
        }
    }
}
