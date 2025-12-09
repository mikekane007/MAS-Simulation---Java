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
    private final int MAX_STEPS = 500;
    private int step = 0;

    public Simulation() {
        this.map = new Map(width, height);
    }

    public void initialize() {
        // 1. Place Masters
        placeMaster(MasterBowser.getInstance(1, 1));
        placeMaster(MasterKingBoo.getInstance(width - 2, 1));
        placeMaster(MasterLuigi.getInstance(1, height - 2));
        placeMaster(MasterMario.getInstance(width - 2, height - 2));

        // 2. Place Obstacles
        for (int i = 0; i < 20; i++) {
            int x = RandomUtils.getInt(0, width - 1);
            int y = RandomUtils.getInt(0, height - 1);
            map.addObstacle(x, y);
        }

        // 3. Place Agents
        for (int i = 0; i < AGENTS_PER_SPECIES; i++) {
            createAndPlaceAgent(Species.BOWSER, i);
            createAndPlaceAgent(Species.KING_BOO, i);
            createAndPlaceAgent(Species.LUIGI, i);
            createAndPlaceAgent(Species.MARIO, i);
        }
    }

    private void placeMaster(MasterAgent master) {
        // Force placement
        map.addAgent(master);
        System.out.println("Placed " + master + " at " + master.getX() + "," + master.getY());
    }

    private void createAndPlaceAgent(Species species, int index) {
        MobileAgent agent = null;
        int maxEp = RandomUtils.getInt(200, 300);
        int x, y;

        do {
            x = RandomUtils.getInt(0, width - 1);
            y = RandomUtils.getInt(0, height - 1);
        } while (!map.isFree(x, y) || map.isRestrictedSafeZone(x, y, species));

        switch (species) {
            case BOWSER:
                agent = new Bowser(x, y, maxEp);
                break;
            case KING_BOO:
                agent = new KingBoo(x, y, maxEp);
                break;
            case LUIGI:
                agent = new Luigi(x, y, maxEp);
                break;
            case MARIO:
                agent = new Mario(x, y, maxEp);
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
                Thread.sleep(100); // Slow down for visualization
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("--- Simulation Ended ---");
        determineWinner();
    }

    private void printStats() {
        System.out.println("Stats:");
        System.out.println("Bowser Master Knowledge: " + MasterBowser.getInstance().getKnowledge().size());
        System.out.println("KingBoo Master Knowledge: " + MasterKingBoo.getInstance().getKnowledge().size());
        System.out.println("Luigi Master Knowledge: " + MasterLuigi.getInstance().getKnowledge().size());
        System.out.println("Mario Master Knowledge: " + MasterMario.getInstance().getKnowledge().size());
    }

    private void determineWinner() {
        // Collect scores
        int bowserScore = MasterBowser.getInstance().getKnowledge().size();
        int kingBooScore = MasterKingBoo.getInstance().getKnowledge().size();
        int luigiScore = MasterLuigi.getInstance().getKnowledge().size();
        int marioScore = MasterMario.getInstance().getKnowledge().size();

        int max = Math.max(Math.max(bowserScore, kingBooScore), Math.max(luigiScore, marioScore));

        System.out.println("Max Score: " + max);
        if (bowserScore == max)
            System.out.println("Bowser Wins!");
        if (kingBooScore == max)
            System.out.println("King Boo Wins!");
        if (luigiScore == max)
            System.out.println("Luigi Wins!");
        if (marioScore == max)
            System.out.println("Mario Wins!");
    }
}
