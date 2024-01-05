package advent2023;

import com.google.common.base.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.compare;
import static java.lang.Math.pow;
import static java.util.Arrays.stream;

@Slf4j
public class Advent7 {
    // https://adventofcode.com/2023/day/7
    String cards = "23456789TJQKA";
    List<String> cardsList = stream(cards.split("")).toList();
    String cardsWithJoker = "J23456789TQKA";
    List<String> cardsListWithJoker = stream(cardsWithJoker.split("")).toList();

    public Long runP1(String file) {
        return run(file, handCompare());
    }

    public Long runP2(String file) {
        return run(file, handCompareWithJoker());
    }

    private long run(String file, Comparator<String> comparator) {
        Map<String, Long> bids = getBids(file);

        List<String> orderedHands = bids.keySet().stream().sorted(comparator).collect(Collectors.toList());

        long pot = 0;
        for (int hand = 0; hand < orderedHands.size(); hand++) {
            pot += (hand + 1) * bids.get(orderedHands.get(hand));
        }

        return pot;
    }

    private static Map<String, Long> getBids(String file) {
        Map<String, Long> bids = newHashMap();
        InputUtils.fileStream(file).forEach(s -> {
            String[] split = s.split(" ");
            bids.put(split[0], Long.parseLong(split[1]));
        });
        return bids;
    }

    Comparator<String> handCompare() {
        return (hand1, hand2) -> {
            int compare = compare(getHandScore(hand1), getHandScore(hand2));
            return compare != 0 ? compare : compareHandsOfEqualRank(hand1, hand2, cardsList);
        };
    }

    Comparator<String> handCompareWithJoker() {
        return (hand1, hand2) -> {
            int compare = compare(getHandScore(convertJokerIntoBestCard(hand1)), getHandScore(convertJokerIntoBestCard(hand2)));
            return compare != 0 ? compare : compareHandsOfEqualRank(hand1, hand2, cardsListWithJoker);
        };
    }

    /**
     * math way of ordering hands by raising the card occurrences ^3
     * five of a kind = 5^3 = 125
     * four of a kind = 4^3 + 1^3 = 65
     * full house = 3^3 + 2^3 = 35
     * three of a kind = 3^3 + 1^3 + 1^3 = 29
     * two pair = 2^3 + 2^3 + 1^3 = 17
     * one pair = 2^3 + 1^3 + 1^3 + 1^3 = 11
     * high card = 1^3 + 1^3 + 1^3 + 1^3 + 1^3 = 5
     */
    private static int getHandScore(String hand) {
        return stream(hand.split(""))
                .distinct()
                .mapToInt(s -> (int) pow(CharMatcher.is(s.charAt(0)).countIn(hand), 3))
                .sum();
    }

    int compareHandsOfEqualRank(String hand1, String hand2, List<String> cards) {
        for (int i = 0; i < hand1.length(); i++) {
            int c = compare(cards.indexOf(hand1.charAt(i) + ""), cards.indexOf(hand2.charAt(i) + ""));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    String convertJokerIntoBestCard(String hand) {
        List<String> newHands = newArrayList();

        // replace joker with every other card
        stream(cardsWithJoker.replace("J", "").split(""))
                .map(card -> hand.replace("J", card))
                .forEach(newHands::add);

        newHands.sort(handCompare());
        return newHands.getLast();
    }
}
