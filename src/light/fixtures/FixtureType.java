package light.fixtures;

public enum FixtureType {
    SPOT,
    WASH,
    DEVICE;

    public static FixtureType getFixtureType(String text) {
        for (FixtureType type : FixtureType.values()) {
            if (type.toString().equals(text)) return type;
        }
        return null;
    }
}
