package advent2022;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.InputUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent16 {
    public static final String START = "AA";
    static int INF = 99999;
    static Map<String, Room> rooms;

    static Map<Pair<String, String>, Integer> distances = newHashMap();

    static Map<Set<String>, Integer> releasePressureForValveCombination = newHashMap();

    public static void main(String[] args) {
        rooms = InputUtils.fileStream("advent2022/advent16")
                .map(Room::new).collect(Collectors.toMap(room -> room.label, room -> room));

        floydWarshall();

        log.info("Best Path: {}", explore(rooms.get(START), 30, newArrayList(), 0));

        releasePressureForValveCombination.clear();
        explore(rooms.get(START), 26, newArrayList(), 0);

        AtomicInteger maxElephantPressure = new AtomicInteger();
        AtomicReference<Pair<Set<String>, Set<String>>> savedPath = new AtomicReference<>();

        releasePressureForValveCombination.forEach((strings1, integer1) -> {
            releasePressureForValveCombination.forEach((strings2, integer2) -> {
                Set<String> intersection = newHashSet(strings1);
                intersection.retainAll(strings2);

                if (intersection.size() == 0) {
                    if (maxElephantPressure.get() < integer1 + integer2) {
                        maxElephantPressure.set(integer1 + integer2);
                        savedPath.set(Pair.create(strings1, strings2));
                    }
                }
            });
        });
        log.info("Best Path with Elephant: {} - Path 1: {} - Path 2: {}", maxElephantPressure.get(), savedPath.get().getFirst(), savedPath.get().getSecond());
    }

    private static Pair<Integer, List<String>> explore(Room current, int timeRemaining, List<String> openValves, int releasedWhenMoving) {
        int thisPressure = openValves
                .stream()
                .map(valve -> rooms.get(valve).flowRate)
                .mapToInt(Integer::intValue)
                .sum();
        int maxPressure = thisPressure * timeRemaining;

        Set<String> currentValves = newHashSet(openValves);
        if (releasePressureForValveCombination.containsKey(currentValves)) {
            if (releasePressureForValveCombination.get(currentValves) < maxPressure + releasedWhenMoving) {
                releasePressureForValveCombination.put(currentValves, maxPressure + releasedWhenMoving);
            }
        } else {
            releasePressureForValveCombination.put(currentValves, maxPressure + releasedWhenMoving);
        }

        List<String> bestRoute = newArrayList();

        List<Room> valves = rooms.values().stream()
                .filter(room -> room.flowRate > 0)
                .filter(room -> !room.label.equals(current.label))
                .collect(Collectors.toList());

        for (Room valve : valves) {
            int movesToOpen = distances.get(Pair.create(current.label, valve.label)) + 1;
            if (timeRemaining >= movesToOpen && !openValves.contains(valve.label)) {
                openValves.add(valve.label);
                releasedWhenMoving += thisPressure * movesToOpen;
                Pair<Integer, List<String>> explore = explore(rooms.get(valve.label), timeRemaining - movesToOpen, openValves, releasedWhenMoving);
                int subPressure = explore.getFirst() + thisPressure * movesToOpen;

                if (subPressure > maxPressure) {
                    maxPressure = subPressure;
                    bestRoute.clear();
                    bestRoute.add(valve.label);
                    bestRoute.addAll(explore.getSecond());
                }

                releasedWhenMoving -= thisPressure * movesToOpen;
                openValves.remove(openValves.size() - 1);
            }
        }

        return Pair.create(maxPressure, bestRoute);
    }

    private static void floydWarshall() {
        rooms.values().forEach(room -> room.tunnels.forEach(tunnel -> distances.put(new Pair<>(room.label, tunnel), 1)));
        for (Room intermediate : rooms.values()) {
            for (Room first : rooms.values()) {
                for (Room second : rooms.values()) {
                    if (!first.equals(second)) {
                        int dist_first_interm = distances.getOrDefault(new Pair<>(first.label, intermediate.label), INF);
                        int dist_interm_second = distances.getOrDefault(new Pair<>(intermediate.label, second.label), INF);
                        int dist_first_second = distances.getOrDefault(new Pair<>(first.label, second.label), INF);
                        if (dist_first_interm + dist_interm_second < dist_first_second) {
                            distances.put(new Pair<>(first.label, second.label), dist_first_interm + dist_interm_second);
                        }
                    }
                }
            }
        }
        distances = distances.entrySet().stream()
                .filter(entry -> rooms.get(entry.getKey().getFirst()).flowRate > 0 || START.equals(entry.getKey().getFirst()))
                .filter(entry -> rooms.get(entry.getKey().getSecond()).flowRate > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    static class Room {
        String label;
        int flowRate;
        List<String> tunnels = newArrayList();

        public Room(String input) {
            this.label = input.split(" ")[1];
            this.flowRate = Integer.parseInt(input.split("rate=")[1].split(";")[0]);

            String[] split = input.replace("valves", "valve").split("valve ")[1].split(", ");
            tunnels.addAll(Arrays.stream(split).toList());
        }

        @Override
        public String toString() {
            return label + "=" + flowRate + " " + tunnels;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Room room = (Room) o;
            return Objects.equal(label, room.label);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(label);
        }
    }
}