package advent2023;

import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Queues.newArrayDeque;
import static java.lang.Integer.parseInt;
import static java.util.stream.IntStream.range;
import static util.Util.fileStream;

@Slf4j
public class Advent19 {
    // https://adventofcode.com/2023/day/19

    public Long runP1(String file) {
        String[] split = fileStream(file).collect(Collectors.joining("\n")).split("\n\n");

        Map<String, Workflow> workflowMap = newHashMap();
        Arrays.stream(split[0].split("\n")).forEach(workflow -> {
            String[] split1 = workflow.split("\\{");
            String[] split2 = split1[1].replace("}", "").split(",");

            List<WorkflowRule> workflowRules = newArrayList();
            Arrays.stream(split2).forEach(rule -> {
                if (rule.contains(":")) {
                    String[] split3 = rule.split(":");
                    String variable = split3[0].charAt(0) + "";
                    boolean lessThan = split3[0].charAt(1) == '<';
                    int threshold = parseInt(split3[0].substring(2));
                    String goToWorkflow = split3[1];
                    workflowRules.add(new WorkflowRule(variable, lessThan, threshold, goToWorkflow));
                } else
                    workflowRules.add(new WorkflowRule(null, true, -1, rule));
            });

            workflowMap.put(split1[0], new Workflow(split1[0], workflowRules));
        });

        List<MachinePart> machineParts = Arrays.stream(split[1].split("\\n")).map(part -> {
            String[] split1 = part.replace("{", "").replace("}", "").split(",");

            int x = parseInt(split1[0].substring(2));
            int m = parseInt(split1[1].substring(2));
            int a = parseInt(split1[2].substring(2));
            int s = parseInt(split1[3].substring(2));
            return new MachinePart(x, m, a, s);
        }).toList();

        int sum = machineParts.stream().mapToInt(machinePart -> {
            String ruleName = "in";
            while (true) {
                Workflow workflow = workflowMap.get(ruleName);

                if (workflow == null) {
                    if (ruleName.equals("A")) {
                        return machinePart.x + machinePart.m + machinePart.a + machinePart.s;
                    } else if (ruleName.equals("R")) {
                        return 0;
                    }
                }

                processRules:
                for (WorkflowRule rule : workflow.conditions()) {
                    if (rule.variable == null) {
                        ruleName = rule.goToWorkflow;
                        break;
                    }

                    switch (rule.variable()) {
                        case "x" -> {
                            if (rule.lessThan()) {
                                if (machinePart.x < rule.threshold) {
                                    ruleName = rule.goToWorkflow;
                                    break processRules;
                                }
                            } else {
                                if (machinePart.x > rule.threshold) {
                                    ruleName = rule.goToWorkflow;
                                    break processRules;
                                }
                            }
                        }
                        case "m" -> {
                            if (rule.lessThan()) {
                                if (machinePart.m < rule.threshold) {
                                    ruleName = rule.goToWorkflow;
                                    break processRules;
                                }
                            } else {
                                if (machinePart.m > rule.threshold) {
                                    ruleName = rule.goToWorkflow;
                                    break processRules;
                                }
                            }
                        }
                        case "a" -> {
                            if (rule.lessThan()) {
                                if (machinePart.a < rule.threshold) {
                                    ruleName = rule.goToWorkflow;
                                    break processRules;
                                }
                            } else {
                                if (machinePart.a > rule.threshold) {
                                    ruleName = rule.goToWorkflow;
                                    break processRules;
                                }
                            }
                        }
                        case "s" -> {
                            if (rule.lessThan()) {
                                if (machinePart.s < rule.threshold) {
                                    ruleName = rule.goToWorkflow;
                                    break processRules;
                                }
                            } else {
                                if (machinePart.s > rule.threshold) {
                                    ruleName = rule.goToWorkflow;
                                    break processRules;
                                }
                            }
                        }
                        default -> {
                            ruleName = rule.goToWorkflow;
                            break processRules;
                        }
                    }
                }
            }
        }).sum();

        return (long) sum;
    }

    public Long runP2(String file) {
        String[] split = fileStream(file).collect(Collectors.joining("\n")).split("\n\n");

        Map<String, Workflow> workflowMap = newHashMap();
        Arrays.stream(split[0].split("\n")).forEach(workflow -> {
            String[] split1 = workflow.split("\\{");
            String[] split2 = split1[1].replace("}", "").split(",");

            List<WorkflowRule> workflowRules = newArrayList();
            Arrays.stream(split2).forEach(rule -> {
                if (rule.contains(":")) {
                    String[] split3 = rule.split(":");
                    String variable = split3[0].charAt(0) + "";
                    boolean lessThan = split3[0].charAt(1) == '<';
                    int threshold = parseInt(split3[0].substring(2));
                    String goToWorkflow = split3[1];
                    workflowRules.add(new WorkflowRule(variable, lessThan, threshold, goToWorkflow));
                } else
                    workflowRules.add(new WorkflowRule(null, true, -1, rule));
            });

            workflowMap.put(split1[0], new Workflow(split1[0], workflowRules));
        });

        Queue<Workflow> toProcess = newArrayDeque();
        Map<Workflow, WorkflowInterval> allowed = newHashMap();

        Workflow in = workflowMap.get("in");
        toProcess.add(in);
        allowed.put(in, new WorkflowInterval(
                range(1, 4001).boxed().collect(Collectors.toSet()),
                range(1, 4001).boxed().collect(Collectors.toSet()),
                range(1, 4001).boxed().collect(Collectors.toSet()),
                range(1, 4001).boxed().collect(Collectors.toSet())));

        AtomicLong accepted = new AtomicLong(0L);
        while (!toProcess.isEmpty()) {
            Workflow workflow = toProcess.remove();
            AtomicReference<WorkflowInterval> interval = new AtomicReference<>(allowed.get(workflow));

            AtomicReference<String> previous = new AtomicReference<>(null);
            workflow.conditions.forEach(rule -> {
                if (previous.get() != null) {
                    interval.set(interval.get().invert(previous.get()));
                }

                if (rule.variable != null) {
                    interval.set(interval.get().restrict(rule));
                }

                if (rule.goToWorkflow.equals("A")) {
                    long delta = ((long) interval.get().x.size()) * interval.get().m.size() * interval.get().a.size() * interval.get().s.size();
                    accepted.addAndGet(delta);
                } else if (!rule.goToWorkflow.equals("R")) {
                    Workflow goTo = workflowMap.get(rule.goToWorkflow);
                    toProcess.add(goTo);
                    allowed.put(goTo, interval.get());
                }
                previous.set(rule.variable);
            });
        }

        return accepted.get();
    }

    record Workflow(String name, List<WorkflowRule> conditions) {
    }

    @With
    record WorkflowInterval(Set<Integer> x, Set<Integer> m, Set<Integer> a, Set<Integer> s) {
        WorkflowInterval invert(String variable) {
            switch (variable) {
                case "x" -> {
                    return this.withX(range(1, 4001).filter(value -> !this.x.contains(value)).boxed().collect(Collectors.toSet()));
                }
                case "m" -> {
                    return this.withM(range(1, 4001).filter(value -> !this.m.contains(value)).boxed().collect(Collectors.toSet()));
                }
                case "a" -> {
                    return this.withA(range(1, 4001).filter(value -> !this.a.contains(value)).boxed().collect(Collectors.toSet()));
                }
                case "s" -> {
                    return this.withS(range(1, 4001).filter(value -> !this.s.contains(value)).boxed().collect(Collectors.toSet()));
                }
            }
            return new WorkflowInterval(null, null, null, null);
        }

        WorkflowInterval restrict(WorkflowRule rule) {
            if (rule.variable == null) {
                return this;
            }

            switch (rule.variable()) {
                case "x" -> {
                    if (rule.lessThan()) {
                        return this.withX(this.x.stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
                    } else {
                        return this.withX(this.x.stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
                    }
                }
                case "m" -> {
                    if (rule.lessThan()) {
                        return this.withM(this.m.stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
                    } else {
                        return this.withM(this.m.stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
                    }
                }
                case "a" -> {
                    if (rule.lessThan()) {
                        return this.withA(this.a.stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
                    } else {
                        return this.withA(this.a.stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
                    }
                }
                case "s" -> {
                    if (rule.lessThan()) {
                        return this.withS(this.s.stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
                    } else {
                        return this.withS(this.s.stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
                    }
                }
            }
            return new WorkflowInterval(null, null, null, null);
        }
    }

    record WorkflowRule(String variable, boolean lessThan, int threshold, String goToWorkflow) {
    }

    record MachinePart(int x, int m, int a, int s) {
    }
}