package mas.agents.species;

import mas.agents.MasterAgent;
import mas.agents.Species;

public class MasterElf extends MasterAgent {
    private static MasterElf instance;

    private MasterElf(int x, int y) {
        super(x, y, Species.ELF);
    }

    public static synchronized MasterElf getInstance(int x, int y) {
        if (instance == null) {
            instance = new MasterElf(x, y);
        }
        return instance;
    }

    public static synchronized MasterElf getInstance() {
        if (instance == null)
            throw new IllegalStateException("Not initialized");
        return instance;
    }

    @Override
    public String getSymbol() {
        return "ME";
    }
}
