package advent2023;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Queues.newArrayDeque;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent20 {
    public static final String BROADCASTER = "broadcaster";
    public static final String BUTTON = "button";
    // https://adventofcode.com/2023/day/20

    Map<String, Map<String, Long>> periodicity = newHashMap();
    long cycle = 0L;
    long countHigh;
    long countLow;

    Queue<Module> toProcess = newArrayDeque();
    Map<String, Module> moduleMap = newHashMap();

    public Long runP1(String file) {
        initModules(file);

        Button button = new Button(BUTTON);
        button.output.add(BROADCASTER);

        for (int i = 0; i < 1000; i++) {
            buttonPress(button);
        }

        return countHigh * countLow;
    }

    public Long runP2(String file) {
        initModules(file);

        Button button = new Button(BUTTON);
        button.output.add(BROADCASTER);

        long conjCount = moduleMap.values().stream()
                .filter(module -> module instanceof Conjunction)
                .count();

        while (periodicity.size() < conjCount - 1) {
            buttonPress(button);
        }

        return periodicity.entrySet().stream()
                .map(Map.Entry::getValue)
                .flatMap(map -> map.values().stream())
                .reduce(1L, (left, right) -> left * right);
    }

    private void buttonPress(Module button) {
        ++cycle;
        toProcess.add(button);
        while (!toProcess.isEmpty()) {
            toProcess.poll().receive();
        }
    }

    private void initModules(String file) {
        fileStream(file).forEach(line -> {
            String[] split = line.split(" -> ");

            List<String> outputs = Arrays.stream(split[1].split(", ")).toList();
            if (split[0].equals(BROADCASTER)) {
                Broadcast broadcast = new Broadcast(BROADCASTER);
                broadcast.output = outputs;
                moduleMap.put(BROADCASTER, broadcast);
            } else {
                String name = split[0].substring(1);
                if (split[0].charAt(0) == '%') {
                    FlipFlop flipFlop = new FlipFlop(name);
                    flipFlop.output = outputs;
                    moduleMap.put(name, flipFlop);
                } else if (split[0].charAt(0) == '&') {
                    Conjunction conjunction = new Conjunction(name);
                    conjunction.output = outputs;
                    moduleMap.put(name, conjunction);
                }
            }
        });

        moduleMap.forEach((key, module) -> {
            if (module instanceof Conjunction) {
                moduleMap.values().stream()
                        .filter(parent -> parent.output.contains(key))
                        .forEach(parent -> ((Conjunction) module).memory.put(parent.name, false));
            }
        });
    }

    class Button extends Module {
        public Button(String name) {
            super(name);
        }

        @Override
        void receive() {
            send(false);
        }

    }

    class Broadcast extends Module {
        public Broadcast(String name) {
            super(name);
        }

        @Override
        void receive() {
            send(input.removeFirst().isHigh);
        }

    }

    class Conjunction extends Module {
        Map<String, Boolean> memory = newHashMap();

        public Conjunction(String name) {
            super(name);
        }

        @Override
        void receive() {
            Signal signal = input.removeFirst();
            memory.put(signal.from, signal.isHigh);
            boolean isHigh = !memory.values().stream().allMatch(b -> b);

            if (!isHigh) {
                periodicity.computeIfAbsent(name, k -> newHashMap());
                periodicity.get(name).computeIfAbsent(this.name, k -> cycle);
            }

            send(isHigh);
        }

    }

    class FlipFlop extends Module {
        boolean isOn;

        public FlipFlop(String name) {
            super(name);
        }

        @Override
        void receive() {
            Signal signal = input.removeFirst();
            if (!signal.isHigh) {
                isOn = !isOn;
                send(isOn);
            }
        }
    }

    abstract class Module {
        String name;
        List<Signal> input = newArrayList();
        List<String> output = newArrayList();

        public Module(String name) {
            this.name = name;
        }

        abstract void receive();

        protected void send(boolean isHigh) {
            output.forEach(output -> {
                if (isHigh) countHigh++;
                else countLow++;

                Module to = moduleMap.get(output);

                if (to != null) {
                    to.input.add(new Signal(this.name, isHigh));
                    toProcess.add(to);
                }
            });
        }
    }

    record Signal(String from, boolean isHigh) {
    }
}