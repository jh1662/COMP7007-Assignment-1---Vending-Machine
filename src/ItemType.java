public enum ItemType {
    //: Not alot, but more can be added (extension) if needed such as 'MEAL_MAIN'.
    MISCELLANEOUS("miscellaneous item"),
    DRINK("drink"),
    SNACK("snack");

    private final String value;

    ItemType(String value) { this.value = value; }

    @Override
    public String toString() { return value; }
    //^ Used instead of a 'return switch' for easier extensibility.
}
