package polymorphicSimulation;

import polymorphicSimulation.agents.*;
import polymorphicSimulation.agents.Message;
import polymorphicSimulation.utils.MonteCarloRNG;
import polymorphicSimulation.utils.SingletonMasterFactory;
import polymorphicSimulation.environment.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulation {
    private final Map map;
    private final int width = 14;
    private final int height = 8;
    private final int AGENTS_PER_SPECIES = 4;
    private final int MAX_STEPS = 20;
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
                    placeMaster(SingletonMasterFactory.getMaster(s, 1, 1));
                    break;
                case KING_BOO:
                    placeMaster(SingletonMasterFactory.getMaster(s, width - 2, 1));
                    break;
                case LUIGI:
                    placeMaster(SingletonMasterFactory.getMaster(s, 1, height - 2));
                    break;
                case MARIO:
                    placeMaster(SingletonMasterFactory.getMaster(s, width - 2, height - 2));
                    break;
            }

            for (int i = 0; i < AGENTS_PER_SPECIES; i++) {
                createAndPlaceAgent(s, i);
            }
        }

        // 2. Place Obstacles
        for (int i = 0; i < 15; i++) {
            int x = MonteCarloRNG.getInt(0, width - 1);
            int y = MonteCarloRNG.getInt(0, height - 1);
            map.addObstacle(x, y);
        }
    }

    private void placeMaster(Master master) {
        // Force placement
        map.addAgent(master);
        System.out.println("Placed " + master + " at " + master.getX() + "," + master.getY());
    }

    private void createAndPlaceAgent(Species species, int index) {
        Agent agent = null;
        int maxEp = 120; // Slightly more buffer
        int x, y;

        do {
            x = MonteCarloRNG.getInt(0, width - 1);
            y = MonteCarloRNG.getInt(0, height - 1);
        } while (!map.isFree(x, y) || map.isRestrictedSafeZone(x, y, species));

        // Generic Agent Creation with Factory
        agent = SingletonMasterFactory.createAgent(species, x, y, maxEp);

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
                if (map.getAgents().contains(agent) && agent instanceof Agent) {
                    ((Agent) agent).move(map);
                }
            }

            printStats();
            step++;

            try {
                Thread.sleep(1000); // Slow down for visualization
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("--- Simulation Ended ---");
        printAllMasterKnowledge();
        determineWinner();
    }

    private void printAllMasterKnowledge() {
        System.out.println("\n=== Final Master Knowledge ===");
        for (Species s : participants) {
            Master master = SingletonMasterFactory.getMaster(s);
            System.out.println(
                    s.getColorCode() + s.name() + " Master (" + master.getKnowledge().size() + " msgs):\u001B[0m");
            for (Message m : master.getKnowledge()) {
                System.out.println("  - " + m.getContent());
            }
        }
        System.out.println("==============================\n");
    }

    private void printStats() {
        System.out.println("Stats:");
        for (Species s : participants) {
            System.out.println(
                    s.name() + " Master Knowledge: " + SingletonMasterFactory.getMaster(s).getKnowledge().size());
        }
    }

    private void determineWinner() {
        System.out.println("Final Scores (Knowledge | Energy):");

        int maxScore = -1;
        // Calculate scores dynamically
        for (Species s : participants) {
            int score = SingletonMasterFactory.getMaster(s).getKnowledge().size();
            int energy = 0;

            for (LivingBeing agent : map.getAgents()) {
                if (agent instanceof Agent && ((Agent) agent).getSpecies() == s) {
                    energy += ((Agent) agent).getEnergy();
                }
            }

            System.out.println(s.name() + ": " + score + " | " + energy);
            if (score > maxScore)
                maxScore = score;
        }

        System.out.println("Max Knowledge Score: " + maxScore);

        List<Species> knowledgeWinners = new ArrayList<>();
        for (Species s : participants) {
            if (SingletonMasterFactory.getMaster(s).getKnowledge().size() == maxScore) {
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
                    if (agent instanceof Agent && ((Agent) agent).getSpecies() == s) {
                        energy += ((Agent) agent).getEnergy();
                    }
                }
                if (energy > maxEnergy)
                    maxEnergy = energy;
            }

            for (Species s : knowledgeWinners) {
                int energy = 0;
                for (LivingBeing agent : map.getAgents()) {
                    if (agent instanceof Agent && ((Agent) agent).getSpecies() == s) {
                        energy += ((Agent) agent).getEnergy();
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
            Species winner = finalWinners.get(0);
            System.out.println(winner.getColorCode() + "Winner: " + winner + "\u001B[0m");
        } else {
            System.out.println("DOUBLE TIE (Knowledge & Energy)! Winners: " + finalWinners);
            System.out.println("Initiating SUDDEN DEATH PLAYOFF among " + finalWinners);

            // --- PLAYOFF LOGIC ---
            // Reset Masters
            for (Species s : participants) {
                SingletonMasterFactory.getMaster(s).reset();
            }

            // Start recursive simulation
            Simulation playoff = new Simulation(finalWinners);
            playoff.run();
        }
    }
}
