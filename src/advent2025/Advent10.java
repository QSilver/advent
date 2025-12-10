package advent2025;

import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import util.Extensions;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.round;
import static lpsolve.LpSolve.makeLp;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent10 {
    // https://adventofcode.com/2025/day/10
    // https://en.wikipedia.org/wiki/Linear_programming#Standard_form

    public Long runP1(String file) {
        return fileStream(file)
                .map(line -> new Machine(line.split(" ")))
                .map(Advent10::tryButtons)
                .mapToLong(Long::longValue)
                .sum();
    }

    public Long runP2(String file) {
        return fileStream(file)
                .map(line -> new Machine(line.split(" ")))
                .map(Advent10::solveForMachine)
                .mapToLong(Long::longValue)
                .sum();
    }

    private static long tryButtons(Machine machine) {
        Deque<GoalState> queue = new ArrayDeque<>();
        ArrayList<Boolean> lights = newArrayList();
        for (int i = 0; i < machine.lights().size(); i++) {
            lights.add(false);
        }
        queue.add(new GoalState(lights, 0));
        Set<List<Boolean>> seen = newHashSet();

        while (!queue.isEmpty()) {
            GoalState current = queue.poll();

            if (current.lights().equals(machine.lights())) {
                return current.buttonPresses();
            }

            for (Button button : machine.buttons()) {
                List<Boolean> newLights = newArrayList(current.lights());

                // TODO - find a way to just XOR it
                for (Integer position : button.positions()) {
                    newLights.set(position, !newLights.get(position));
                }

                if (!seen.contains(newLights)) {
                    seen.add(newLights);
                    queue.add(new GoalState(newLights, current.buttonPresses() + 1));
                }
            }
        }

        throw new RuntimeException("No solution found");
    }

    @SneakyThrows
    private static long solveForMachine(Machine machine) {
        LpSolve solver = makeLp(0, machine.buttons.size());

        setObjectiveToAll1s(machine, solver);
        addConstraints(machine, solver);

        solver.solve();

        double objVal = solver.getObjective();
        solver.deleteLp();

        return round(objVal);
    }

    private static void setObjectiveToAll1s(Machine machine, LpSolve solver) throws LpSolveException {
        int size = machine.buttons.size();

        double[] objective = new double[size + 1];
        for (int i = 1; i <= size; i++) {
            objective[i] = 1.0;
            solver.setInt(i, true);
            solver.setLowbo(i, 0);
        }

        solver.setObjFn(objective);
        // setting objective to all 1s so coeffs of the affine are x1 + x2 + ... + xn
        // minimal sum x1 + x2 + ... + xn = minimal number of button presses
        solver.setMinim();
    }

    private static void addConstraints(Machine machine, LpSolve solver) throws LpSolveException {
        /* this makes my head hurt

        [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
        [0,0,0,0,1,1] -> buttonIndex 4,5 affect joltageIndex 0
        [0,1,0,0,0,1] -> buttonIndex 2,5 affect joltageIndex 1
        [0,0,1,1,1,0] -> buttonIndex 2,3,4 affect joltageIndex 2
        [1,1,0,1,0,0] -> buttonIndex 0,1,3 affect joltageIndex 3
         */

        for (int joltageIndex = 0; joltageIndex < machine.joltage.size(); joltageIndex++) {
            Integer joltageValue = machine.joltage.get(joltageIndex);
            solver.addConstraint(buttonsThatAffect(machine, joltageIndex), LpSolve.EQ, joltageValue);
        }
    }

    private static double[] buttonsThatAffect(Machine machine, int joltageIndex) {
        double[] row = new double[machine.buttons.size() + 1];
        for (int buttonIndex = 0; buttonIndex < machine.buttons.size(); buttonIndex++) {
            Button button = machine.buttons.get(buttonIndex);
            if (button.positions.contains(joltageIndex)) { // button affects this joltage
                row[buttonIndex + 1] = 1.0;
            }
        }
        return row;
    }

    record Machine(List<Boolean> lights, List<Button> buttons, List<Integer> joltage) {
        public Machine(String[] split) {
            this(getLights(split), getButtons(split), getJoltageList(split));
        }
    }


    private static List<Integer> getJoltageList(String[] split) {
        List<Integer> joltageList = newArrayList();
        String[] joltage = split[split.length - 1]
                .substring(1, split[split.length - 1].length() - 1)
                .split(",");
        for (String s : joltage) {
            joltageList.add(Integer.parseInt(s));
        }
        return joltageList;
    }

    private static List<Button> getButtons(String[] split) {
        List<Button> buttons = newArrayList();
        for (int s = 1; s < split.length - 1; s++) {
            List<Integer> list = split[s]
                    .substring(1, split[s].length() - 1)
                    .split(",").stream()
                    .map(Integer::parseInt)
                    .toList();
            buttons.add(new Button(list));
        }
        return buttons;
    }

    private static List<Boolean> getLights(String[] split) {
        List<Boolean> lights = newArrayList();
        for (int c = 1; c < split[0].length() - 1; c++) {
            lights.add(split[0].charAt(c) == '#');
        }
        return lights;
    }

    record Button(List<Integer> positions) {

    }

    record GoalState(List<Boolean> lights, Integer buttonPresses) {
    }
}
