package advent2020;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class Advent25 {
    private static final BigInteger PRIME = BigInteger.valueOf(20201227);
    private static final BigInteger CARD_PUBLIC = BigInteger.valueOf(1614360);
    private static final BigInteger DOOR_PUBLIC = BigInteger.valueOf(7734663);
    private static final BigInteger SUBJECT = BigInteger.valueOf(7);

    public static void main(String[] args) {
        int cardLoop = getLoop(CARD_PUBLIC);
        int doorLoop = getLoop(DOOR_PUBLIC);

        log.info("Card loop: {}", cardLoop);
        log.info("Door loop: {}", doorLoop);

        log.info("Encryption: {}", DOOR_PUBLIC.pow(cardLoop)
                                              .mod(PRIME));
        log.info("Encryption: {}", CARD_PUBLIC.pow(doorLoop)
                                              .mod(PRIME));
    }

    private static int getLoop(BigInteger publicVal) {
        BigInteger val = BigInteger.ONE;
        int loop = 0;

        while (!val.equals(publicVal)) {
            val = val.multiply(SUBJECT)
                     .mod(PRIME);
            loop++;
        }
        return loop;
    }
}
