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
    record WorkflowInterval(Map<String, Set<Integer>> variables) {
        boolean isValid() {
            return variables().values().stream().noneMatch(Set::isEmpty);
        }

        long intervalSumFirst() {
            return variables().values().stream().mapToInt(value -> value.iterator().next()).sum();
        }

        long intervalCount() {
            return variables().values().stream().mapToLong(Set::size).reduce(1L, (left, right) -> left * right);
        }

        static WorkflowInterval fullInterval() {
            return new WorkflowInterval(Map.of(
                    "x", rangeToSet(1, 4001),
                    "m", rangeToSet(1, 4001),
                    "a", rangeToSet(1, 4001),
                    "s", rangeToSet(1, 4001)));
        }

        WorkflowInterval restrict(WorkflowRule rule) {
            if (rule.variable == null) {
                return this;
            }

            Map<String, Set<Integer>> filtered = newHashMap(variables());
            if (rule.lessThan) {
                filtered.put(rule.variable, variables.get(rule.variable).stream().filter(value -> value < rule.threshold).collect(Collectors.toSet()));
            } else {
                filtered.put(rule.variable, variables.get(rule.variable).stream().filter(value -> value > rule.threshold).collect(Collectors.toSet()));
            }
            return new WorkflowInterval(filtered);
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
            return new WorkflowInterval(Map.of(
                    "x", rangeToSet(this.x, this.x + 1),
                    "m", rangeToSet(this.m, this.m + 1),
                    "a", rangeToSet(this.a, this.a + 1),
                    "s", rangeToSet(this.s, this.s + 1)
            ));
        }
    }
}