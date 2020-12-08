package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent8 {
    static List<String> commands = newArrayList();

    public static void main(String[] args) {
        commands = Util.fileStream("advent2020/advent8")
                       .collect(Collectors.toList());

        for (int i = 0; i < commands.size(); i++) {
            List<String> commandsCopy = new ArrayList<>(commands);

            if (commandsCopy.get(i)
                            .contains("nop")) {
                commandsCopy.add(i, commandsCopy.get(i)
                                                .replace("nop", "jmp"));
                commandsCopy.remove(i + 1);
            } else if (commandsCopy.get(i)
                                   .contains("jmp")) {
                commandsCopy.add(i, commandsCopy.get(i)
                                                .replace("jmp", "nop"));
                commandsCopy.remove(i + 1);
            }

            Set<Integer> haveSeen = newHashSet();
            int accumulator = 0;
            int pc = 0;
            while (haveSeen.add(pc)) {
                if (pc == commandsCopy.size()) {
                    log.info("We did it! Acc {}", accumulator);
                    break;
                }
                String[] s = commandsCopy.get(pc)
                                         .split(" ");

                int num = Integer.parseInt(s[1].substring(1)) * (s[1].charAt(0) == '+' ? 1 : -1);
                if (s[0].equals("acc")) {
                    accumulator += num;
                }
                if (s[0].equals("jmp")) {
                    pc += num;
                } else {
                    pc++;
                }
            }
        }
    }
}
