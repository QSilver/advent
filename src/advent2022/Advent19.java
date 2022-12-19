package advent2022;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.max;

// 2287 - too low
@Slf4j
public class Advent19 {
    public static void main(String[] args) {
        List<Blueprint> blueprints = Util.fileStream("advent2022/advent19")
                .map(Blueprint::new)
                .collect(Collectors.toList());

        Turn startingTurn = new Turn(0, 0, 0, 0, 0, 1, 0, 0, 0);
        part1(blueprints, startingTurn);
    }

    private static void part1(List<Blueprint> blueprints, Turn startingTurn) {
        blueprints.forEach(blueprint -> {
            DFS(blueprint, startingTurn);
            log.info("Blueprint {} - Max Geodes: {}", blueprint.id, blueprint.maxGeodes);
        });
        int totalQuality = blueprints.stream().mapToInt(blueprint -> blueprint.id * blueprint.maxGeodes).sum();
        log.info("Total Quality: {}", totalQuality);
    }

    static void DFS(Blueprint blueprint, Turn turn) {
        if (turn.minute == 24) {
            if (blueprint.maxGeodes < turn.geode) {
                blueprint.maxGeodes = turn.geode;
            }
            return;
        }

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
        int minute;
        int ore;
        int clay;
        int obsi;
        int geode;
        int oreGenRobots;
        int clayGenRobots;
        int obsiGenRobots;
        int geodeGenRobots;

        Turn mine() {
            return new Turn(minute + 1,
                    ore + oreGenRobots,
                    clay + clayGenRobots,
                    obsi + obsiGenRobots,
                    geode + geodeGenRobots,
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
                    geode + geodeGenRobots,
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
                    geode + geodeGenRobots,
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
                    geode + geodeGenRobots,
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
                    geode + geodeGenRobots,
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
            return minute == turn.minute && ore == turn.ore && clay == turn.clay && obsi == turn.obsi && geode == turn.geode && oreGenRobots == turn.oreGenRobots && clayGenRobots == turn.clayGenRobots && obsiGenRobots == turn.obsiGenRobots && geodeGenRobots == turn.geodeGenRobots;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(minute, ore, clay, obsi, geode, oreGenRobots, clayGenRobots, obsiGenRobots, geodeGenRobots);
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
