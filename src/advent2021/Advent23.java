package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.abs;

@Slf4j
public class Advent23 {
    static final int STACK_SIZE = 4;
    static final Map<Integer, Character> HALLWAY_MAPPING = newHashMap();
    static final Map<Character, Integer> ENERGY = newHashMap();
    static final Map<String, Integer> MEMORY = newHashMap();

    static {
        ENERGY.put('A', 1);
        ENERGY.put('B', 10);
        ENERGY.put('C', 100);
        ENERGY.put('D', 1000);

        HALLWAY_MAPPING.put(3, 'A');
        HALLWAY_MAPPING.put(5, 'B');
        HALLWAY_MAPPING.put(7, 'C');
        HALLWAY_MAPPING.put(9, 'D');
    }

    static int CURRENT_BEST = Integer.MAX_VALUE;
    static State WINNING_MOVE;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(state -> state.cost));
        queue.add(new State());

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.cost >= CURRENT_BEST) {
                break;
            }

            List<State> possibleMoves = current.getPossibleMoves();
            for (State newState : possibleMoves) {
                if (newState.isDone()) {
                    if (newState.cost < CURRENT_BEST) {
                        CURRENT_BEST = newState.cost;
                        WINNING_MOVE = newState;
                    }
                } else {
                    String game = newState.toString();
                    if (newState.cost < MEMORY.getOrDefault(game, Integer.MAX_VALUE)) {
                        MEMORY.put(game, newState.cost);
                        queue.add(newState);
                    }
                }
            }
        }

        log.info("Min Cost: {}", CURRENT_BEST);
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    @AllArgsConstructor
    static class State {
        State parent;
        List<Character> hallway;
        Map<Character, Stack<Character>> rooms = newHashMap();
        int cost = 0;

        public State() {
            Stack<Character> A = new Stack<>();
            A.push('D');
            A.push('D');
            A.push('D');
            A.push('C');
            rooms.put('A', A);
            Stack<Character> B = new Stack<>();
            B.push('C');
            B.push('B');
            B.push('C');
            B.push('A');
            rooms.put('B', B);
            Stack<Character> C = new Stack<>();
            C.push('A');
            C.push('A');
            C.push('B');
            C.push('B');
            rooms.put('C', C);
            Stack<Character> D = new Stack<>();
            D.push('B');
            D.push('C');
            D.push('A');
            D.push('D');
            rooms.put('D', D);
            hallway = "-00x0x0x0x00".chars()
                                    .mapToObj(value -> ((char) value))
                                    .collect(Collectors.toList());
        }

        boolean isDone() {
            return isRoomDone('A') && isRoomDone('B') && isRoomDone('C') && isRoomDone('D');
        }

        private boolean isRoomDone(Character c) {
            return rooms.get(c)
                        .stream()
                        .filter(character -> character == c)
                        .count() == STACK_SIZE;
        }

        private boolean isCorrectRoom(Character c, Integer position) {
            return HALLWAY_MAPPING.get(position) == c;
        }

        private boolean canFit(Character c, Stack<Character> room) {
            if (room.isEmpty()) {
                return true;
            }
            // return false if any char in the room doesn't belong there
            return room.stream()
                       .noneMatch(character -> character != c);
        }

        List<State> getPossibleMoves() {
            List<State> possibleMoves = newArrayList();
            for (int i = 1; i <= 11; i++) {
                if (hallway.get(i) != '0') {
                    if (hallway.get(i) != 'x') {
                        // can move from hallway to room
                        for (int j = i - 1; j >= 3; j--) {
                            if (hallwayToRoom(possibleMoves, i, j)) {
                                break;
                            }
                        }
                        for (int j = i + 1; j <= 9; j++) {
                            if (hallwayToRoom(possibleMoves, i, j)) {
                                break;
                            }
                        }
                    } else if (hallway.get(i) == 'x') {
                        if (!isRoomDone(HALLWAY_MAPPING.get(i))) {
                            // can move out of room
                            for (int j = i - 1; j >= 1; j--) {
                                if (moveOutOfRoom(possibleMoves, i, j)) {
                                    break;
                                }
                            }
                            for (int j = i + 1; j <= 11; j++) {
                                if (moveOutOfRoom(possibleMoves, i, j)) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return possibleMoves.stream()
                                .filter(Objects::nonNull)
                                .filter(state -> state.cost < CURRENT_BEST)
                                .toList();
        }

        private boolean moveOutOfRoom(List<State> possibleMoves, int i, int j) {
            if (hallway.get(j) != '0') {
                // hallway blocked
                if (hallway.get(j) != 'x') {
                    return true;
                }
                // try move into room
                possibleMoves.add(roomToRoom(i, j));
            }
            // move into hallway
            if (hallway.get(j) == '0') {
                possibleMoves.add(roomToHallway(i, j));
            }
            return false;
        }

        private State roomToHallway(int i, int j) {
            Stack<Character> currentRoom = rooms.get(HALLWAY_MAPPING.get(i));
            if (currentRoom.size() > 0) {
                Stack<Character> newRoom = new Stack<>();
                newRoom.addAll(currentRoom);

                List<Character> newHallway = newArrayList(hallway);
                newHallway.remove(j);
                Character pop = newRoom.pop();
                newHallway.add(j, pop);

                Map<Character, Stack<Character>> newRooms = newHashMap(rooms);
                newRooms.put(HALLWAY_MAPPING.get(i), newRoom);
                int newCost = (STACK_SIZE - newRoom.size() + abs(i - j)) * ENERGY.get(pop);
                return new State(this, newHallway, newRooms, cost + newCost);
            }
            return null;
        }

        private State roomToRoom(int i, int j) {
            Stack<Character> currentRoom = rooms.get(HALLWAY_MAPPING.get(i));
            Stack<Character> moveInto = rooms.get(HALLWAY_MAPPING.get(j));
            if (currentRoom.size() > 0) {
                if (isCorrectRoom(currentRoom.peek(), j)) {
                    if (canFit(currentRoom.peek(), moveInto)) {
                        if (currentRoom.size() > 0) {
                            Stack<Character> newCurrentRoom = new Stack<>();
                            newCurrentRoom.addAll(currentRoom);

                            Stack<Character> newMoveInto = new Stack<>();
                            newMoveInto.addAll(moveInto);

                            Character pop = newCurrentRoom.pop();
                            newMoveInto.push(pop);

                            List<Character> newHallway = newArrayList(hallway);

                            Map<Character, Stack<Character>> newRooms = newHashMap(rooms);
                            newRooms.put(HALLWAY_MAPPING.get(i), newCurrentRoom);
                            newRooms.put(HALLWAY_MAPPING.get(j), newMoveInto);
                            int newCost = (STACK_SIZE - newCurrentRoom.size() + STACK_SIZE - moveInto.size() + abs(i - j)) * ENERGY.get(pop);
                            return new State(this, newHallway, newRooms, cost + newCost);
                        }
                    }
                }
            }
            return null;
        }

        private boolean hallwayToRoom(List<State> possibleMoves, int i, int j) {
            if (hallway.get(j) != '0' && hallway.get(j) != 'x') {
                // hallway blocked
                return true;
            }
            Stack<Character> roomToMove = rooms.get(HALLWAY_MAPPING.get(j));
            if (hallway.get(j) == 'x') {
                if (isCorrectRoom(hallway.get(i), j)) {
                    if (canFit(hallway.get(i), roomToMove)) {
                        List<Character> newHallway = newArrayList(hallway);
                        Character remove = newHallway.remove(i);
                        newHallway.add(i, HALLWAY_MAPPING.containsKey(i) ? 'x' : '0');
                        Stack<Character> newRoom = new Stack<>();
                        newRoom.addAll(roomToMove);
                        newRoom.push(hallway.get(i));
                        Map<Character, Stack<Character>> newRooms = newHashMap(rooms);
                        newRooms.put(hallway.get(i), newRoom);
                        int newCost = (STACK_SIZE - roomToMove.size() + abs(i - j)) * ENERGY.get(remove);
                        possibleMoves.add(new State(this, newHallway, newRooms, cost + newCost));
                    }
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "#############\n" +
                    "#" + hallway.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining())
                                 .substring(1)
                                 .replace('x', '.')
                                 .replace('0', '.') + "#\n" +
                    "###" + getString('A', 3) + "#" + getString('B', 3) + "#" + getString('C', 3) + "#" + getString('D', 3) + "#  \n" +
                    "  #" + getString('A', 2) + "#" + getString('B', 2) + "#" + getString('C', 2) + "#" + getString('D', 2) + "#  \n" +
                    "  #" + getString('A', 1) + "#" + getString('B', 1) + "#" + getString('C', 1) + "#" + getString('D', 1) + "#  \n" +
                    "  #" + getString('A', 0) + "#" + getString('B', 0) + "#" + getString('C', 0) + "#" + getString('D', 0) + "#  \n" +
                    "  #########  ";
        }

        private String getString(Character room, int pos) {
            return rooms.get(room)
                        .size() >= pos + 1 ? String.valueOf(rooms.get(room)
                                                                 .get(pos)) : ".";
        }
    }
}