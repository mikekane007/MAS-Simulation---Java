package mas.agents.species;

import mas.agents.MasterAgent;
import mas.agents.Species;

public class MasterMario extends MasterAgent {
    private static MasterMario instance;

    private MasterMario(int x, int y) {
        super(x, y, Species.MARIO);
    }

    public static synchronized MasterMario getInstance(int x, int y) {
        if (instance == null) {
            instance = new MasterMario(x, y);
        }
        return instance;
    }

    public static synchronized MasterMario getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MasterMario has not been initialized yet!");
        }
        return instance;
    }

    @Override
    public String getSymbol() {
        return "MM"; // Master Mario
    }
}
