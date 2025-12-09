package mas.agents;

public class MasterAgent extends LivingBeing {
    private static final java.util.Map<Species, MasterAgent> instances = new java.util.EnumMap<>(Species.class);

    private MasterAgent(int x, int y, Species species) {
        super(x, y, species);
    }

    public static synchronized MasterAgent getInstance(Species species, int x, int y) {
        if (!instances.containsKey(species)) {
            instances.put(species, new MasterAgent(x, y, species));
        }
        return instances.get(species);
    }

    public static synchronized MasterAgent getInstance(Species species) {
        if (!instances.containsKey(species)) {
            throw new IllegalStateException("MasterAgent for " + species + " has not been initialized yet!");
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
