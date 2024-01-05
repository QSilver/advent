package advent2022;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Advent4 {
    public static void main(String[] args) {
        List<String> pairs = InputUtils.fileStream("advent2022/advent4").collect(Collectors.toList());

        long containedCount = pairs.stream().map(ElfPair::new).filter(ElfPair::fullyContains).count();
        log.info("Contained Pairs: {}", containedCount);

        long overlapCount = pairs.stream().map(ElfPair::new).filter(ElfPair::overlap).count();
        log.info("Overlapping Pairs: {}", overlapCount);
    }


    @AllArgsConstructor
    static class ElfPair {
        Elf elf1;
        Elf elf2;

        public ElfPair(String assignment) {
            String[] split = assignment.split(",");
            elf1 = new Elf(split[0]);
            elf2 = new Elf(split[1]);
        }

        public boolean fullyContains() {
            return (elf1.start <= elf2.start && elf1.end >= elf2.end) || (elf2.start <= elf1.start && elf2.end >= elf1.end);
        }

        public boolean overlap() {
            return Math.max(elf1.start, elf2.start) <= Math.min(elf1.end, elf2.end);
        }
    }

    static class Elf {
        long start;
        long end;

        public Elf(String assignment) {
            String[] split = assignment.split("-");
            start = Long.parseLong(split[0]);
            end = Long.parseLong(split[1]);
        }
    }
}
