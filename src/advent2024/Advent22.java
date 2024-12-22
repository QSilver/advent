package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent22 {
    // https://adventofcode.com/2024/day/22

    public Long runP1(String file) {
        return fileStream(file).mapToLong(value -> {
            long secret = Long.parseLong(value);
            for (int i = 0; i < 2000; i++) {
                secret = evolve(secret);
            }
            return secret;
        }).sum();
    }

    public Long runP2(String file) {
        List<List<Long>> monkeyPrices = newArrayList();
        fileStream(file).forEach(value -> {
            List<Long> prices = newArrayList();
            long secret = Long.parseLong(value);
            for (int i = 1; i <= 2000; i++) {
                prices.add(secret % 10);
                secret = evolve(secret);
            }
            monkeyPrices.add(prices);
        });

        Set<Deltas> sequences = newHashSet();
        List<LinkedHashMap<Deltas, Long>> sequenceToPrice = newArrayList();
        monkeyPrices.forEach(priceList -> {
            LinkedHashMap<Deltas, Long> monkeyDeltaMap = new LinkedHashMap<>();
            for (int i = 4; i < priceList.size(); i++) {
                Long four = priceList.get(i - 4);
                Long three = priceList.get(i - 3);
                Long two = priceList.get(i - 2);
                Long one = priceList.get(i - 1);
                Long current = priceList.get(i);
                Deltas delta = new Deltas(three - four, two - three, one - two, current - one);

                if (!monkeyDeltaMap.containsKey(delta)) {
                    monkeyDeltaMap.put(delta, current);
                    sequences.add(delta);
                }
            }
            sequenceToPrice.add(monkeyDeltaMap);
        });

        AtomicLong maxBestPrice = new AtomicLong(0L);
        for (Deltas delta : sequences) {
            AtomicLong bestPriceForDelta = new AtomicLong(0L);
            sequenceToPrice.forEach(monkey -> {
                if (monkey.get(delta) != null) {
                    bestPriceForDelta.addAndGet(monkey.get(delta));
                }
            });
            if (bestPriceForDelta.get() > maxBestPrice.get()) {
                maxBestPrice.set(bestPriceForDelta.get());
            }
        }

        return maxBestPrice.get();
    }

    private long evolve(long secret) {
        long step1 = (secret ^ (secret * 64)) % 16777216;
        long step2 = (step1 ^ (step1 / 32)) % 16777216;
        long step3 = (step2 ^ (step2 * 2048)) % 16777216;
        return step3;
    }

    private record Deltas(long three, long two, long one, long current) {

    }
}
