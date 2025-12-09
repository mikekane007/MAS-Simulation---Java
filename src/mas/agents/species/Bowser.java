package mas.agents.species;

import mas.agents.MobileAgent;
import mas.agents.Species;

public class Bowser extends MobileAgent {
    public Bowser(int x, int y, int maxEnergy) {
        super(x, y, Species.BOWSER, maxEnergy);
    }

    @Override
    public String getSymbol() {
        return "B";
    }
}
