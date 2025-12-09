package mas.agents.species;

import mas.agents.MobileAgent;
import mas.agents.Species;

public class Goblin extends MobileAgent {
    public Goblin(int x, int y, int maxEnergy) {
        super(x, y, Species.GOBLIN, maxEnergy);
    }

    @Override
    public String getSymbol() {
        return "G";
    }
}
