package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.abs;

@Slf4j
public class Advent23 {
    static int STACK_SIZE;
    static final Map<Integer, Character> HALLWAY_MAPPING = newHashMap();
    static final Map<Character, Integer> ENERGY = newHashMap();
    static final Map<String, Integer> MEMORY = newHashMap();
    static final Queue<Character> EXTRA_ROW_1 = new LinkedList<>();
    static final Queue<Character> EXTRA_ROW_2 = new LinkedList<>();

    static {
        ENERGY.put('A', 1);
        ENERGY.put('B', 10);
        ENERGY.put('C', 100);
        ENERGY.put('D', 1000);

        HALLWAY_MAPPING.put(3, 'A');
        HALLWAY_MAPPING.put(5, 'B');
        HALLWAY_MAPPING.put(7, 'C');
        HALLWAY_MAPPING.put(9, 'D');

        EXTRA_ROW_1.addAll(newArrayList('D', 'B', 'A', 'C'));
        EXTRA_ROW_2.addAll(newArrayList('D', 'C', 'B', 'A'));
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        solve(new State(Util.lines("advent2021/advent23"), true));
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    private static void solve(State starting) {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(state -> state.cost));
        queue.add(starting);

        State winningMove = new State(null, null, null, Integer.MAX_VALUE);
        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.cost >= winningMove.cost) {
                break;
            }

            List<State> possibleMoves = current.getPossibleMoves();
            for (State newState : possibleMoves) {
                if (newState.isDone()) {
                    winningMove = newState.cost < winningMove.cost ? newState : winningMove;
                } else {
                    String game = newState.toString();
                    if (newState.cost < MEMORY.getOrDefault(game, Integer.MAX_VALUE)) {
                        MEMORY.put(game, newState.cost);
                        queue.add(newState);
                    }
                }
            }
        }

        log.info("Min Cost: {}", winningMove.cost);
        displayWinningMove(winningMove);
    }

    private static void displayWinningMove(State winningMove) {
        while (winningMove.parent != null) {
            log.info("{}", winningMove);
            winningMove = winningMove.parent;
        }
    }

    @AllArgsConstructor
    static class State {
        State parent;
        List<Character> hallway;
        Map<Character, Stack<Character>> rooms = newHashMap();
        int cost = 0;

        public State(List<String> input, boolean isPart2) {
            String row3 = input.get(3);
            String row2 = input.get(2);
            rooms.put('A', getCharacters(row3.charAt(3), row2.charAt(3), isPart2));
            rooms.put('B', getCharacters(row3.charAt(5), row2.charAt(5), isPart2));
            rooms.put('C', getCharacters(row3.charAt(7), row2.charAt(7), isPart2));
            rooms.put('D', getCharacters(row3.charAt(9), row2.charAt(9), isPart2));
            hallway = "-..x.x.x.x..".chars()
                                    .mapToObj(value -> ((char) value))
                                    .collect(Collectors.toList());
            STACK_SIZE = isPart2 ? 4 : 2;
        }

        private Stack<Character> getCharacters(char row3, char row2, boolean isPart2) {
            Stack<Character> room = new Stack<>();
            room.push(row3);
            if (isPart2) {
                room.push(EXTRA_ROW_1.poll());
                room.push(EXTRA_ROW_2.poll());
            }
            room.push(row2);
            return room;
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
            return hallway.get(newPos) == '.' || hallway.get(newPos) == 'x';
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
                if (hallway.get(currentPos) != '.') {
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
            if (hallway.get(newPos) == '.') {
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
            newHallway.add(currentPos, HALLWAY_MAPPING.containsKey(currentPos) ? 'x' : '.');
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
            StringBuilder string = new StringBuilder();
            string.append("\n");
            string.append(hallway.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining())
                                 .substring(1)
                                 .replace('x', '.'));
            string.append("\n ");
            for (int i = 3; i >= 0; i--) {
                string.append(" ");
                for (char c : newArrayList('A', 'B', 'C', 'D')) {
                    string.append(getString(c, i));
                    string.append(" ");
                }
                string.append("\n ");
            }
            return string.substring(0, string.length() - 2);
        }

        private String getString(Character room, int pos) {
            return rooms.get(room)
                        .size() >= pos + 1 ? String.valueOf(rooms.get(room)
                                                                 .get(pos)) : ".";
        }
    }
}