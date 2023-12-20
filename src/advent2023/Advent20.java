package advent2023;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static advent2023.Advent20.Signal.HIGH;
import static advent2023.Advent20.Signal.LOW;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.System.currentTimeMillis;
import static util.Util.fileStream;

@Slf4j
public class Advent20 {
    // https://adventofcode.com/2023/day/20

    static Map<String, Map<String, Long>> periodicity = newHashMap();
    static long cycle = 0L;

    public Long runP1(String file) {
        Map<String, Module> moduleMap = getModuleMap(file);
        initModules(moduleMap);

        AtomicLong countHigh = new AtomicLong(0L);
        AtomicLong countLow = new AtomicLong(0L);

        for (int i = 0; i < 1000; i++) {
            buttonPress(countHigh, countLow, moduleMap);
        }

        return countHigh.get() * countLow.get();
    }

    public Long runP2(String file) {
        Map<String, Module> moduleMap = getModuleMap(file);
        initModules(moduleMap);

        long count = 0L;

        long start = currentTimeMillis();
        boolean end = false;
        while (!end) {
            ++cycle;
            end = buttonPress(new AtomicLong(), new AtomicLong(), moduleMap);
            if (++count % 1000000 == 0) {
                log.info("{} - {}s", count, (currentTimeMillis() - start) / 1000);
                break;
            }
        }

        periodicity.forEach((module, moduleInputs) -> {
            Function<Map.Entry<String, Long>, String> formatPeriod = entry -> entry.getKey() + " " + entry.getValue();
            log.info("{} - with input periods {}", module, moduleInputs.entrySet().stream().map(formatPeriod).toList());
        });

        return count;
    }

    private static boolean buttonPress(AtomicLong countHigh, AtomicLong countLow, Map<String, Module> moduleMap) {
        Button button = new Button("button");
        button.output.add("broadcaster");

        List<Wire> signalsToProcess = button.send();
        while (!signalsToProcess.isEmpty()) {
            List<Wire> newSignals = newArrayList();

            signalsToProcess.forEach(wire -> {
                if (wire.signal == HIGH) {
                    countHigh.getAndIncrement();
                } else {
                    countLow.getAndIncrement();
                }

                List<Wire> receive = newArrayList();
                Module module = moduleMap.get(wire.destination);

                if (module != null) {
                    receive = module.receive(wire.from, wire.signal);
                }

                newSignals.addAll(receive);
            });

            signalsToProcess = newSignals;

            if (signalsToProcess.stream().anyMatch(wire -> wire.destination.equals("rx") && wire.signal == LOW)) {
                return true;
            }
        }
        return false;
    }

    private static void initModules(Map<String, Module> moduleMap) {
        moduleMap.forEach((name, module) -> {
            module.output.forEach(down -> {
                Module downstream = moduleMap.get(down);
                if (downstream != null) {
                    downstream.input.add(name);
                }
            });

            if (module instanceof Conjunction) {
                ((Conjunction) module).init();
            }
        });
    }

    private static Map<String, Module> getModuleMap(String file) {
        Map<String, Module> moduleMap = newHashMap();
        fileStream(file).forEach(line -> {
            String[] split = line.split(" -> ");

            List<String> outputs = Arrays.stream(split[1].split(", ")).toList();
            if (split[0].equals("broadcaster")) {
                Broadcast broadcast = new Broadcast("broadcaster");
                broadcast.output = outputs;
                moduleMap.put("broadcaster", broadcast);
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
        return moduleMap;
    }

    static class Button extends Module {
        public Button(String name) {
            super(name);
        }

        public List<Wire> send() {
            return send(LOW);
        }

        @Override
        List<Wire> receive(String from, Signal signal) {
            return send(LOW);
        }

    }

    static class Broadcast extends Module {
        public Broadcast(String name) {
            super(name);
        }

        @Override
        List<Wire> receive(String from, Signal signal) {
            return send(signal);
        }

    }

    static class Conjunction extends Module {
        Map<String, Signal> memory = newHashMap();

        public Conjunction(String name) {
            super(name);
        }

        void init() {
            input.forEach(input -> memory.put(input, LOW));
        }

        @Override
        List<Wire> receive(String from, Signal signal) {
            // When a pulse is received, the conjunction module first updates its memory for that input.
            memory.put(from, signal);

            if (signal == HIGH) {
                if (periodicity.get(name) == null) {
                    periodicity.put(name, newHashMap());
                }
                periodicity.get(name).computeIfAbsent(from, k -> cycle);
            }

            // Then, if it remembers high pulses for all inputs, it sends a low pulse; otherwise, it sends a high pulse.
            Signal toSend;
            if (memory.values().stream().allMatch(memSignal -> memSignal == HIGH)) {
                toSend = LOW;
            } else {
                toSend = HIGH;
            }
            return send(toSend);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Conjunction that = (Conjunction) o;

            if (!memory.equals(that.memory)) return false;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            int result = memory.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    static class FlipFlop extends Module {
        boolean isOn;

        public FlipFlop(String name) {
            super(name);
        }

        @Override
        List<Wire> receive(String from, Signal signal) {
            if (signal == LOW) {
                // If it was off, it turns on and sends a high pulse. If it was on, it turns off and sends a low pulse.
                isOn = !isOn;
                return send(isOn ? HIGH : LOW);
            }
            return newArrayList();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FlipFlop flipFlop = (FlipFlop) o;

            if (isOn != flipFlop.isOn) return false;
            return name.equals(flipFlop.name);
        }

        @Override
        public int hashCode() {
            int result = (isOn ? 1 : 0);
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    static abstract class Module {
        String name;
        List<String> input = newArrayList();
        List<String> output = newArrayList();

        public Module(String name) {
            this.name = name;
        }

        abstract List<Wire> receive(String from, Signal signal);

        protected List<Wire> send(Signal signal) {
            return output.stream().map(module -> new Wire(name, signal, module)).toList();
        }
    }

    record Wire(String from, Signal signal, String destination) {

    }

    enum Signal {
        HIGH,
        LOW
    }
}