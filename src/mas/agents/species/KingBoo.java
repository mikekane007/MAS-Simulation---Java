package mas.agents.species;

import mas.agents.MobileAgent;
import mas.agents.Species;

public class KingBoo extends MobileAgent {
    public KingBoo(int x, int y, int maxEnergy) {
        super(x, y, Species.KING_BOO, maxEnergy);
    }

    @Override
    public String getSymbol() {
        return "K";
    }
}
