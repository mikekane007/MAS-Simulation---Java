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
    private final int width = 14;
    private final int height = 8;
    private final int AGENTS_PER_SPECIES = 4;
    private final int MAX_STEPS = 80;
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
            switch (s) {
                case BOWSER:
                    placeMaster(MasterBowser.getInstance(1, 1));
                    break;
                case KING_BOO:
                    placeMaster(MasterKingBoo.getInstance(width - 2, 1));
                    break;
                case LUIGI:
                    placeMaster(MasterLuigi.getInstance(1, height - 2));
                    break;
                case MARIO:
                    placeMaster(MasterMario.getInstance(width - 2, height - 2));
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
        if (participants.contains(Species.BOWSER))
            System.out.println("Bowser Master Knowledge: " + MasterBowser.getInstance().getKnowledge().size());
        if (participants.contains(Species.KING_BOO))
            System.out.println("KingBoo Master Knowledge: " + MasterKingBoo.getInstance().getKnowledge().size());
        if (participants.contains(Species.LUIGI))
            System.out.println("Luigi Master Knowledge: " + MasterLuigi.getInstance().getKnowledge().size());
        if (participants.contains(Species.MARIO))
            System.out.println("Mario Master Knowledge: " + MasterMario.getInstance().getKnowledge().size());
    }

    private void determineWinner() {
        // Collect scores and energy
        int bowserScore = participants.contains(Species.BOWSER) ? MasterBowser.getInstance().getKnowledge().size() : -1;
        int kingBooScore = participants.contains(Species.KING_BOO) ? MasterKingBoo.getInstance().getKnowledge().size()
                : -1;
        int luigiScore = participants.contains(Species.LUIGI) ? MasterLuigi.getInstance().getKnowledge().size() : -1;
        int marioScore = participants.contains(Species.MARIO) ? MasterMario.getInstance().getKnowledge().size() : -1;

        int bowserEnergy = 0, kingBooEnergy = 0, luigiEnergy = 0, marioEnergy = 0;

        for (LivingBeing agent : map.getAgents()) {
            if (agent instanceof MobileAgent) {
                MobileAgent mobile = (MobileAgent) agent;
                switch (mobile.getSpecies()) {
                    case BOWSER:
                        bowserEnergy += mobile.getEnergy();
                        break;
                    case KING_BOO:
                        kingBooEnergy += mobile.getEnergy();
                        break;
                    case LUIGI:
                        luigiEnergy += mobile.getEnergy();
                        break;
                    case MARIO:
                        marioEnergy += mobile.getEnergy();
                        break;
                }
            }
        }

        System.out.println("Final Scores (Knowledge | Energy):");
        if (participants.contains(Species.BOWSER))
            System.out.println("Bowser: " + bowserScore + " | " + bowserEnergy);
        if (participants.contains(Species.KING_BOO))
            System.out.println("KingBoo: " + kingBooScore + " | " + kingBooEnergy);
        if (participants.contains(Species.LUIGI))
            System.out.println("Luigi: " + luigiScore + " | " + luigiEnergy);
        if (participants.contains(Species.MARIO))
            System.out.println("Mario: " + marioScore + " | " + marioEnergy);

        int maxScore = Math.max(Math.max(bowserScore, kingBooScore), Math.max(luigiScore, marioScore));
        System.out.println("Max Knowledge Score: " + maxScore);

        // Identify potential winners by Knowledge
        List<Species> knowledgeWinners = new ArrayList<>();
        if (bowserScore == maxScore)
            knowledgeWinners.add(Species.BOWSER);
        if (kingBooScore == maxScore)
            knowledgeWinners.add(Species.KING_BOO);
        if (luigiScore == maxScore)
            knowledgeWinners.add(Species.LUIGI);
        if (marioScore == maxScore)
            knowledgeWinners.add(Species.MARIO);

        List<Species> finalWinners = new ArrayList<>();

        // Break ties with Energy
        if (knowledgeWinners.size() > 1) {
            System.out.println("Knowledge Tie detected! Converting to Power (Energy) check...");
            int maxEnergy = -1;
            // Calculate max energy among knowledge winners only
            for (Species s : knowledgeWinners) {
                int e = 0;
                switch (s) {
                    case BOWSER:
                        e = bowserEnergy;
                        break;
                    case KING_BOO:
                        e = kingBooEnergy;
                        break;
                    case LUIGI:
                        e = luigiEnergy;
                        break;
                    case MARIO:
                        e = marioEnergy;
                        break;
                }
                if (e > maxEnergy)
                    maxEnergy = e;
            }

            for (Species s : knowledgeWinners) {
                int e = 0;
                switch (s) {
                    case BOWSER:
                        e = bowserEnergy;
                        break;
                    case KING_BOO:
                        e = kingBooEnergy;
                        break;
                    case LUIGI:
                        e = luigiEnergy;
                        break;
                    case MARIO:
                        e = marioEnergy;
                        break;
                }
                if (e == maxEnergy) {
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
                resetMaster(s);
            }

            // Start recursive simulation
            Simulation playoff = new Simulation(finalWinners);
            playoff.run();
        }
    }

    private void resetMaster(Species s) {
        switch (s) {
            case BOWSER:
                MasterBowser.getInstance().reset();
                break;
            case KING_BOO:
                MasterKingBoo.getInstance().reset();
                break;
            case LUIGI:
                MasterLuigi.getInstance().reset();
                break;
            case MARIO:
                MasterMario.getInstance().reset();
                break;
        }
    }
}
