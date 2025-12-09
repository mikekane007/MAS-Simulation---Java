package mas.agents;

public abstract class MasterAgent extends LivingBeing {

    public MasterAgent(int x, int y, Species species) {
        super(x, y, species);
    }

    // Stationary
    public void move() {
        // Do nothing
    }

    public void reset() {
        this.knowledge.clear();
    }

    @Override
    public String toString() {
        return "MASTER_" + species.name();
    }
}
