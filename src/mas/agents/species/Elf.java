package mas.agents.species;

import mas.agents.MobileAgent;
import mas.agents.Species;

public class Elf extends MobileAgent {
    public Elf(int x, int y, int maxEnergy) {
        super(x, y, Species.ELF, maxEnergy);
    }

    @Override
    public String getSymbol() {
        return "E";
    }
}
