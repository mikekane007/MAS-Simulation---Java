package mas.agents.species;

import mas.agents.MobileAgent;
import mas.agents.Species;

public class Human extends MobileAgent {
    public Human(int x, int y, int maxEnergy) {
        super(x, y, Species.HUMAN, maxEnergy);
    }

    @Override
    public String getSymbol() {
        return "H";
    }
}
