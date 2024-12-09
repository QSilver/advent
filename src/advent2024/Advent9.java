package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent9 {
    // https://adventofcode.com/2024/day/9

    public Long runP1(String file) {
        String collect = fileStream(file).collect(joining());

        List<Integer> disk = newArrayList();
        int id = 0;
        for (int i = 0; i < collect.length(); i++) {
            int blockSize = parseInt(valueOf(collect.charAt(i)));
            if (i % 2 == 0) {
                for (int j = 0; j < blockSize; j++) {
                    disk.add(id);
                }
                id++;
            } else {
                for (int j = 0; j < blockSize; j++) {
                    disk.add(-1);
                }
            }
        }

        while (disk.contains(-1)) {
            int i = disk.indexOf(-1);
            disk.set(i, disk.getLast());
            disk = disk.subList(0, disk.size() - 1);
        }

        long sum = 0;
        for (int i = 0; i < disk.size(); i++) {
            sum += (disk.get(i) * i);
        }

        return sum;
    }

    public Long runP2(String file) {
        List<DiskBlock> diskBlocks = parseInput(file);

        for (int i = diskBlocks.size() - 1; i >= 0; i--) {
            DiskBlock element = diskBlocks.get(i);
            if (element.label != -1) {
                for (int j = 0; j < i; j++) {
                    DiskBlock space = diskBlocks.get(j);
                    if (space.label == -1 && space.size >= element.size) {
                        int oldIndex = diskBlocks.indexOf(element);
                        diskBlocks.remove(element);
                        if (oldIndex < diskBlocks.size() - 1) {
                            diskBlocks.add(oldIndex, new DiskBlock(element.size, -1));
                        }

                        diskBlocks.remove(space);
                        diskBlocks.add(j, element);
                        if (space.size - element.size > 0) {
                            diskBlocks.add(j + 1, new DiskBlock(space.size - element.size, -1));
                        }
                        break;
                    }
                }
            }
        }

        AtomicInteger pos = new AtomicInteger();
        return diskBlocks.stream().mapToLong(block -> {
            long checksum = blockChecksum(block, pos.get());
            pos.addAndGet(block.size);
            return checksum;
        }).sum();
    }

    public Long blockChecksum(DiskBlock block, int position) {
        int sum = IntStream.range(position, position + block.size).sum();
        return (long) (block.label == -1 ? 0 : block.label) * sum;
    }

    private static List<DiskBlock> parseInput(String file) {
        String collect = fileStream(file).collect(joining());

        List<DiskBlock> diskBlocks = newArrayList();

        int id = 0;
        for (int i = 0; i < collect.length(); i++) {
            int blockSize = parseInt(valueOf(collect.charAt(i)));
            diskBlocks.add(new DiskBlock(blockSize, i % 2 == 0 ? id++ : -1));
        }
        return diskBlocks;
    }

    public record DiskBlock(int size, int label) {
    }
}
