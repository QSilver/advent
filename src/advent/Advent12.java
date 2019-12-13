package advent;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class Advent12 {
    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        List<Moon> moons = Util.fileStream("advent12")
                               .map(s -> s.substring(1, s.length() - 1))
                               .map(s -> s.split(","))
                               .collect(Collectors.toList())
                               .stream()
                               .map(Advent12::getMoon)
                               .collect(Collectors.toList());

        int STEP = 1000;
        while (STEP-- > 0) {
            for (int i = 0; i < moons.size() - 1; i++) {
                for (int j = i; j < moons.size(); j++) {
                    if (moons.get(i) != moons.get(j)) {
                        applyGravity(moons.get(i), moons.get(j));
                    }
                }
            }
            moons.forEach(Moon::applyVelocity);
        }

        AtomicInteger energy = new AtomicInteger();
        moons.forEach(moon -> {
            int kinetic = moon.kineticEnergy();
            int potential = moon.potentialEnergy();
            int total = kinetic * potential;
            energy.getAndAdd(total);
            log.info("Kinetic = {}, Potential = {}, Total = {}", kinetic, potential, total);
        });
        log.info("Total = {}", energy.get());
    }

    private static void applyGravity(Moon moon1, Moon moon2) {
        moon1.xVel -= Integer.compare(moon1.xPos, moon2.xPos);
        moon2.xVel += Integer.compare(moon1.xPos, moon2.xPos);

        moon1.yVel -= Integer.compare(moon1.yPos, moon2.yPos);
        moon2.yVel += Integer.compare(moon1.yPos, moon2.yPos);

        moon1.zVel -= Integer.compare(moon1.zPos, moon2.zPos);
        moon2.zVel += Integer.compare(moon1.zPos, moon2.zPos);
    }

    private static Moon getMoon(String[] strings) {
        int x = Integer.parseInt(strings[0].split("=")[1]);
        int y = Integer.parseInt(strings[1].split("=")[1]);
        int z = Integer.parseInt(strings[2].split("=")[1]);
        return new Moon(x, y, z);
    }
}

class Moon {
    int xPos;
    int yPos;
    int zPos;

    int xVel;
    int yVel;
    int zVel;

    public Moon(int xPos, int yPos, int zPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public void applyVelocity() {
        xPos += xVel;
        yPos += yVel;
        zPos += zVel;
    }

    public int potentialEnergy() {
        return Math.abs(xPos) + Math.abs(yPos) + Math.abs(zPos);
    }

    public int kineticEnergy() {
        return Math.abs(xVel) + Math.abs(yVel) + Math.abs(zVel);
    }

    @Override
    public String toString() {
        return "pos=<x=" + xPos + ", y=" + yPos + ", z=" + zPos + ">, vel=<x=" + xVel + ", y=" + yVel + ", z=" + zVel + '>';
    }
}