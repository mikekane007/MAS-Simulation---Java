package mas.agents.species;

import mas.agents.MobileAgent;
import mas.agents.Species;

public class Luigi extends MobileAgent {
    public Luigi(int x, int y, int maxEnergy) {
        super(x, y, Species.LUIGI, maxEnergy);
    }

    @Override
    public String getSymbol() {
        return "L";
    }
}
