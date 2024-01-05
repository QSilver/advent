package advent2020;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent16 {
    static List<Condition> conditions = newArrayList();

    public static void main(String[] args) {
        InputUtils.fileStream("advent2020/advent16-conditions")
            .forEach(line -> {
                String[] split = line.split(":");
                String[] s = split[1].split(" ");
                conditions.add(new Condition(split[0],
                        s[1].split("-")[0],
                        s[1].split("-")[1],
                        s[3].split("-")[0],
                        s[3].split("-")[1])
                );
            });

        int ticketSum = InputUtils.fileStream("advent2020/advent16-tickets")
                            .collect(Collectors.toList())
                            .stream()
                            .map(ticket -> checkTicketAndGetInvalid(ticket, 0))
                            .reduce(Integer::sum)
                            .orElse(0);

        log.info("Result: {}", ticketSum);

        List<String> validTickets = InputUtils.fileStream("advent2020/advent16-tickets")
                                        .collect(Collectors.toList())
                                        .stream()
                                        .filter(ticket -> checkTicketAndGetInvalid(ticket, -1) == -1)
                                        .collect(Collectors.toList());

        int ticketLength = validTickets.get(0)
                                       .split(",").length;
        conditions.forEach(condition -> IntStream.range(0, ticketLength)
                                                 .forEach(position -> addMatchingConditions(validTickets, condition, position)));

        List<Condition> locked = newArrayList();
        while (!hasFinished()) {
            filterConditions(locked);
        }
        filterConditions(locked);

        locked.forEach(condition -> log.info("Condition: {}, Positions: {}", condition.name, condition.positions.toString()));
        log.info("Ticket: {}", validTickets.get(0));

        long departure = locked.stream()
                               .filter(condition -> condition.name.contains("departure"))
                               .map(condition -> condition.positions.iterator()
                                                                    .next())
                               .map(position -> validTickets.get(0)
                                                            .split(",")[position])
                               .map(Long::parseLong)
                               .reduce((l1, l2) -> l1 * l2)
                               .orElse(-1L);
        log.info("Departure: {}", departure);
    }

    private static void addMatchingConditions(List<String> validTickets, Condition condition, int position) {
        if (validTickets.stream()
                        .map(ticket -> condition.check(Integer.parseInt(ticket.split(",")[position])))
                        .reduce((aBoolean, aBoolean2) -> aBoolean & aBoolean2)
                        .orElse(false)) {
            condition.positions.add(position);
        }
    }

    private static void filterConditions(List<Condition> locked) {
        List<Condition> finishedConditions = conditions.stream()
                                                       .filter(condition -> condition.positions.size() == 1)
                                                       .collect(Collectors.toList());
        locked.addAll(finishedConditions);
        conditions.removeAll(finishedConditions);

        conditions.forEach(condition -> condition.positions.removeAll(locked.stream()
                                                                            .map(lockedCondition -> lockedCondition.positions.iterator()
                                                                                                                             .next())
                                                                            .collect(Collectors.toList())));
    }

    private static Integer checkTicketAndGetInvalid(String ticket, int orElse) {
        return Arrays.stream(ticket.split(","))
                     .map(Integer::parseInt)
                     .filter(integer -> !checkAllConditions(integer))
                     .reduce(Integer::sum)
                     .orElse(orElse);
    }

    private static boolean checkAllConditions(Integer integer) {
        return conditions.stream()
                         .map(condition -> condition.check(integer))
                         .reduce((aBoolean, aBoolean2) -> aBoolean | aBoolean2)
                         .orElse(false);
    }

    private static boolean hasFinished() {
        return conditions.stream()
                         .map(condition -> condition.positions.size() == 1)
                         .reduce((aBoolean, aBoolean2) -> aBoolean & aBoolean2)
                         .orElse(false);
    }
}

@ToString
class Condition {
    String name;
    int min1;
    int max1;
    int min2;
    int max2;
    Set<Integer> positions = newHashSet();

    public Condition(String name, String min1, String max1, String min2, String max2) {
        this.name = name;
        this.min1 = Integer.parseInt(min1);
        this.max1 = Integer.parseInt(max1);
        this.min2 = Integer.parseInt(min2);
        this.max2 = Integer.parseInt(max2);
    }

    public boolean check(int value) {
        return (value >= min1 && value <= max1) || (value >= min2 && value <= max2);
    }
}
