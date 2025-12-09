package mas.agents.species;

import mas.agents.MasterAgent;
import mas.agents.Species;

public class MasterGoblin extends MasterAgent {
    private static MasterGoblin instance;

    private MasterGoblin(int x, int y) {
        super(x, y, Species.GOBLIN);
    }

    public static synchronized MasterGoblin getInstance(int x, int y) {
        if (instance == null) {
            instance = new MasterGoblin(x, y);
        }
        return instance;
    }

    public static synchronized MasterGoblin getInstance() {
        if (instance == null)
            throw new IllegalStateException("Not initialized");
        return instance;
    }

    @Override
    public String getSymbol() {
        return "MG";
    }
}
