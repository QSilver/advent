package advent2022;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

// 1725 - too low
@Slf4j
public class Advent16 {
    public static final String START = "AA";
    static int INF = 99999;
    static Map<String, Room> rooms;

    static Map<Pair<String, String>, Integer> distances = newHashMap();

    public static void main(String[] args) {
        rooms = Util.fileStream("advent2022/advent16")
                .map(Room::new).collect(Collectors.toMap(room -> room.label, room -> room));

        floydWarshall();

        Path starting = new Path();
        starting.route.add(START);
        starting.turn = 1;

        List<Path> paths = newArrayList(starting);
        while (true) {
            List<Path> newPaths = paths.stream()
                    .map(Path::generatePaths)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            log.info("New Iteration Size: {}", newPaths.size());
            if (newPaths.size() == 0) {
                break;
            }
            paths = newPaths;
        }

        paths.forEach(path -> {
            if (path.turn < 31) {
                path.pressure += (path.opened.stream().mapToInt(s -> rooms.get(s).flowRate * (31 - path.turn))).sum();
                path.turn = 31;
            }
        });

        List<Path> sortedPaths = paths.stream().sorted((o1, o2) -> Integer.compare(o2.pressure, o1.pressure)).collect(Collectors.toList());
        log.info("Max Pressure: {}", sortedPaths.get(0).pressure);
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

    static class Path {
        List<String> route = newArrayList();
        List<String> opened = newArrayList();
        int pressure;
        int turn;

        List<Path> generatePaths() {
            openValve();

            List<Path> possiblePaths = newArrayList();
            Room current = rooms.get(route.get(route.size() - 1));
            distances.entrySet().stream()
                    .filter(entry -> current.label.equals(entry.getKey().getFirst())) // all connections
                    .filter(entry -> !route.contains(entry.getKey().getSecond())) // unvisited
                    .forEach(entry -> {
                        if (turn + entry.getValue() < 31) { // can reach node
                            possiblePaths.add(createNewPath(entry));
                        }
                    });
            return possiblePaths;
        }

        private Path createNewPath(Map.Entry<Pair<String, String>, Integer> entry) {
            Path newPath = new Path();
            newPath.route = newArrayList(route);
            newPath.route.add(entry.getKey().getSecond());
            newPath.opened = newArrayList(opened);
            newPath.pressure = pressure;
            newPath.pressure += (newPath.opened.stream().mapToInt(s -> rooms.get(s).flowRate * entry.getValue())).sum();
            newPath.turn = turn + entry.getValue();
            return newPath;
        }

        void openValve() {
            Room current = rooms.get(route.get(route.size() - 1));
            if (!opened.contains(current.label) && current.flowRate > 0) {
                turn++;
                pressure += (opened.stream().mapToInt(s -> rooms.get(s).flowRate)).sum();
                opened.add(current.label);
            }
        }

        @Override
        public String toString() {
            return route + ", " + opened + ", pR=" + pressure + ", min=" + turn;
        }
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
