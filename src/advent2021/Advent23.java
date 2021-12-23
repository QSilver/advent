package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

        private boolean canGoIntoRoom(Character c, int newPos) {
            return HALLWAY_MAPPING.containsKey(newPos) && !isRoomDone(HALLWAY_MAPPING.get(newPos)) && isCorrectRoom(c, newPos) && canFitInRoom(c, newPos);
        }

        private boolean hallwayNotBlocked(int newPos) {
            return hallway.get(newPos) == '0' || hallway.get(newPos) == 'x';
        }

        private boolean isCorrectRoom(Character c, int position) {
            return HALLWAY_MAPPING.get(position) == c;
        }

        private boolean canFitInRoom(Character c, int position) {
            if (rooms.get(HALLWAY_MAPPING.get(position))
                     .isEmpty()) {
                return true;
            }
            // return false if any char in the room doesn't belong there
            return rooms.get(HALLWAY_MAPPING.get(position))
                        .stream()
                        .noneMatch(character -> character != c);
        }

        List<State> getPossibleMoves() {
            List<State> possibleMoves = newArrayList();
            for (int currentPos = 1; currentPos <= 11; currentPos++) {
                // if not empty space
                if (hallway.get(currentPos) != '0') {
                    // if current is in hallway
                    if (hallway.get(currentPos) != 'x') {
                        // can move from hallway to room - left/right
                        for (int newPos = currentPos - 1; newPos >= 3 && hallwayNotBlocked(newPos); newPos--) {
                            moveOutOfHallway(possibleMoves, currentPos, newPos);
                        }
                        for (int newPos = currentPos + 1; newPos <= 9 && hallwayNotBlocked(newPos); newPos++) {
                            moveOutOfHallway(possibleMoves, currentPos, newPos);
                        }
                    } else {
                        // current is in room
                        Stack<Character> currentRoom = rooms.get(HALLWAY_MAPPING.get(currentPos));
                        if (currentRoom.size() > 0) {
                            // can move out of room - left/right
                            for (int newPos = currentPos - 1; newPos >= 1 && hallwayNotBlocked(newPos); newPos--) {
                                moveOutOfRoom(possibleMoves, currentPos, newPos);
                            }
                            for (int newPos = currentPos + 1; newPos <= 11 && hallwayNotBlocked(newPos); newPos++) {
                                moveOutOfRoom(possibleMoves, currentPos, newPos);
                            }
                        }
                    }
                }
            }
            return possibleMoves;
        }

        private void moveOutOfRoom(List<State> possibleMoves, int currentPos, int newPos) {
            if (canGoIntoRoom(hallway.get(currentPos), newPos)) {
                possibleMoves.add(moveFromRoomToRoom(currentPos, newPos));
            }
            // if not room or room is unsuitable; check if hallway is empty
            if (hallway.get(newPos) == '0') {
                possibleMoves.add(moveFromRoomIntoHallway(currentPos, newPos));
            }
        }

        private void moveOutOfHallway(List<State> possibleMoves, int currentPos, int newPos) {
            if (canGoIntoRoom(hallway.get(currentPos), newPos)) {
                possibleMoves.add(moveFromHallwayToRoom(currentPos, newPos));
            }
        }

        private State moveFromRoomToRoom(int currentPos, int newPos) {
            Stack<Character> newCurrentRoom = new Stack<>();
            newCurrentRoom.addAll(rooms.get(HALLWAY_MAPPING.get(currentPos)));

            Stack<Character> moveInto = rooms.get(HALLWAY_MAPPING.get(newPos));
            Stack<Character> newMoveInto = new Stack<>();
            newMoveInto.addAll(moveInto);

            Character pop = newCurrentRoom.pop();
            newMoveInto.push(pop);

            Map<Character, Stack<Character>> newRooms = newHashMap(rooms);
            newRooms.put(HALLWAY_MAPPING.get(currentPos), newCurrentRoom);
            newRooms.put(HALLWAY_MAPPING.get(newPos), newMoveInto);
            int newCost = (STACK_SIZE - newCurrentRoom.size() + STACK_SIZE - moveInto.size() + abs(currentPos - newPos)) * ENERGY.get(pop);
            return new State(this, newArrayList(hallway), newRooms, cost + newCost);
        }

        private State moveFromRoomIntoHallway(int currentPos, int newPos) {
            Stack<Character> newRoom = new Stack<>();
            newRoom.addAll(rooms.get(HALLWAY_MAPPING.get(currentPos)));

            List<Character> newHallway = newArrayList(hallway);
            newHallway.remove(newPos);
            Character pop = newRoom.pop();
            newHallway.add(newPos, pop);

            Map<Character, Stack<Character>> newRooms = newHashMap(rooms);
            newRooms.put(HALLWAY_MAPPING.get(currentPos), newRoom);
            int newCost = (STACK_SIZE - newRoom.size() + abs(currentPos - newPos)) * ENERGY.get(pop);
            return new State(this, newHallway, newRooms, cost + newCost);
        }

        private State moveFromHallwayToRoom(int currentPos, int newPos) {
            Stack<Character> roomToMove = rooms.get(HALLWAY_MAPPING.get(newPos));
            List<Character> newHallway = newArrayList(hallway);
            Character remove = newHallway.remove(currentPos);
            newHallway.add(currentPos, HALLWAY_MAPPING.containsKey(currentPos) ? 'x' : '0');
            Stack<Character> newRoom = new Stack<>();
            newRoom.addAll(roomToMove);
            newRoom.push(hallway.get(currentPos));
            Map<Character, Stack<Character>> newRooms = newHashMap(rooms);
            newRooms.put(hallway.get(currentPos), newRoom);
            int newCost = (STACK_SIZE - roomToMove.size() + abs(currentPos - newPos)) * ENERGY.get(remove);
            return new State(this, newHallway, newRooms, cost + newCost);
        }

        @Override
        public String toString() {
            return "#############\n" + "#" + hallway.stream()
                                                    .map(String::valueOf)
                                                    .collect(Collectors.joining())
                                                    .substring(1)
                                                    .replace('x', '.')
                                                    .replace('0', '.') + "#\n" + "###" + getString('A', 3) + "#" + getString('B', 3) + "#" + getString('C', 3) + "#" + getString('D', 3) + "#  \n" + "  #" + getString('A', 2) + "#" + getString('B', 2) + "#" + getString('C', 2) + "#" + getString('D', 2) + "#  \n" + "  #" + getString('A', 1) + "#" + getString('B', 1) + "#" + getString('C', 1) + "#" + getString('D', 1) + "#  \n" + "  #" + getString('A', 0) + "#" + getString('B', 0) + "#" + getString('C', 0) + "#" + getString('D', 0) + "#  \n" + "  #########  ";
        }

        private String getString(Character room, int pos) {
            return rooms.get(room)
                        .size() >= pos + 1 ? String.valueOf(rooms.get(room)
                                                                 .get(pos)) : ".";
        }
    }
}