package mas.agents.species;

import mas.agents.MobileAgent;
import mas.agents.Species;

public class Orc extends MobileAgent {
    public Orc(int x, int y, int maxEnergy) {
        super(x, y, Species.ORC, maxEnergy);
    }

    @Override
    public String getSymbol() {
        return "O";
    }
}
