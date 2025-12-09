package mas;

import mas.agents.LivingBeing;
import mas.agents.MasterAgent;
import mas.agents.MobileAgent;
import mas.agents.Species;
import mas.agents.species.*;
import mas.data.Message;
import mas.utils.RandomUtils;
import mas.world.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulation {
    private final Map map;
    private final int width = 20;
    private final int height = 20;
    private final int AGENTS_PER_SPECIES = 4;
    private final int MAX_STEPS = 100;
    private int step = 0;

    public Simulation() {
        this.map = new Map(width, height);
    }

    public void initialize() {
        // 1. Place Masters
        placeMaster(MasterOrc.getInstance(1, 1));
        placeMaster(MasterGoblin.getInstance(width - 2, 1));
        placeMaster(MasterElf.getInstance(1, height - 2));
        placeMaster(MasterHuman.getInstance(width - 2, height - 2));

        // 2. Place Obstacles
        for (int i = 0; i < 20; i++) {
            int x = RandomUtils.getInt(0, width - 1);
            int y = RandomUtils.getInt(0, height - 1);
            map.addObstacle(x, y);
        }

        // 3. Place Agents
        for (int i = 0; i < AGENTS_PER_SPECIES; i++) {
            createAndPlaceAgent(Species.ORC, i);
            createAndPlaceAgent(Species.GOBLIN, i);
            createAndPlaceAgent(Species.ELF, i);
            createAndPlaceAgent(Species.HUMAN, i);
        }
    }

    private void placeMaster(MasterAgent master) {
        // Force placement
        map.addAgent(master);
        System.out.println("Placed " + master + " at " + master.getX() + "," + master.getY());
    }

    private void createAndPlaceAgent(Species species, int index) {
        MobileAgent agent = null;
        int maxEp = RandomUtils.getInt(50, 100);
        int x, y;

        do {
            x = RandomUtils.getInt(0, width - 1);
            y = RandomUtils.getInt(0, height - 1);
        } while (!map.isFree(x, y) || map.isRestrictedSafeZone(x, y, species));

        switch (species) {
            case ORC:
                agent = new Orc(x, y, maxEp);
                break;
            case GOBLIN:
                agent = new Goblin(x, y, maxEp);
                break;
            case ELF:
                agent = new Elf(x, y, maxEp);
                break;
            case HUMAN:
                agent = new Human(x, y, maxEp);
                break;
        }

        // Give initial unique message
        agent.addMessage(new Message(species.name() + "_Msg_" + index));

        map.addAgent(agent);
    }

    public void run() {
        initialize();

        while (step < MAX_STEPS) {
            System.out.println("\n--- Step " + step + " ---");
            map.display();

            // Random order
            List<LivingBeing> turnOrder = new ArrayList<>(map.getAgents());
            Collections.shuffle(turnOrder);

            for (LivingBeing agent : turnOrder) {
                // If agent is still on map and mobile
                if (map.getAgents().contains(agent) && agent instanceof MobileAgent) {
                    ((MobileAgent) agent).move(map);
                }
            }

            printStats();
            step++;

            try {
                Thread.sleep(500); // Slow down for visualization
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("--- Simulation Ended ---");
        determineWinner();
    }

    private void printStats() {
        System.out.println("Stats:");
        System.out.println("Orc Master Knowledge: " + MasterOrc.getInstance().getKnowledge().size());
        System.out.println("Goblin Master Knowledge: " + MasterGoblin.getInstance().getKnowledge().size());
        System.out.println("Elf Master Knowledge: " + MasterElf.getInstance().getKnowledge().size());
        System.out.println("Human Master Knowledge: " + MasterHuman.getInstance().getKnowledge().size());
    }

    private void determineWinner() {
        // Collect scores
        int orcScore = MasterOrc.getInstance().getKnowledge().size();
        int goblinScore = MasterGoblin.getInstance().getKnowledge().size();
        int elfScore = MasterElf.getInstance().getKnowledge().size();
        int humanScore = MasterHuman.getInstance().getKnowledge().size();

        int max = Math.max(Math.max(orcScore, goblinScore), Math.max(elfScore, humanScore));

        System.out.println("Max Score: " + max);
        if (orcScore == max)
            System.out.println("Orcs Win!");
        if (goblinScore == max)
            System.out.println("Goblins Win!");
        if (elfScore == max)
            System.out.println("Elves Win!");
        if (humanScore == max)
            System.out.println("Humans Win!");
    }
}
