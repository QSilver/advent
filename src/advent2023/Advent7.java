package advent2023;

import com.google.common.base.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent7 {
    String cards = "23456789TJQKA";
    List<String> cardsList = Arrays.stream(cards.split("")).toList();
    String cardsWithJoker = "J23456789TQKA";
    List<String> cardsListWithJoker = Arrays.stream(cardsWithJoker.split("")).toList();

    public Long runP1(String file) {
        Map<String, Long> bids = getBids(file);

        List<String> orderedHands = bids.keySet().stream().sorted(handCompare()).collect(Collectors.toList());

        long pot = 0;
        for (int hand = 0; hand < orderedHands.size(); hand++) {
            pot += (hand + 1) * bids.get(orderedHands.get(hand));
        }

        return pot;
    }

    private static Map<String, Long> getBids(String file) {
        Map<String, Long> bids = newHashMap();
        Util.fileStream(file).forEach(s -> {
            String[] split = s.split(" ");
            bids.put(split[0], Long.parseLong(split[1]));
        });
        return bids;
    }

    Comparator<String> handCompare() {
        return (hand1, hand2) -> {
            long hand1DistinctCount = hand1.chars().distinct().count();
            long hand2DistinctCount = hand2.chars().distinct().count();

            if (hand1DistinctCount < hand2DistinctCount) {
                return 1;
            }
            if (hand1DistinctCount > hand2DistinctCount) {
                return -1;
            }

            // five of a kind, one pair, or high card = can compare hand directly
            if (hand1DistinctCount == 1 || hand1DistinctCount == 4 || hand1DistinctCount == 5) {
                return compareHandsOfEqualRank(hand1, hand2);
            }

            // four of a kind OR full house
            if (hand1DistinctCount == 2) {
                Set<String> hand1Cards = Arrays.stream(hand1.split("")).collect(Collectors.toSet());
                Set<String> hand2Cards = Arrays.stream(hand2.split("")).collect(Collectors.toSet());

                // four of a kind = 4
                // full house = 3
                int hand1Score = hand1Cards.stream()
                        .mapToInt(s -> CharMatcher.is(s.charAt(0)).countIn(hand1))
                        .max().orElseThrow();
                int hand2Score = hand2Cards.stream()
                        .mapToInt(s -> CharMatcher.is(s.charAt(0)).countIn(hand2))
                        .max().orElseThrow();

                int compare = Integer.compare(hand1Score, hand2Score);
                if (compare != 0) {
                    return compare;
                }

                return compareHandsOfEqualRank(hand1, hand2);
            }

            // three of a kind OR two pair
            if (hand1DistinctCount == 3) {
                Set<String> hand1Cards = Arrays.stream(hand1.split("")).collect(Collectors.toSet());
                Set<String> hand2Cards = Arrays.stream(hand2.split("")).collect(Collectors.toSet());

                // three of a kind = 3
                // two pair = 2
                int hand1Score = hand1Cards.stream()
                        .mapToInt(s -> CharMatcher.is(s.charAt(0)).countIn(hand1))
                        .max().orElseThrow();
                int hand2Score = hand2Cards.stream()
                        .mapToInt(s -> CharMatcher.is(s.charAt(0)).countIn(hand2))
                        .max().orElseThrow();

                int compare = Integer.compare(hand1Score, hand2Score);
                if (compare != 0) {
                    return compare;
                }

                return compareHandsOfEqualRank(hand1, hand2);
            }

            return 0;
        };
    }

    int compareHandsOfEqualRank(String hand1, String hand2) {
        for (int i = 0; i < hand1.length(); i++) {
            int c = Integer.compare(cardsList.indexOf(hand1.charAt(i) + ""), cardsList.indexOf(hand2.charAt(i) + ""));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    Comparator<String> handCompareWithJoker() {
        return (hand1, hand2) -> {
            String hand1Convert = convertJokerIntoBestCard(hand1);
            String hand2Convert = convertJokerIntoBestCard(hand2);

            long hand1DistinctCount = hand1Convert.chars().distinct().count();
            long hand2DistinctCount = hand2Convert.chars().distinct().count();

            if (hand1DistinctCount < hand2DistinctCount) {
                return 1;
            }
            if (hand1DistinctCount > hand2DistinctCount) {
                return -1;
            }

            // five of a kind, one pair, or high card = can compare hand directly
            if (hand1DistinctCount == 1 || hand1DistinctCount == 4 || hand1DistinctCount == 5) {
                return compareHandsOfEqualRankWithJoker(hand1, hand2);
            }

            // four of a kind OR full house
            if (hand1DistinctCount == 2) {
                Set<String> hand1Cards = Arrays.stream(hand1Convert.split("")).collect(Collectors.toSet());
                Set<String> hand2Cards = Arrays.stream(hand2Convert.split("")).collect(Collectors.toSet());

                // four of a kind = 4
                // full house = 3
                int hand1Score = hand1Cards.stream()
                        .mapToInt(s -> CharMatcher.is(s.charAt(0)).countIn(hand1Convert))
                        .max().orElseThrow();
                int hand2Score = hand2Cards.stream()
                        .mapToInt(s -> CharMatcher.is(s.charAt(0)).countIn(hand2Convert))
                        .max().orElseThrow();

                int compare = Integer.compare(hand1Score, hand2Score);
                if (compare != 0) {
                    return compare;
                }

                return compareHandsOfEqualRankWithJoker(hand1, hand2);
            }

            // three of a kind OR two pair
            if (hand1DistinctCount == 3) {
                Set<String> hand1Cards = Arrays.stream(hand1Convert.split("")).collect(Collectors.toSet());
                Set<String> hand2Cards = Arrays.stream(hand2Convert.split("")).collect(Collectors.toSet());

                // three of a kind = 3
                // two pair = 2
                int hand1Score = hand1Cards.stream()
                        .mapToInt(s -> CharMatcher.is(s.charAt(0)).countIn(hand1Convert))
                        .max().orElseThrow();
                int hand2Score = hand2Cards.stream()
                        .mapToInt(s -> CharMatcher.is(s.charAt(0)).countIn(hand2Convert))
                        .max().orElseThrow();

                int compare = Integer.compare(hand1Score, hand2Score);
                if (compare != 0) {
                    return compare;
                }

                return compareHandsOfEqualRankWithJoker(hand1, hand2);
            }

            return 0;
        };
    }

    int compareHandsOfEqualRankWithJoker(String hand1, String hand2) {
        for (int i = 0; i < hand1.length(); i++) {
            int c = Integer.compare(cardsListWithJoker.indexOf(hand1.charAt(i) + ""), cardsListWithJoker.indexOf(hand2.charAt(i) + ""));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    String convertJokerIntoBestCard(String hand) {
        List<String> newHands = newArrayList();

        List<String> split = Arrays.stream(cardsWithJoker.replace("J", "").split("")).toList();
        split.forEach(card -> {
            String j1 = hand.replace("J", card);
            newHands.add(j1);
        });

        newHands.sort(handCompare());
        return newHands.get(newHands.size() - 1);
    }

    public Long runP2(String file) {
        Map<String, Long> bids = getBids(file);

        List<String> orderedHands = bids.keySet().stream().sorted(handCompareWithJoker()).collect(Collectors.toList());

        long pot = 0;
        for (int hand = 0; hand < orderedHands.size(); hand++) {
            pot += (hand + 1) * bids.get(orderedHands.get(hand));
        }

        return pot;
    }
}
