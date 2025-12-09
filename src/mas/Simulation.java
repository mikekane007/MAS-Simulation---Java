package mas;

import mas.agents.*;
import mas.data.Message;
import mas.utils.RandomUtils;
import mas.world.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulation {
    private final Map map;
    private final int width = 14;
    private final int height = 8;
    private final int AGENTS_PER_SPECIES = 4;
    private final int MAX_STEPS = 50;
    private int step = 0;
    private List<Species> participants;

    public Simulation() {
        this(List.of(Species.values()));
    }

    public Simulation(List<Species> participants) {
        this.map = new Map(width, height);
        this.participants = new ArrayList<>(participants);
    }

    public void initialize() {
        // 1. Place Masters & 3. Place Agents
        for (Species s : participants) {
            // Generic Master Placement
            switch (s) {
                case BOWSER:
                    placeMaster(MasterAgent.getInstance(s, 1, 1));
                    break;
                case KING_BOO:
                    placeMaster(MasterAgent.getInstance(s, width - 2, 1));
                    break;
                case LUIGI:
                    placeMaster(MasterAgent.getInstance(s, 1, height - 2));
                    break;
                case MARIO:
                    placeMaster(MasterAgent.getInstance(s, width - 2, height - 2));
                    break;
            }

            for (int i = 0; i < AGENTS_PER_SPECIES; i++) {
                createAndPlaceAgent(s, i);
            }
        }

        // 2. Place Obstacles
        for (int i = 0; i < 15; i++) {
            int x = RandomUtils.getInt(0, width - 1);
            int y = RandomUtils.getInt(0, height - 1);
            map.addObstacle(x, y);
        }
    }

    private void placeMaster(MasterAgent master) {
        // Force placement
        map.addAgent(master);
        System.out.println("Placed " + master + " at " + master.getX() + "," + master.getY());
    }

    private void createAndPlaceAgent(Species species, int index) {
        MobileAgent agent = null;
        int maxEp = 120; // Slightly more buffer
        int x, y;

        do {
            x = RandomUtils.getInt(0, width - 1);
            y = RandomUtils.getInt(0, height - 1);
        } while (!map.isFree(x, y) || map.isRestrictedSafeZone(x, y, species));

        // Generic Agent Creation
        agent = new MobileAgent(x, y, species, maxEp);

        if (agent != null) {
            // Give initial unique message
            agent.addMessage(new Message(species.name() + "_Msg_" + index));
            map.addAgent(agent);
        }
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
        for (Species s : participants) {
            System.out.println(s.name() + " Master Knowledge: " + MasterAgent.getInstance(s).getKnowledge().size());
        }
    }

    private void determineWinner() {
        // Collect scores and energy logic ...
        // Generic refactor needed here too

        System.out.println("Final Scores (Knowledge | Energy):");

        int maxScore = -1;
        // Calculate scores dynamically
        for (Species s : participants) {
            int score = MasterAgent.getInstance(s).getKnowledge().size();
            int energy = 0;

            for (LivingBeing agent : map.getAgents()) {
                if (agent instanceof MobileAgent && ((MobileAgent) agent).getSpecies() == s) {
                    energy += ((MobileAgent) agent).getEnergy();
                }
            }

            System.out.println(s.name() + ": " + score + " | " + energy);
            if (score > maxScore)
                maxScore = score;
        }

        System.out.println("Max Knowledge Score: " + maxScore);

        List<Species> knowledgeWinners = new ArrayList<>();
        for (Species s : participants) {
            if (MasterAgent.getInstance(s).getKnowledge().size() == maxScore) {
                knowledgeWinners.add(s);
            }
        }

        List<Species> finalWinners = new ArrayList<>();

        if (knowledgeWinners.size() > 1) {
            System.out.println("Knowledge Tie detected! Converting to Power (Energy) check...");
            int maxEnergy = -1;

            for (Species s : knowledgeWinners) {
                int energy = 0;
                for (LivingBeing agent : map.getAgents()) {
                    if (agent instanceof MobileAgent && ((MobileAgent) agent).getSpecies() == s) {
                        energy += ((MobileAgent) agent).getEnergy();
                    }
                }
                if (energy > maxEnergy)
                    maxEnergy = energy;
            }

            for (Species s : knowledgeWinners) {
                int energy = 0;
                for (LivingBeing agent : map.getAgents()) {
                    if (agent instanceof MobileAgent && ((MobileAgent) agent).getSpecies() == s) {
                        energy += ((MobileAgent) agent).getEnergy();
                    }
                }
                if (energy == maxEnergy) {
                    finalWinners.add(s);
                }
            }
        } else {
            finalWinners.addAll(knowledgeWinners);
        }

        // Final Result Handling
        if (finalWinners.size() == 1) {
            System.out.println("Winner: " + finalWinners.get(0));
        } else {
            System.out.println("DOUBLE TIE (Knowledge & Energy)! Winners: " + finalWinners);
            System.out.println("Initiating SUDDEN DEATH PLAYOFF among " + finalWinners);

            // --- PLAYOFF LOGIC ---
            // Reset Masters
            for (Species s : participants) {
                MasterAgent.getInstance(s).reset();
            }

            // Start recursive simulation
            Simulation playoff = new Simulation(finalWinners);
            playoff.run();
        }
    }

}
