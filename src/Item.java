import java.util.Map;

/**
 * Abstract class representing a generic item in the vending machine.
 * It serves as a base for specific item types like snacks and drinks.
 */
public abstract class Item {
    private final String name;
    private final int iD;
    private final double price;

    /**
     * Constructor initializes the item with its name, ID, and price; subclasses adds more specific attributes.
     * @param name  What it is called for the customer's knowledge.
     * @param iD    The unique identifier (unique in subject vending machine) for the item.
     * @param price How much, each of this specific item, costs.
     */
    public Item(String name, int iD, double price) {
        this.name = name;
        this.iD = iD;
        this.price = price;
    }

    /**
     * Abstract method to get the type of the item (like snack, drink or miscellaneous). Does not make use of a type field to simplify instantiation.
     * @return ItemType enum representing the type of the item.
     */
    abstract ItemType getType();

    //: Getter methods.
    /**
     * Gets the price of the item. The item factory ensures that the price is valid upon creation.
     * @return price - positive double with max two decimal places.
     */
    public double getPrice() { return price; }
    //^ For customer purchases.
    /**
     * Gets the identifier (unique in subject vending machine) of the item. The item factory ensures that the price is valid upon creation.
     * @return identification code - positive integer.
     */
    public int getID() { return iD; }
    //^ For finding duplicate iDs in item storage.
    /**
     * Gets the name of item. The item factory ensures that the name is valid upon creation.
     * @return name - non-null String that cannot be empty/blank of exceed 30 characters long .
     */
    public String getName() { return name; }

    /**
     * Checks if the provided ID matches the item's ID.
     * @param iD The ID to check against the item's ID.
     * @return true if the IDs match, false otherwise.
     */
    public boolean checkID(int iD){ return iD == this.iD; }
    //^ Predicate method.
    //^ Used to select item by ID.
    //^ More secure than just giving 'this.codeID'.

    /**
     * Renders the item's details. Expanded upon in subclasses to include extra attributes. Very useful for wanting all or one of the attributes by calling just this one method and then choosing which ones to use.
     * @return Item's name, type (drink, snack, etc.), ID, and price.
     */
    public Map<String, String> render(){
        //* For both user and owner/admin - seeing what the vending machine offers.
        return Map.of(
            "name",this.name,
            "type",this.getType().toString(),
            "ID", Integer.toString(this.iD),
            "Price", Double.toString(this.price));
    }
}
