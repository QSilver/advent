package advent2019;

import util.Util;

public class Advent1 {
    public static void main(String[] args) {
        System.out.println(solve());
    }

    private static int solve() {
        return Util.fileStream("advent2019/advent1")
                   .map(Integer::parseInt)
                   .map(Advent1::getAddedFuel)
                   .mapToInt(Integer::intValue)
                   .sum();
    }

    private static int getAddedFuel(int mass) {
        int addedFuel = 0;
        while (mass >= 0) {
            mass = calculateFuelForMass(mass);
            if (mass >= 0) {
                addedFuel += mass;
            }
        }
        return addedFuel;
    }

    private static int calculateFuelForMass(int mass) {
        return mass / 3 - 2;
    }
}