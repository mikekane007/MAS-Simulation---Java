package mas.agents.species;

import mas.agents.MasterAgent;
import mas.agents.Species;

public class MasterBowser extends MasterAgent {
    private static MasterBowser instance;

    private MasterBowser(int x, int y) {
        super(x, y, Species.BOWSER);
    }

    public static synchronized MasterBowser getInstance(int x, int y) {
        if (instance == null) {
            instance = new MasterBowser(x, y);
        }
        return instance;
    }

    public static synchronized MasterBowser getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MasterBowser has not been initialized yet!");
        }
        return instance;
    }

    @Override
    public String getSymbol() {
        return "MB"; // Master Bowser
    }
}
