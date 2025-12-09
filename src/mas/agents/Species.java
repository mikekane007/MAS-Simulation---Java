package mas.agents;

public enum Species {
    ORC(Alliance.EVIL),
    GOBLIN(Alliance.EVIL),
    ELF(Alliance.GOOD),
    HUMAN(Alliance.GOOD);

    private final Alliance alliance;

    Species(Alliance alliance) {
        this.alliance = alliance;
    }

    public Alliance getAlliance() {
        return alliance;
    }
}
