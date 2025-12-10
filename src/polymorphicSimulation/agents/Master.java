package polymorphicSimulation.agents;

import java.util.EnumMap;
import java.util.Map;

public class Master extends LivingBeing {
    private static final Map<Species, Master> instances = new EnumMap<>(Species.class);

    private Master(int x, int y, Species species) {
        super(x, y, species);
    }

    public static synchronized Master getInstance(Species species, int x, int y) {
        if (!instances.containsKey(species)) {
            instances.put(species, new Master(x, y, species));
        }
        return instances.get(species);
    }

    public static synchronized Master getInstance(Species species) {
        if (!instances.containsKey(species)) {
            throw new IllegalStateException("Master for " + species + " has not been initialized yet!");
        }
        return instances.get(species);
    }

    // Stationary
    public void move() {
        // Do nothing
    }

    public void reset() {
        this.knowledge.clear();
    }

    @Override
    public String getSymbol() {
        return species.getMasterSymbol();
    }

    @Override
    public String toString() {
        return "MASTER_" + species.name();
    }
}
