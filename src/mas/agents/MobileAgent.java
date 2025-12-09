package mas.agents;

import mas.data.Message;
import mas.agents.MasterAgent;
import mas.utils.RandomUtils;
import mas.world.Direction;
import mas.world.Map;
import mas.world.Obstacle;

import java.util.Collections;
import java.util.List;

public class MobileAgent extends LivingBeing {
    protected int energyPoints;
    protected int maxEnergy;
    protected Direction lastDirection;

    public MobileAgent(int x, int y, Species species, int maxEnergy) {
        super(x, y, species);
        this.maxEnergy = maxEnergy;
        this.energyPoints = maxEnergy;
        this.lastDirection = RandomUtils.getItem(Direction.values());
    }

    public int getEnergy() {
        return energyPoints;
    }

    public void move(Map map) {
        // 1. Check Energy / State
        if (energyPoints <= 0) {
            // Die and become obstacle
            System.out.println(this + " ran out of energy and turned into an obstacle.");
            map.removeAgent(this);
            return;
        }

        // Recover energy if in SafeZone
        if (map.isInSafeZone(this)) {
            energyPoints = Math.min(maxEnergy, energyPoints + 5); // Recover 5 EP
        } else {
            // 2. Determine Move
            int targetX = x;
            int targetY = y;
            Direction moveDir = null;

            double epRatio = (double) energyPoints / maxEnergy;

            if (epRatio >= 0.20) {
                // Random move
                moveDir = RandomUtils.getItem(Direction.values());
            } else {
                // Go to SafeZone
                moveDir = map.getDirectionToSafeZone(this);
            }

            targetX += moveDir.dx;
            targetY += moveDir.dy;

            // 3. Check Validity and Collisions
            if (!map.isValid(targetX, targetY) || map.isRestrictedSafeZone(targetX, targetY, this.species)) {
                // Hit wall or restricted zone: lose EP, stay, safe last dir
                consumeEnergy(map);
                lastDirection = moveDir;
                return;
            }

            Object target = map.getEntityAt(targetX, targetY);

            if (target == null) {
                // Free space
                map.moveAgent(this, targetX, targetY);
                consumeEnergy(map);
                lastDirection = moveDir;
            } else if (target instanceof Obstacle) {
                // Blocked by obstacle
                consumeEnergy(map); // Lose remaining energy for step? "loses as many EP as he has left to go" -
                                    // assuming standard cost for now or full move cost
                lastDirection = moveDir;
            } else if (target instanceof LivingBeing) {
                // Interaction
                interact((LivingBeing) target);
                // "movement can be stopped by ... an individual standing in the way"
                // So we don't move into the tile, but we interact.
                consumeEnergy(map);
            }
        }

        scanForMaster(map);
    }

    private void scanForMaster(Map map) {
        for (Direction d : Direction.values()) {
            int tx = x + d.dx;
            int ty = y + d.dy;
            if (map.isValid(tx, ty)) {
                Object obj = map.getEntityAt(tx, ty);
                if (obj instanceof MasterAgent) {
                    MasterAgent master = (MasterAgent) obj;
                    if (master.getSpecies() == this.species) {
                        // Transfer all distinct messages to master
                        // Note: We keep copies? Or just give copies?
                        // "Master collects them... obtain from other groups"
                        // Usually we just copy.
                        shareKnowledge(master);
                        // And Master shares back? Not necessarily.
                        // But Master is "Wisest", so maybe gives info?
                        // Requirement doesn't say.
                        // Interaction with same population -> Union.
                        master.getKnowledge().forEach(this::addMessage);
                    }
                }
            }
        }
    }

    private void consumeEnergy(Map map) {
        if (!map.isInSafeZone(this)) {
            energyPoints -= 2; // Increased cost for faster burn
        }
    }

    protected void interact(LivingBeing other) {
        System.out.println(this + " interacts with " + other);

        if (other.getSpecies() == this.species) {
            // Same population: Union of knowledge
            shareKnowledge(other);
            other.getKnowledge().forEach(this::addMessage);
        } else if (other.getSpecies().getAlliance() == this.species.getAlliance()) {
            // Same alliance: Exchange random messages
            shareRandomMessages(other);
        } else {
            // Different alliance: Fight
            fight(other);
        }
    }

    private void shareKnowledge(LivingBeing other) {
        for (Message m : this.knowledge) {
            other.addMessage(m);
        }
    }

    private void shareRandomMessages(LivingBeing other) {
        // Exchange 3 messages for better knowledge spread
        for (int i = 0; i < 3; i++) {
            if (!this.knowledge.isEmpty()) {
                other.addMessage(RandomUtils.getItem(this.knowledge.toArray(new Message[0])));
            }
            if (!other.getKnowledge().isEmpty()) {
                this.addMessage(RandomUtils.getItem(other.getKnowledge().toArray(new Message[0])));
            }
        }
    }

    private void fight(LivingBeing other) {
        // Simple 50/50 fight or based on something else?
        // Winner gets random messages from loser
        boolean iWin = RandomUtils.chance(0.5);
        if (iWin) {
            stealMessage(other, this);
        } else {
            stealMessage(this, other);
        }
    }

    private void stealMessage(LivingBeing loser, LivingBeing winner) {
        if (loser.getKnowledge().isEmpty())
            return;

        // Steal 50% of messages (min 1)
        int stealCount = Math.max(1, loser.getKnowledge().size() / 2);

        for (int i = 0; i < stealCount; i++) {
            if (loser.getKnowledge().isEmpty())
                break;

            Message stolen = RandomUtils.getItem(loser.getKnowledge().toArray(new Message[0]));
            if (stolen != null) {
                winner.addMessage(stolen);
                loser.getKnowledge().remove(stolen);
                System.out.println(winner + " stole " + stolen + " from " + loser);
            }
        }
    }

    @Override
    public String toString() {
        return species.name() + "-" + Integer.toHexString(hashCode()).substring(0, 4);
    }
}
