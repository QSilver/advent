package advent;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

public class Advent22 {
    private static final int DECK_SIZE = 10007;

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<ShuffleInstruction> shuffleInstructions = newArrayList();

        Util.fileStream("advent22").forEach(s -> {
            String[] splitLine = s.split(" ");
            try {
                shuffleInstructions.add(new ShuffleInstruction(Shuffle.CUT, Integer.parseInt(splitLine[1])));
            } catch (Exception e) {
                if (splitLine[1].equals("with")) {
                    shuffleInstructions.add(new ShuffleInstruction(Shuffle.DEAL_INCREMENT, Integer.parseInt(splitLine[3])));
                } else {
                    shuffleInstructions.add(new ShuffleInstruction(Shuffle.DEAL_STACK, 0));
                }
            }
        });

        int[] cards = IntStream.range(0, DECK_SIZE).toArray();
        print(cards, new ShuffleInstruction(Shuffle.DEAL_INCREMENT, 1));

        shuffleInstructions.forEach(shuffleInstruction -> {
            switch (shuffleInstruction.instruction) {
                case DEAL_STACK:
                    int i = 0;
                    for (int j = cards.length - 1; j > i; ++i) {
                        int tmp = cards[j];
                        cards[j] = cards[i];
                        cards[i] = tmp;
                        --j;
                    }
                    break;
                case CUT:
                    int[] temp = new int[cards.length];
                    if (shuffleInstruction.number > 0) {
                        int rest = DECK_SIZE - shuffleInstruction.number;
                        int cut = shuffleInstruction.number;
                        System.arraycopy(cards, 0, temp, rest, cut);
                        System.arraycopy(cards, shuffleInstruction.number, temp, 0, rest);
                    } else {
                        int rest = DECK_SIZE + shuffleInstruction.number;
                        int cut = Math.abs(shuffleInstruction.number);

                        System.arraycopy(cards, 0, temp, cut, rest);
                        System.arraycopy(cards, rest, temp, 0, cut);
                    }
                    System.arraycopy(temp, 0, cards, 0, cards.length);
                    break;
                case DEAL_INCREMENT:
                    int[] temp2 = new int[cards.length];
                    int current = 0;
                    for (long mod = 0; mod < DECK_SIZE * shuffleInstruction.number; mod += shuffleInstruction.number) {
                        temp2[(int) (mod % DECK_SIZE)] = cards[current++];
                    }
                    System.arraycopy(temp2, 0, cards, 0, cards.length);
                    break;
            }
            print(cards, shuffleInstruction);
        });

        for (int card = 0; card < DECK_SIZE; card++) {
            if (cards[card] == 2019) {
                System.out.println(card);
                return;
            }
        }
    }

    private static void print(int[] cards, ShuffleInstruction shuffleInstruction) {
        String collect = Arrays.stream(cards).boxed().map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(shuffleInstruction.instruction + " " + shuffleInstruction.number + ": " + collect);
    }
}

@AllArgsConstructor
class ShuffleInstruction {
    Shuffle instruction;
    int number;

    @Override
    public String toString() {
        return "ShuffleInstruction{" +
                "instruction=" + instruction +
                ", number=" + number +
                '}';
    }
}

enum Shuffle {
    DEAL_INCREMENT,
    CUT,
    DEAL_STACK
}