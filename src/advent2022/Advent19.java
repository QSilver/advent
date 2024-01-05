package advent2022;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.max;

// 2287 - too low
// 8398 - too low P2
@Slf4j
public class Advent19 {
    static int[] triangular = {
            0, 1, 3, 6, 10, 15,
            21, 28, 36, 45, 55, 66,
            78, 91, 105, 120, 136, 153,
            171, 190, 210, 231, 253, 276,
            300, 325, 351, 378, 406, 435,
            465, 496, 528};

    public static void main(String[] args) {
        List<Blueprint> blueprints = InputUtils.fileStream("advent2022/advent19")
                .map(Blueprint::new)
                .collect(Collectors.toList());

        solve(blueprints, new Turn(24, 0, 0, 0, 0, 1, 0, 0, 0));
        solve(blueprints.subList(0, 3), new Turn(32, 0, 0, 0, 0, 1, 0, 0, 0));
    }

    private static void solve(List<Blueprint> blueprints, Turn startingTurn) {
        blueprints.stream().parallel().forEach(blueprint -> {
            DFS(blueprint, startingTurn);
            log.info("Blueprint {} - Max Geodes: {}", blueprint.id, blueprint.maxGeodes);
        });
        int totalQuality = blueprints.stream().mapToInt(blueprint -> blueprint.id * blueprint.maxGeodes).sum();
        log.info("Total Quality: {}", totalQuality);
        int geodeProduct = blueprints.stream().mapToInt(blueprint -> blueprint.maxGeodes).reduce((left, right) -> left * right).getAsInt();
        log.info("Geode Product: {}", geodeProduct);
    }

    static void DFS(Blueprint blueprint, Turn turn) {
        if (turn.minutesRemaining == 0) {
            if (turn.geodes > blueprint.maxGeodes) {
                blueprint.maxGeodes = turn.geodes;
            }
            return;
        }

        // we use triangular numbers to prune branches that cannot generate enough geodes to beat current max
        // count how many geodes we have + how many we'd make + hoe many we can possibly make if we build 1 Geode Robot / turn
        if (turn.geodes + turn.geodeRobots * turn.minutesRemaining + triangular[turn.minutesRemaining] < blueprint.maxGeodes) {
            return;
        }

        // prioritize Geode-making Robots
        if (turn.canBuildGeodeRobot(blueprint)) {
            DFS(blueprint, turn.buildGeodeRobot(blueprint));
        } else {
            if (turn.canBuildObsiRobot(blueprint)) {
                DFS(blueprint, turn.buildObsiRobot(blueprint));
            }
            if (turn.canBuildClayRobot(blueprint)) {
                DFS(blueprint, turn.buildClayRobot(blueprint));
            }
            if (turn.canBuildOreRobot(blueprint)) {
                DFS(blueprint, turn.buildOreRobot(blueprint));
            }
            DFS(blueprint, turn.mine());
        }
    }

    @AllArgsConstructor
    static class Turn {
        int minutesRemaining;
        int ore;
        int clay;
        int obsi;
        int geodes;
        int oreRobots;
        int clayRobots;
        int obsiRobots;
        int geodeRobots;

        // since we can only build 1 robot/turn, don't build more robots than the max resource cost
        boolean canBuildOreRobot(Blueprint blueprint) {
            return this.ore >= blueprint.oreRobot.oreCost
                    && oreRobots <= blueprint.maxOre;
        }

        boolean canBuildClayRobot(Blueprint blueprint) {
            return this.ore >= blueprint.clayRobot.oreCost
                    && clayRobots <= blueprint.maxClay;
        }

        boolean canBuildObsiRobot(Blueprint blueprint) {
            return this.ore >= blueprint.obsiRobot.oreCost
                    && this.clay >= blueprint.obsiRobot.clayCost
                    && obsiRobots <= blueprint.maxObsi;
        }

        boolean canBuildGeodeRobot(Blueprint blueprint) {
            return this.ore >= blueprint.geodeRobot.oreCost
                    && this.obsi >= blueprint.geodeRobot.obsiCost;
        }

        Turn mine() {
            return new Turn(minutesRemaining - 1,
                    ore + oreRobots,
                    clay + clayRobots,
                    obsi + obsiRobots,
                    geodes + geodeRobots,
                    oreRobots,
                    clayRobots,
                    obsiRobots,
                    geodeRobots);
        }

        Turn buildOreRobot(Blueprint blueprint) {
            return new Turn(minutesRemaining - 1,
                    ore + oreRobots - blueprint.oreRobot.oreCost,
                    clay + clayRobots,
                    obsi + obsiRobots,
                    geodes + geodeRobots,
                    oreRobots + 1,
                    clayRobots,
                    obsiRobots,
                    geodeRobots);
        }

        Turn buildClayRobot(Blueprint blueprint) {
            return new Turn(minutesRemaining - 1,
                    ore + oreRobots - blueprint.clayRobot.oreCost,
                    clay + clayRobots,
                    obsi + obsiRobots,
                    geodes + geodeRobots,
                    oreRobots,
                    clayRobots + 1,
                    obsiRobots,
                    geodeRobots);
        }

        Turn buildObsiRobot(Blueprint blueprint) {
            return new Turn(minutesRemaining - 1,
                    ore + oreRobots - blueprint.obsiRobot.oreCost,
                    clay + clayRobots - blueprint.obsiRobot.clayCost,
                    obsi + obsiRobots,
                    geodes + geodeRobots,
                    oreRobots,
                    clayRobots,
                    obsiRobots + 1,
                    geodeRobots);
        }

        Turn buildGeodeRobot(Blueprint blueprint) {
            return new Turn(minutesRemaining - 1,
                    ore + oreRobots - blueprint.geodeRobot.oreCost,
                    clay + clayRobots,
                    obsi + obsiRobots - blueprint.geodeRobot.obsiCost,
                    geodes + geodeRobots,
                    oreRobots,
                    clayRobots,
                    obsiRobots,
                    geodeRobots + 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Turn turn = (Turn) o;
            return minutesRemaining == turn.minutesRemaining && ore == turn.ore && clay == turn.clay && obsi == turn.obsi && geodes == turn.geodes && oreRobots == turn.oreRobots && clayRobots == turn.clayRobots && obsiRobots == turn.obsiRobots && geodeRobots == turn.geodeRobots;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(minutesRemaining, ore, clay, obsi, geodes, oreRobots, clayRobots, obsiRobots, geodeRobots);
        }
    }

    static class Blueprint {
        int id;
        Robot oreRobot;
        Robot clayRobot;
        Robot obsiRobot;
        Robot geodeRobot;

        int maxOre;
        int maxClay;
        int maxObsi;
        int maxGeodes;

        public Blueprint(String blueprint) {
            String[] split = blueprint.split(" ");
            this.id = Integer.parseInt(split[1].split(":")[0]);
            this.oreRobot = new Robot(Integer.parseInt(split[6]), 0, 0, 1, 0, 0, 0);
            this.clayRobot = new Robot(Integer.parseInt(split[12]), 0, 0, 0, 1, 0, 0);
            this.obsiRobot = new Robot(Integer.parseInt(split[18]), Integer.parseInt(split[21]), 0, 0, 0, 1, 0);
            this.geodeRobot = new Robot(Integer.parseInt(split[27]), 0, Integer.parseInt(split[30]), 0, 0, 0, 1);

            maxOre = max(max(oreRobot.oreCost, clayRobot.oreCost), max(obsiRobot.oreCost, geodeRobot.oreCost));
            maxClay = max(max(oreRobot.clayCost, clayRobot.clayCost), max(obsiRobot.clayCost, geodeRobot.clayCost));
            maxObsi = max(max(oreRobot.obsiCost, clayRobot.obsiCost), max(obsiRobot.obsiCost, geodeRobot.obsiCost));
        }
    }

    @AllArgsConstructor
    static class Robot {
        int oreCost;
        int clayCost;
        int obsiCost;
        int oreGen;
        int clayGen;
        int obsiGen;
        int geodeGen;
    }
}
