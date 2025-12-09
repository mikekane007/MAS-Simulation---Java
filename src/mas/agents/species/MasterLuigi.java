package mas.agents.species;

import mas.agents.MasterAgent;
import mas.agents.Species;

public class MasterLuigi extends MasterAgent {
    private static MasterLuigi instance;

    private MasterLuigi(int x, int y) {
        super(x, y, Species.LUIGI);
    }

    public static synchronized MasterLuigi getInstance(int x, int y) {
        if (instance == null) {
            instance = new MasterLuigi(x, y);
        }
        return instance;
    }

    public static synchronized MasterLuigi getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MasterLuigi has not been initialized yet!");
        }
        return instance;
    }

    @Override
    public String getSymbol() {
        return "ML"; // Master Luigi
    }
}
