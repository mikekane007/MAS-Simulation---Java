package mas.agents;

import mas.data.Message;
import java.util.ArrayList;
import java.util.List;

public abstract class LivingBeing {
    protected int x;
    protected int y;
    protected Species species;
    protected List<Message> knowledge;

    public LivingBeing(int x, int y, Species species) {
        this.x = x;
        this.y = y;
        this.species = species;
        this.knowledge = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Species getSpecies() {
        return species;
    }

    public List<Message> getKnowledge() {
        return knowledge;
    }

    public void addMessage(Message message) {
        if (!knowledge.contains(message)) {
            knowledge.add(message);
        }
    }

    public abstract String getSymbol();
}
