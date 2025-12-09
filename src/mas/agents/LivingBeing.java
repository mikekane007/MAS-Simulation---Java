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

    public String getSymbol() {
        return species.getSymbol();
    }

    public Species getSpecies() {
        return species;
    }

    public List<Message> getKnowledge() {
        return knowledge;
    }

    public void addMessage(Message m) {
        if (!knowledge.contains(m)) {
            knowledge.add(m);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
