package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.abs;

@Slf4j
public class Advent10 {
    static Set<Integer> importantCycles = newHashSet(20, 60, 100, 140, 180, 220);
    static AtomicInteger register = new AtomicInteger(1);
    static AtomicInteger cycle = new AtomicInteger(1);
    static AtomicInteger signalStrength = new AtomicInteger();
    static StringBuffer crt = new StringBuffer();

    public static void main(String[] args) {
        Util.fileStream("advent2022/advent10").forEach(Advent10::processInstruction);
        log.info("Sum Signal Strength: {}", signalStrength);
        log.info("{}", crt.toString());
    }

    private static void processInstruction(String instr) {
        String[] split = instr.split(" ");
        if (split[0].equals("addx")) {
            checkCycle(register.get(), cycle, signalStrength);
            checkCycle(register.get(), cycle, signalStrength);
            register.addAndGet(Integer.parseInt(split[1]));
        } else if (split[0].equals("noop")) {
            checkCycle(register.get(), cycle, signalStrength);
        }
    }

    private static void checkCycle(int register, AtomicInteger atomicCycle, AtomicInteger signalStrength) {
        int cycle = atomicCycle.get();

        if (cycle % 40 == 1) {
            crt.append(System.getProperty("line.separator"));
        }

        if (abs((cycle - 1) % 40 - register) <= 1) {
            crt.append("██");
        } else {
            crt.append("  ");
        }

        if (importantCycles.contains(cycle)) {
            signalStrength.addAndGet(register * cycle);
            log.info("Cycle: {}; Instant Signal Strength: {}", cycle, register * cycle);
        }
        atomicCycle.getAndIncrement();
    }
}
