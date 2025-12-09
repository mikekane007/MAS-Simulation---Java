package mas.agents;

public enum Species {
    BOWSER(Alliance.EVIL),
    KING_BOO(Alliance.EVIL),
    LUIGI(Alliance.GOOD),
    MARIO(Alliance.GOOD);

    private final Alliance alliance;

    Species(Alliance alliance) {
        this.alliance = alliance;
    }

    public Alliance getAlliance() {
        return alliance;
    }
}
