package advent2022;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.max;

// 2287 - too low
// 8398 - too low P2
@Slf4j
public class Advent19 {
    static int[] triangular = {
            0, 1, 3, 6, 10,
            15, 21, 28, 36, 45,
            55, 66, 78, 91, 105,
            120, 136, 153, 171, 190,
            210, 231, 253, 276, 300,
            325, 351, 378, 406, 435,
            465, 496, 528};

    public static void main(String[] args) {
        List<Blueprint> blueprints = Util.fileStream("advent2022/advent19")
                .map(Blueprint::new)
                .collect(Collectors.toList());

        Turn startingTurn = new Turn(0, 0, 0, 0, 0, 1, 0, 0, 0);
        solve(blueprints, startingTurn, 24);
        solve(blueprints.subList(0, 3), startingTurn, 32);
    }

    private static void solve(List<Blueprint> blueprints, Turn startingTurn, int maxTurns) {
        blueprints.stream().parallel().forEach(blueprint -> {
            DFS(blueprint, startingTurn, maxTurns);
            log.info("Blueprint {} - Max Geodes: {}", blueprint.id, blueprint.maxGeodes);
        });
        int totalQuality = blueprints.stream().mapToInt(blueprint -> blueprint.id * blueprint.maxGeodes).sum();
        log.info("Total Quality: {}", totalQuality);
        int geodeProduct = blueprints.stream().mapToInt(blueprint -> blueprint.maxGeodes).reduce((left, right) -> left * right).getAsInt();
        log.info("Geode Product: {}", geodeProduct);
    }

    static void DFS(Blueprint blueprint, Turn turn, int maxTurns) {
        if (turn.minute == maxTurns) {
            if (turn.geodes > blueprint.maxGeodes) {
                blueprint.maxGeodes = turn.geodes;
            }
            return;
        }

        // we use triangular numbers to prune branches that cannot generate enough geodes to beat current max
        if (turn.geodes + turn.geodeGenRobots * (maxTurns - turn.minute) + triangular[maxTurns - turn.minute] < blueprint.maxGeodes) {
            return;
        }

        if (turn.canBuildGeodeRobot(blueprint)) {
            DFS(blueprint, turn.buildGeodeRobot(blueprint), maxTurns);
        } else {
            if (turn.canBuildObsiRobot(blueprint)) {
                DFS(blueprint, turn.buildObsiRobot(blueprint), maxTurns);
            }
            if (turn.canBuildClayRobot(blueprint)) {
                DFS(blueprint, turn.buildClayRobot(blueprint), maxTurns);
            }
            if (turn.canBuildOreRobot(blueprint)) {
                DFS(blueprint, turn.buildOreRobot(blueprint), maxTurns);
            }
            DFS(blueprint, turn.mine(), maxTurns);
        }
    }

    @AllArgsConstructor
    static class Turn {
        int minute;
        int ore;
        int clay;
        int obsi;
        int geodes;
        int oreGenRobots;
        int clayGenRobots;
        int obsiGenRobots;
        int geodeGenRobots;

        Turn mine() {
            return new Turn(minute + 1,
                    ore + oreGenRobots,
                    clay + clayGenRobots,
                    obsi + obsiGenRobots,
                    geodes + geodeGenRobots,
                    oreGenRobots,
                    clayGenRobots,
                    obsiGenRobots,
                    geodeGenRobots);
        }

        boolean canBuildOreRobot(Blueprint blueprint) {
            return this.ore >= blueprint.oreRobot.oreCost
                    && oreGenRobots <= blueprint.maxOre;
        }

        boolean canBuildClayRobot(Blueprint blueprint) {
            return this.ore >= blueprint.clayRobot.oreCost
                    && clayGenRobots <= blueprint.maxClay;
        }

        boolean canBuildObsiRobot(Blueprint blueprint) {
            return this.ore >= blueprint.obsiRobot.oreCost
                    && this.clay >= blueprint.obsiRobot.clayCost
                    && obsiGenRobots <= blueprint.maxObsi;
        }

        boolean canBuildGeodeRobot(Blueprint blueprint) {
            return this.ore >= blueprint.geodeRobot.oreCost
                    && this.obsi >= blueprint.geodeRobot.obsiCost;
        }

        Turn buildOreRobot(Blueprint blueprint) {
            return new Turn(minute + 1,
                    ore + oreGenRobots - blueprint.oreRobot.oreCost,
                    clay + clayGenRobots,
                    obsi + obsiGenRobots,
                    geodes + geodeGenRobots,
                    oreGenRobots + 1,
                    clayGenRobots,
                    obsiGenRobots,
                    geodeGenRobots);
        }

        Turn buildClayRobot(Blueprint blueprint) {
            return new Turn(minute + 1,
                    ore + oreGenRobots - blueprint.clayRobot.oreCost,
                    clay + clayGenRobots,
                    obsi + obsiGenRobots,
                    geodes + geodeGenRobots,
                    oreGenRobots,
                    clayGenRobots + 1,
                    obsiGenRobots,
                    geodeGenRobots);
        }

        Turn buildObsiRobot(Blueprint blueprint) {
            return new Turn(minute + 1,
                    ore + oreGenRobots - blueprint.obsiRobot.oreCost,
                    clay + clayGenRobots - blueprint.obsiRobot.clayCost,
                    obsi + obsiGenRobots,
                    geodes + geodeGenRobots,
                    oreGenRobots,
                    clayGenRobots,
                    obsiGenRobots + 1,
                    geodeGenRobots);
        }

        Turn buildGeodeRobot(Blueprint blueprint) {
            return new Turn(minute + 1,
                    ore + oreGenRobots - blueprint.geodeRobot.oreCost,
                    clay + clayGenRobots,
                    obsi + obsiGenRobots - blueprint.geodeRobot.obsiCost,
                    geodes + geodeGenRobots,
                    oreGenRobots,
                    clayGenRobots,
                    obsiGenRobots,
                    geodeGenRobots + 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Turn turn = (Turn) o;
            return minute == turn.minute && ore == turn.ore && clay == turn.clay && obsi == turn.obsi && geodes == turn.geodes && oreGenRobots == turn.oreGenRobots && clayGenRobots == turn.clayGenRobots && obsiGenRobots == turn.obsiGenRobots && geodeGenRobots == turn.geodeGenRobots;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(minute, ore, clay, obsi, geodes, oreGenRobots, clayGenRobots, obsiGenRobots, geodeGenRobots);
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
