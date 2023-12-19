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

        List<List<WorkflowRule>> in = process(workflowMap, workflowMap.get("in"));

        AtomicLong count = new AtomicLong(0L);
        in.forEach(workflowRules -> {
            boolean a = workflowRules.get(workflowRules.size() - 1).goToWorkflow.equals("A");
            if (a) {
                AtomicReference<WorkflowInterval> interval = new AtomicReference<>(new WorkflowInterval(
                        range(1, 4001).boxed().collect(Collectors.toSet()),
                        range(1, 4001).boxed().collect(Collectors.toSet()),
                        range(1, 4001).boxed().collect(Collectors.toSet()),
                        range(1, 4001).boxed().collect(Collectors.toSet())));

                workflowRules.forEach(workflowRule -> interval.set(interval.get().restrict(workflowRule)));

                count.addAndGet((long) interval.get().x.size() * interval.get().m.size() * interval.get().a.size() * interval.get().s.size());
            }
        });

        return count.get();
    }

    List<List<WorkflowRule>> process(Map<String, Workflow> workflowMap, Workflow workflow) {
        List<List<WorkflowRule>> workflowRules = newArrayList();

        if (workflow != null) {
            for (int c1 = 0; c1 < workflow.conditions.size(); c1++) {
                List<List<WorkflowRule>> process = process(workflowMap, workflowMap.get(workflow.conditions.get(c1).goToWorkflow));

                WorkflowRule parent = workflow.conditions.get(c1);
                process.forEach(list -> list.add(0, parent));
                for (int c2 = 0; c2 < c1; c2++) {
                    WorkflowRule previous = workflow.conditions.get(c2);
                    process.forEach(list -> list.add(0, previous.invert()));
                }

                workflowRules.addAll(process);
            }
        } else {
            workflowRules.add(newArrayList());
        }

        return workflowRules;
    }

    record Workflow(String name, List<WorkflowRule> conditions) {
    }

    @With
    record WorkflowInterval(Set<Integer> x, Set<Integer> m, Set<Integer> a, Set<Integer> s) {
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

    @With
    record WorkflowRule(String variable, boolean lessThan, int threshold, String goToWorkflow) {
        WorkflowRule invert() {
            if (lessThan) return this.withLessThan(false).withThreshold(threshold - 1);
            else return this.withLessThan(true).withThreshold(threshold + 1);
        }
    }

    record MachinePart(int x, int m, int a, int s) {
    }
}