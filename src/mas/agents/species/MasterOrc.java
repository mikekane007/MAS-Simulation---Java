package mas.agents.species;

import mas.agents.MasterAgent;
import mas.agents.Species;

public class MasterOrc extends MasterAgent {
    private static MasterOrc instance;

    private MasterOrc(int x, int y) {
        super(x, y, Species.ORC);
    }

    public static synchronized MasterOrc getInstance(int x, int y) {
        if (instance == null) {
            instance = new MasterOrc(x, y);
        }
        return instance;
    }

    public static synchronized MasterOrc getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MasterOrc not initialized yet!");
        }
        return instance;
    }

    @Override
    public String getSymbol() {
        return "MO";
    }
}
