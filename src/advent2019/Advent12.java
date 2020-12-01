package advent2019;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Advent12 {
    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        List<Moon> starting = Util.fileStream("advent2019/advent12")
                                  .map(s -> s.substring(1, s.length() - 1))
                                  .map(s -> s.split(","))
                                  .collect(Collectors.toList())
                                  .stream()
                                  .map(Advent12::getMoon)
                                  .collect(Collectors.toList());

        int[] steps = new int[3];
        for (int axis = 0; axis < 3; axis++) {
            List<Moon> moons = starting.stream()
                                       .map(moon -> new Moon(moon.position[0], moon.position[1], moon.position[2]))
                                       .collect(Collectors.toList());

            do {
                steps[axis]++;
                for (int i = 0; i < moons.size() - 1; i++) {
                    for (int j = i; j < moons.size(); j++) {
                        if (moons.get(i) != moons.get(j)) {
                            applyGravity(moons.get(i), moons.get(j), axis);
                        }
                    }
                }
                moons.forEach(Moon::applyVelocity);
            } while (!isStartingPositionForAxis(starting, moons, axis));
            log.info("Axis = {}, Steps = {}", axis, steps[axis]);
        }

        log.info("LCM Steps = {}", lcm_of_array_elements(steps));

//        AtomicInteger energy = new AtomicInteger();
//        moons.forEach(moon -> {
//            int kinetic = moon.kineticEnergy();
//            int potential = moon.potentialEnergy();
//            int total = kinetic * potential;
//            energy.getAndAdd(total);
//            log.info("Kinetic = {}, Potential = {}, Total = {}", kinetic, potential, total);
//        });
//        log.info("Total = {}", energy.get());
    }

    private static boolean isStartingPositionForAxis(List<Moon> starting, List<Moon> newMoons, int axis) {
        for (int i = 0; i < starting.size(); i++) {
            if (!axisMatch(starting.get(i), newMoons.get(i), axis)) {
                return false;
            }
        }
        return true;
    }

    private static boolean axisMatch(Moon starting, Moon newMoon, int axis) {
        return starting.position[axis] == newMoon.position[axis] && starting.velocity[axis] == newMoon.velocity[axis];
    }

    private static void applyGravity(Moon moon1, Moon moon2, int axis) {
        moon1.velocity[axis] -= Integer.compare(moon1.position[axis], moon2.position[axis]);
        moon2.velocity[axis] += Integer.compare(moon1.position[axis], moon2.position[axis]);
    }

    private static Moon getMoon(String[] strings) {
        int x = Integer.parseInt(strings[0].split("=")[1]);
        int y = Integer.parseInt(strings[1].split("=")[1]);
        int z = Integer.parseInt(strings[2].split("=")[1]);
        return new Moon(x, y, z);
    }

    public static long lcm_of_array_elements(int[] element_array) {
        long lcm_of_array_elements = 1;
        int divisor = 2;

        while (true) {
            int counter = 0;
            boolean divisible = false;

            for (int i = 0; i < element_array.length; i++) {
                if (element_array[i] == 0) {
                    return 0;
                } else if (element_array[i] < 0) {
                    element_array[i] = element_array[i] * (-1);
                }
                if (element_array[i] == 1) {
                    counter++;
                }

                if (element_array[i] % divisor == 0) {
                    divisible = true;
                    element_array[i] = element_array[i] / divisor;
                }
            }

            if (divisible) {
                lcm_of_array_elements = lcm_of_array_elements * divisor;
            } else {
                divisor++;
            }

            if (counter == element_array.length) {
                return lcm_of_array_elements;
            }
        }
    }
}

class Moon {
    int[] position = new int[3];
    int[] velocity = new int[3];

    public Moon(int xPos, int yPos, int zPos) {
        this.position[0] = xPos;
        this.position[1] = yPos;
        this.position[2] = zPos;
    }

    public void applyVelocity() {
        position[0] += velocity[0];
        position[1] += velocity[1];
        position[2] += velocity[2];
    }

    public int potentialEnergy() {
        return Math.abs(position[0]) + Math.abs(position[1]) + Math.abs(position[2]);
    }

    public int kineticEnergy() {
        return Math.abs(velocity[0]) + Math.abs(velocity[1]) + Math.abs(velocity[2]);
    }

    @Override
    public String toString() {
        return "pos=<x=" + position[0] + ", y=" + position[1] + ", z=" + position[2] + ">, vel=<x=" + velocity[0] + ", y=" + velocity[1] + ", z=" + velocity[2] + '>';
    }
}