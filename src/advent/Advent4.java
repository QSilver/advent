package advent;

import java.util.Arrays;

public class Advent4 {
    public static void main(String[] args) {
        System.out.println(solve());
    }

    private static int solve() {
        int lowerBound = 153517;
        int upperBound = 630395;

        int count = 0;
        for (int i = lowerBound; i <= upperBound; i++) {
            if (checkDescending(i + "") && checkAdjacent2(i + "")) {
                count++;
                System.out.println(i + " - " + checkAdjacent2(i + ""));
            }
        }
        return count;
    }

    private static boolean checkDescending(String number) {
        for (int i = 0; i < number.length() - 1; i++) {
            int current = Integer.parseInt(number.charAt(i) + "");
            int next = Integer.parseInt(number.charAt(i + 1) + "");

            if (current > next) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkAdjacent2(String number) {
        Boolean[] hasDouble = new Boolean[10];
        int pairValue = -1;

        for (int i = 0; i < number.length() - 1; i++) {
            int current = Integer.parseInt(number.charAt(i) + "");
            int next = Integer.parseInt(number.charAt(i + 1) + "");

            if (current == next) {
                if (current == pairValue) {
                    hasDouble[pairValue] = false;
                    continue;
                }
                pairValue = current;
                hasDouble[pairValue] = true;
            }
        }
        return Arrays.asList(hasDouble)
                     .contains(true);
    }
}