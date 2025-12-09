package mas.agents.species;

import mas.agents.MasterAgent;
import mas.agents.Species;

public class MasterHuman extends MasterAgent {
    private static MasterHuman instance;

    private MasterHuman(int x, int y) {
        super(x, y, Species.HUMAN);
    }

    public static synchronized MasterHuman getInstance(int x, int y) {
        if (instance == null) {
            instance = new MasterHuman(x, y);
        }
        return instance;
    }

    public static synchronized MasterHuman getInstance() {
        if (instance == null)
            throw new IllegalStateException("Not initialized");
        return instance;
    }

    @Override
    public String getSymbol() {
        return "MH";
    }
}
