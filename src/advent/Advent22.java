package advent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent22 {
    public static final BigInteger DECK_SIZE = BigInteger.valueOf(119315717514047L);
    public static final BigInteger STEPS = BigInteger.valueOf(101741582076661L);
    public static final BigInteger CUT = BigInteger.valueOf(1841799366945L);
    public static final BigInteger INCREMENT = BigInteger.valueOf(93922407988235L);

    public static void main(String[] args) {
        BigInteger a = INCREMENT.modInverse(DECK_SIZE);

        // a^n
        BigInteger aToN = a.modPow(STEPS, DECK_SIZE);

        // 1 - a^n
        BigInteger oneMinus_aToN = BigInteger.ONE.subtract(aToN)
                                                 .mod(DECK_SIZE);

        // 1 - a
        BigInteger oneMinusA = BigInteger.ONE.subtract(a)
                                             .mod(DECK_SIZE);

        // (1 - a^n) / (1 - a)
        BigInteger bFactor = oneMinus_aToN.multiply(oneMinusA.modInverse(DECK_SIZE));

        // a^n * 2020 + CUT * a * (1 - a^n) / (1 - a)
        BigInteger card = aToN.multiply(BigInteger.valueOf(2020))
                              .mod(DECK_SIZE)
                              .add(bFactor.multiply(CUT.multiply(a)
                                                       .mod(DECK_SIZE))
                                          .mod(DECK_SIZE))
                              .mod(DECK_SIZE);

        log.info("Card: {}", card);
    }
}

@AllArgsConstructor
class ShuffleInstruction {
    Shuffle instruction;
    BigInteger number;

    @Override
    public String toString() {
        switch (instruction) {
            case DEAL_INCREMENT:
                return "deal with increment " + number;
            case CUT:
                return "cut " + number;
            case DEAL_STACK:
                return "deal into new stack";
            default:
                return "";
        }
    }
}

enum Shuffle {
    DEAL_INCREMENT,
    CUT,
    DEAL_STACK
}

class Advent22Helper {

    public static void main(String[] args) throws IOException {
        ArrayList<ShuffleInstruction> shuffleInstructions = getShuffleInstructions();

        while (shuffleInstructions.size() > 3) {
            ArrayList<ShuffleInstruction> newInstructions = newArrayList();
            int i;
            for (i = 0; i < shuffleInstructions.size() - 1; i++) {
                BigInteger x = shuffleInstructions.get(i).number;
                BigInteger y = shuffleInstructions.get(i + 1).number;

                Shuffle current = shuffleInstructions.get(i).instruction;
                Shuffle next = shuffleInstructions.get(i + 1).instruction;

                if (current == Shuffle.DEAL_STACK) {
                    newInstructions.add(new ShuffleInstruction(Shuffle.DEAL_INCREMENT, Advent22.DECK_SIZE.subtract(BigInteger.ONE)));
                    newInstructions.add(new ShuffleInstruction(Shuffle.CUT, BigInteger.ONE));
                } else if (current == Shuffle.CUT && next == Shuffle.CUT) {
                    newInstructions.add(new ShuffleInstruction(Shuffle.CUT, x.add(y)
                                                                             .mod(Advent22.DECK_SIZE)));
                    i++;
                } else if (current == Shuffle.DEAL_INCREMENT && next == Shuffle.DEAL_INCREMENT) {
                    newInstructions.add(new ShuffleInstruction(Shuffle.DEAL_INCREMENT, x.multiply(y)
                                                                                        .mod(Advent22.DECK_SIZE)));
                    i++;
                } else if (current == Shuffle.CUT && next == Shuffle.DEAL_INCREMENT) {
                    newInstructions.add(new ShuffleInstruction(Shuffle.DEAL_INCREMENT, y));
                    newInstructions.add(new ShuffleInstruction(Shuffle.CUT, x.multiply(y)
                                                                             .mod(Advent22.DECK_SIZE)));
                    i++;
                } else {
                    newInstructions.add(shuffleInstructions.get(i));
                }
            }
            if (i == shuffleInstructions.size() - 1) {
                newInstructions.add(shuffleInstructions.get(i));
            }
            shuffleInstructions = newInstructions;
        }

        Stream<String> stringStream = shuffleInstructions.stream()
                                                         .map(ShuffleInstruction::toString);
        Files.write(Paths.get(".\\resources", "advent22"), (Iterable<String>) stringStream::iterator);
    }

    private static ArrayList<ShuffleInstruction> getShuffleInstructions() {
        ArrayList<ShuffleInstruction> shuffleInstructions = newArrayList();
        Util.fileStream("advent22")
            .forEach(s -> {
                String[] splitLine = s.split(" ");
                try {
                    BigInteger cutValue = BigInteger.valueOf(Long.parseLong(splitLine[1]));
                    cutValue = cutValue.compareTo(BigInteger.ZERO) < 0 ? Advent22.DECK_SIZE.add(cutValue) : cutValue;
                    shuffleInstructions.add(new ShuffleInstruction(Shuffle.CUT, cutValue));
                } catch (Exception e) {
                    if (splitLine[1].equals("with")) {
                        BigInteger increment = BigInteger.valueOf(Long.parseLong(splitLine[3]));
                        shuffleInstructions.add(new ShuffleInstruction(Shuffle.DEAL_INCREMENT, increment));
                    } else {
                        shuffleInstructions.add(new ShuffleInstruction(Shuffle.DEAL_STACK, BigInteger.ZERO));
                    }
                }
            });
        return shuffleInstructions;
    }
}