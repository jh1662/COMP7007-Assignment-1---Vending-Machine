/**
 * Enum representing different types of items in the vending machine.
 * Helps categorize items for better management and user experience.
 * Also, essential for the item factory (factory method design pattern) when creating items.
 * <p>
 * Description of each enum value is given as comments its scope.
 */
public enum ItemType {
    //: Not alot, but more can be added (extension) if needed such as 'MEAL_MAIN'.
    MISCELLANEOUS("miscellaneous item"), //< Used to notify subclass of 'Item' is 'MiscellaneousItem'.
     //^ Examples: utensils, napkins, promotional items.
    DRINK("drink"), //< Used to notify subclass of 'Item' is 'Drink'.
     //^ Examples: soda, water, juice.
    SNACK("snack"); //< Used to notify subclass of 'Item' is 'Snack'.
     //^ Examples: crisps, candy bars, cookies.

    /**
     * String representation of the item type for user-friendly display.
     */
    private final String value;

    ItemType(String value) { this.value = value; }

    /**
     * Overrides default toString method to provide user-friendly string representation.
     * @return Item type (drink, snack, miscellaneous item).
    */
    @Override
    public String toString() { return value; }
    //^ Used instead of a 'return switch' for easier extensibility.
}
