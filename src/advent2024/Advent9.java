package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent9 {
    // https://adventofcode.com/2024/day/9

    public Long runP1(String file) {
        List<Integer> disk = parseInputP1(file);

        while (disk.contains(-1)) {
            disk.set(disk.indexOf(-1), disk.getLast()); // move last block to first empty space
            disk = disk.subList(0, disk.size() - 1); // remove last block
        }

        List<Integer> finalDisk = disk;
        return range(0, disk.size())
                .mapToLong(i -> finalDisk.get(i) * i)
                .sum();
    }

    public Long runP2(String file) {
        List<DiskBlock> diskBlocks = parseInputP2(file);

        range(0, diskBlocks.size()).boxed()
                .sorted(reverseOrder())
                .forEach(elementIndex -> findAndMoveToEmptySpace(elementIndex, diskBlocks));

        AtomicInteger pos = new AtomicInteger();
        return diskBlocks.stream().mapToLong(block -> {
            long checksum = blockChecksum(block, pos.get());
            pos.addAndGet(block.size);
            return checksum;
        }).sum();
    }

    private static void findAndMoveToEmptySpace(int elementIndex, List<DiskBlock> diskBlocks) {
        DiskBlock currentElement = diskBlocks.get(elementIndex);
        if (currentElement.label != -1) {
            for (int spaceIndex = 0; spaceIndex < elementIndex; spaceIndex++) {
                DiskBlock emptySpace = diskBlocks.get(spaceIndex);
                if (emptySpace.label == -1 && emptySpace.size >= currentElement.size) {
                    moveBlockToEmptySpace(diskBlocks, currentElement, emptySpace, spaceIndex);
                    break;
                }
            }
        }
    }

    private static void moveBlockToEmptySpace(List<DiskBlock> diskBlocks, DiskBlock currentElement, DiskBlock space, int j) {
        int oldIndex = diskBlocks.indexOf(currentElement);
        diskBlocks.remove(currentElement);
        if (oldIndex < diskBlocks.size() - 1) {
            diskBlocks.add(oldIndex, new DiskBlock(currentElement.size, -1));
        }

        diskBlocks.remove(space);
        diskBlocks.add(j, currentElement);
        if (space.size - currentElement.size > 0) {
            diskBlocks.add(j + 1, new DiskBlock(space.size - currentElement.size, -1));
        }
    }

    public Long blockChecksum(DiskBlock block, int position) {
        int sum = range(position, position + block.size).sum();
        return (long) (block.label == -1 ? 0 : block.label) * sum;
    }

    private static List<Integer> parseInputP1(String file) {
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
        return disk;
    }

    private static List<DiskBlock> parseInputP2(String file) {
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
