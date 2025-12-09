package mas.world;

import mas.agents.LivingBeing;
import mas.agents.Species;
import mas.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;

public class Map {
    private final int width;
    private final int height;
    private final Object[][] grid; // Can contain LivingBeing or Obstacle
    private final List<LivingBeing> agents;

    // SafeZone dimensions (e.g., 5x5 corners)
    private static final int SAFE_ZONE_SIZE = 5;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Object[width][height];
        this.agents = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addAgent(LivingBeing agent) {
        if (isValid(agent.getX(), agent.getY()) && isFree(agent.getX(), agent.getY())) {
            grid[agent.getX()][agent.getY()] = agent;
            agents.add(agent);
        } else {
            // Try to find a free spot nearby or error
            System.err.println("Could not place agent at " + agent.getX() + "," + agent.getY());
        }
    }

    public void addObstacle(int x, int y) {
        if (isValid(x, y) && isFree(x, y)) {
            grid[x][y] = new Obstacle();
        }
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isFree(int x, int y) {
        return grid[x][y] == null;
    }

    public Object getEntityAt(int x, int y) {
        if (!isValid(x, y))
            return null;
        return grid[x][y];
    }

    public void moveAgent(LivingBeing agent, int newX, int newY) {
        // Assume validation is done before calling this
        grid[agent.getX()][agent.getY()] = null;
        agent.setPosition(newX, newY);
        grid[newX][newY] = agent;
    }

    public void removeAgent(LivingBeing agent) {
        grid[agent.getX()][agent.getY()] = null; // Clear from grid
        // Intentionally NOT retrieving EP here, handled by agent logic
        // But we might want to place an Obstacle where they died if requested
        grid[agent.getX()][agent.getY()] = new Obstacle();
        agents.remove(agent);
    }

    public boolean isInSafeZone(LivingBeing agent) {
        return isInSafeZone(agent.getX(), agent.getY(), agent.getSpecies());
    }

    public boolean isInSafeZone(int x, int y, Species species) {
        switch (species) {
            case ORC: // Top-Left
                return x < SAFE_ZONE_SIZE && y < SAFE_ZONE_SIZE;
            case GOBLIN: // Top-Right
                return x >= width - SAFE_ZONE_SIZE && y < SAFE_ZONE_SIZE;
            case ELF: // Bottom-Left
                return x < SAFE_ZONE_SIZE && y >= height - SAFE_ZONE_SIZE;
            case HUMAN: // Bottom-Right
                return x >= width - SAFE_ZONE_SIZE && y >= height - SAFE_ZONE_SIZE;
            default:
                return false;
        }
    }

    // Check if x,y is a restricted area (SafeZone of OTHERS)
    public boolean isRestrictedSafeZone(int x, int y, Species mySpecies) {
        // Iterate over all species, if it is in their safe zone and NOT mine, return
        // true
        for (Species s : Species.values()) {
            if (s != mySpecies && isInSafeZone(x, y, s)) {
                return true;
            }
        }
        return false;
    }

    public Direction getDirectionToSafeZone(LivingBeing agent) {
        // Target is the center of their safe zone
        int targetX = 0, targetY = 0;
        switch (agent.getSpecies()) {
            case ORC:
                targetX = 0;
                targetY = 0;
                break;
            case GOBLIN:
                targetX = width - 1;
                targetY = 0;
                break;
            case ELF:
                targetX = 0;
                targetY = height - 1;
                break;
            case HUMAN:
                targetX = width - 1;
                targetY = height - 1;
                break;
        }

        int dx = Integer.compare(targetX, agent.getX());
        int dy = Integer.compare(targetY, agent.getY());

        for (Direction d : Direction.values()) {
            if (d.dx == dx && d.dy == dy)
                return d;
        }
        return RandomUtils.getItem(Direction.values()); // Fallback
    }

    public List<LivingBeing> getAgents() {
        return agents;
    }

    public void display() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Object obj = grid[x][y];
                if (obj == null) {
                    System.out.print(".");
                } else if (obj instanceof Obstacle) {
                    System.out.print("#");
                } else if (obj instanceof LivingBeing) {
                    System.out.print(((LivingBeing) obj).getSymbol());
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
