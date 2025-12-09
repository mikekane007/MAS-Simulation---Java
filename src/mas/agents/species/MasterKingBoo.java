package mas.agents.species;

import mas.agents.MasterAgent;
import mas.agents.Species;

public class MasterKingBoo extends MasterAgent {
    private static MasterKingBoo instance;

    private MasterKingBoo(int x, int y) {
        super(x, y, Species.KING_BOO);
    }

    public static synchronized MasterKingBoo getInstance(int x, int y) {
        if (instance == null) {
            instance = new MasterKingBoo(x, y);
        }
        return instance;
    }

    public static synchronized MasterKingBoo getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MasterKingBoo has not been initialized yet!");
        }
        return instance;
    }

    @Override
    public String getSymbol() {
        return "MK"; // Master King Boo
    }
}
