package advent2023;

import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static util.Util.rangeToSet;
import static util.Util.readDoubleNewlineBlocks;

@Slf4j
public class Advent19 {
    // https://adventofcode.com/2023/day/19

    public Long runP1(String file) {
        String[] split = readDoubleNewlineBlocks(file);

        Map<String, Workflow> workflowMap = getWorkflowMap(split[0]);
        List<MachinePart> machineParts = getMachineParts(split[1]);

        List<List<WorkflowRule>> in = getTreePaths(workflowMap, workflowMap.get("in"));

        return in.stream().mapToLong(treePath -> machineParts.stream()
                .mapToLong(machinePart -> {
                    WorkflowInterval interval = machinePart.getInterval();
                    for (WorkflowRule workflowRule : treePath) {
                        interval = interval.restrict(workflowRule);
                    }

                    boolean accepted = interval.isValid() && treePath.getLast().goToWorkflow.equals("A");
                    return accepted ? interval.intervalSumFirst() : 0L;
                }).sum()).sum();
    }

    public Long runP2(String file) {
        String[] split = readDoubleNewlineBlocks(file);

        Map<String, Workflow> workflowMap = getWorkflowMap(split[0]);

        List<List<WorkflowRule>> in = getTreePaths(workflowMap, workflowMap.get("in"));

        AtomicLong count = new AtomicLong(0L);
        in.forEach(treePath -> {
            if (treePath.getLast().goToWorkflow.equals("A")) {
                WorkflowInterval interval = WorkflowInterval.fullInterval();

                for (WorkflowRule workflowRule : treePath) {
                    interval = interval.restrict(workflowRule);
                }

                count.addAndGet(interval.intervalCount());
            }
        });

        return count.get();
    }

    List<List<WorkflowRule>> getTreePaths(Map<String, Workflow> workflowMap, Workflow workflow) {
        List<List<WorkflowRule>> workflowRules = newArrayList();

        if (workflow != null) {
            for (int current = 0; current < workflow.conditions.size(); current++) {
                WorkflowRule currentRule = workflow.conditions.get(current);

                List<List<WorkflowRule>> process = getTreePaths(workflowMap, workflowMap.get(currentRule.goToWorkflow));
                process.forEach(list -> list.addFirst(currentRule));

                for (int previous = 0; previous < current; previous++) {
                    WorkflowRule toInvert = workflow.conditions.get(previous);
                    process.forEach(list -> list.addFirst(toInvert.invert()));
                }
                workflowRules.addAll(process);
            }
        } else {
            workflowRules.add(newArrayList());
        }

        return workflowRules;
    }

    private static Map<String, Workflow> getWorkflowMap(String workflowLines) {
        Map<String, Workflow> workflowMap = newHashMap();
        Arrays.stream(workflowLines.split("\n")).forEach(workflow -> {
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
        return workflowMap;
    }

    private static List<MachinePart> getMachineParts(String machinePartList) {
        return Arrays.stream(machinePartList.split("\\n")).map(part -> {
            String[] variables = part.replace("{", "")
                    .replace("}", "")
                    .split(",");
            int x = parseInt(variables[0].substring(2));
            int m = parseInt(variables[1].substring(2));
            int a = parseInt(variables[2].substring(2));
            int s = parseInt(variables[3].substring(2));
            return new MachinePart(x, m, a, s);
        }).toList();
    }

    record Workflow(String name, List<WorkflowRule> conditions) {
    }

    @With
    record WorkflowInterval(Set<Integer> x, Set<Integer> m, Set<Integer> a, Set<Integer> s) {
        boolean isValid() {
            return !x.isEmpty() && !m.isEmpty() && !a.isEmpty() && !s.isEmpty();
        }

        long intervalSumFirst() {
            return x.iterator().next() + m.iterator().next() + a.iterator().next() + s.iterator().next();
        }

        long intervalCount() {
            return (long) x.size() * m.size() * a.size() * s.size();
        }

        static WorkflowInterval fullInterval() {
            return new WorkflowInterval(rangeToSet(1, 4001), rangeToSet(1, 4001), rangeToSet(1, 4001), rangeToSet(1, 4001));
        }

        WorkflowInterval restrict(WorkflowRule rule) {
            if (rule.variable == null) {
                return this;
            }

            switch (rule.variable()) {
                case "x" -> {
                    if (rule.lessThan()) {
                        return this.withX(x.stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
                    } else {
                        return this.withX(x.stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
                    }
                }
                case "m" -> {
                    if (rule.lessThan()) {
                        return this.withM(m.stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
                    } else {
                        return this.withM(m.stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
                    }
                }
                case "a" -> {
                    if (rule.lessThan()) {
                        return this.withA(a.stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
                    } else {
                        return this.withA(a.stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
                    }
                }
                case "s" -> {
                    if (rule.lessThan()) {
                        return this.withS(s.stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
                    } else {
                        return this.withS(s.stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
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
        WorkflowInterval getInterval() {
            return new WorkflowInterval(
                    rangeToSet(this.x, this.x + 1),
                    rangeToSet(this.m, this.m + 1),
                    rangeToSet(this.a, this.a + 1),
                    rangeToSet(this.s, this.s + 1));
        }
    }
}