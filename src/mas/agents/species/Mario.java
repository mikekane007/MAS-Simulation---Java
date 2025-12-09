package mas.agents.species;

import mas.agents.MobileAgent;
import mas.agents.Species;

public class Mario extends MobileAgent {
    public Mario(int x, int y, int maxEnergy) {
        super(x, y, Species.MARIO, maxEnergy);
    }

    @Override
    public String getSymbol() {
        return "M";
    }
}
