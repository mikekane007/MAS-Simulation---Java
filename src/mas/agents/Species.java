package mas.agents;

public enum Species {
    BOWSER(Alliance.EVIL, "B", "MB"),
    KING_BOO(Alliance.EVIL, "K", "MK"),
    LUIGI(Alliance.GOOD, "L", "ML"),
    MARIO(Alliance.GOOD, "M", "MM");

    private final Alliance alliance;
    private final String symbol;
    private final String masterSymbol;

    Species(Alliance alliance, String symbol, String masterSymbol) {
        this.alliance = alliance;
        this.symbol = symbol;
        this.masterSymbol = masterSymbol;
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getMasterSymbol() {
        return masterSymbol;
    }
}
