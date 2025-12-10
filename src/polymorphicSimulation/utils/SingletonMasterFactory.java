package polymorphicSimulation.utils;

import polymorphicSimulation.agents.*;

public class SingletonMasterFactory {

    public static Agent createAgent(Species species, int x, int y, int maxEnergy) {
        switch (species) {
            case BOWSER:
                return new Bowser(x, y, maxEnergy);
            case KING_BOO:
                return new KingBoo(x, y, maxEnergy);
            case LUIGI:
                return new Luigi(x, y, maxEnergy);
            case MARIO:
                return new Mario(x, y, maxEnergy);
            default:
                throw new IllegalArgumentException("Unknown species: " + species);
        }
    }

    public static Master getMaster(Species species, int x, int y) {
        return Master.getInstance(species, x, y);
    }

    public static Master getMaster(Species species) {
        return Master.getInstance(species);
    }
}
