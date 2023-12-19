package advent2023;

import advent2023.Advent19.WorkflowInterval;
import advent2023.Advent19.WorkflowRule;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Advent19Test {
    private final Advent19 underTest = new Advent19();

    @Test
    void testPart1() {
        Long run = underTest.runP1("advent2023/advent19_ex1.in");
        assertEquals(19114, run);
    }

    @Test
    void runPart1() {
        Long run = underTest.runP1("advent2023/advent19.in");
        System.out.println(run);
    }

    @Test
    void testIntervalRestrict() {
        WorkflowInterval expected;
        WorkflowInterval in = new WorkflowInterval(
                range(1, 4001).boxed().collect(Collectors.toSet()),
                range(1, 4001).boxed().collect(Collectors.toSet()),
                range(1, 4001).boxed().collect(Collectors.toSet()),
                range(1, 4001).boxed().collect(Collectors.toSet()));

        WorkflowInterval in_px = in.restrict(new WorkflowRule("s", true, 1351, "test"));
        WorkflowInterval in_px_qkq = in_px.restrict(new WorkflowRule("a", true, 2006, "test"));
        WorkflowInterval in_px_qkq_A = in_px_qkq.restrict(new WorkflowRule("x", true, 1416, "test"));

        expected = new WorkflowInterval(
                range(1, 1416).boxed().collect(Collectors.toSet()),
                range(1, 4001).boxed().collect(Collectors.toSet()),
                range(1, 2006).boxed().collect(Collectors.toSet()),
                range(1, 1351).boxed().collect(Collectors.toSet()));

        assertEquals(expected.x(), in_px_qkq_A.x());
        assertEquals(expected.m(), in_px_qkq_A.m());
        assertEquals(expected.a(), in_px_qkq_A.a());
        assertEquals(expected.s(), in_px_qkq_A.s());
    }

    @Test
    void testPart2() {
        Long run = underTest.runP2("advent2023/advent19_ex1.in");
        assertEquals(167409079868000L, run);
    }

    @Test
    void runPart2() {
        Long run = underTest.runP2("advent2023/advent19.in");
        System.out.println(run);
    }
}