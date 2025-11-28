
/**
 * Not a part of the vending machine, but is part of the administrative logistics.
 * <p>
 * Supports creation of Drink, Snack, and MiscellaneousItem item types ('Item' subclass).
 * <p>
 * "factory method design pattern" implementation for creating valid items (relieves item  subclasses' constructor of validation code).
 * Enforced single instance using the "singleton design pattern".
 * <p>
 * "factory method design pattern" is used instead of "abstract factory design pattern" as only one creation method is needed here instead of added complexity.
 * Multiple subclasses are made here, but they have very simplistic and lightweight nature and their creation does not require complex logic.
 * <p>
 * Constructor is not needed for factory method pattern as the only initialization is the singleton instance.
 */
public class ItemFactory {
    //* Factory method and singleton pattern; not abstract factory pattern as only one method is needed.
    //! Using an abstract factory pattern instead would be overkill here.
    private static final ItemFactory instance = new ItemFactory();

    /**
     * Getter for the only instance of ItemFactory ('ItemFactory.instance').
     * <p>
     * Enforces singleton pattern.
     * @return instance reference of ItemFactory instance.
     */
    public static ItemFactory getInstance() { return instance; }

    /**
     * Helper method validates the provided price to ensure it meets the required criteria.
     * Price must be non-negative and have at most two decimal places.
     * <p>
     * Used internally before item creation to ensure data integrity.
     * @param price The price to validate.
     * @throws IllegalArgumentException If the price is negative or has more than two decimal places.
     */
    private void validatePrice(double price){
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (price * 100 % 1 != 0) throw new IllegalArgumentException("Price cannot have more than 2 decimal places");
        //^ Dividing by int instead of double to avoid floating point precision issues (real issue).
    }
    /**
     * Helper forwarder method to 'this.validateNum'.
     * <p>
     * Validates the provided ID to ensure it meets the required criteria.
     * ID must be a positive integer (greater than zero) - same as other number validations.
     * <p>
     * @param iD The identifier to validate.
     * @throws IllegalArgumentException If the ID is zero or negative.
     */
    private void validateID(int iD){ this.validateNum(iD); }

    /**
     * Helper method validates the provided name to ensure it meets the required criteria.
     * Name must be non-blank and have a maximum length of 30 characters.
     * <p>
     * Used internally before item creation to ensure data integrity.
     * @param name The name to validate.
     * @throws IllegalArgumentException If the name is blank or exceeds 30 characters.
     */
    private void validateName(String name){
        if (name.isBlank()) throw new IllegalArgumentException("Name cannot be null or blank");
        //^ 'isBlank()' counts just whitespaces also as empty strings.
        if (name.length() > 30) throw new IllegalArgumentException("Name must be below 31 characters long");
        //^ Cannot be too long to avoid UI issues - names going across the screen or using up too much screen space.
    }
    private void validateNum(int num){
        if (num <= 0) throw new IllegalArgumentException("Numerical extra attribute cannot be zero or below");
    }
    /**
     * Factory method for creating items based on the provided type and attributes.
     * <p>
     * Validates common attributes (ID, price, name) and specific attributes based on item type.
     * Volume for drinks, weight for snacks, and description for miscellaneous items.
     * <p>
     * For DRINK type, expects 'extraAttribute' to be an int representing volume in ml.
     * For SNACK type, expects 'extraAttribute' to be an int representing weight in grams.
     * For MISCELLANEOUS type, expects 'extraAttribute' to be a String representing description.
     * <p>
     * Polymorphic behavior is achieved by returning the base class 'Item', allowing flexibility in handling different item types.
     * <p>
     * Polymorphism ('Object') was preferred over generics ('&lt;T&gt;') because there is no type preservation needed and keeps code both simpler and more flexible.
     * @param type           Determines what type of item to make (DRINK, SNACK, MISCELLANEOUS).
     * @param name           The name of the item.
     * @param iD             The unique identifier for the item.
     * @param price          The price of the item.
     * @param extraAttribute The specific attribute required by the item subclass (volume, weight, or description).
     * @return The created Item instance (Drink, Snack, or MiscellaneousItem).
     * @throws IllegalArgumentException If any validation fails or if 'extraAttribute' is of incorrect type.
     */
    public Item createItem(ItemType type, String name, int iD, double price, Object extraAttribute) {
        this.validateID(iD);
        this.validatePrice(price);
        this.validateName(name);
        switch (type) {
            case DRINK:
                if (extraAttribute instanceof Integer volume) {
                    this.validateNum(volume);

                    return new Drink(name, iD, price, volume);
                }
                throw new IllegalArgumentException("Invalid extra attribute for Drink. Expected Integer volume.");
            case SNACK:
                if (extraAttribute instanceof Integer weight) {
                    this.validateNum(weight);

                    return new Snack(name, iD, price, weight);
                }
                throw new IllegalArgumentException("Invalid attribute for Snack. Expected Integer weight.");
            case MISCELLANEOUS:
                if (extraAttribute instanceof String description) {
                    //* No specific validation for description; even an empty description is allowed.

                    return new MiscellaneousItem(name, iD, price, description);
                }
                throw new IllegalArgumentException("Invalid attribute for MiscellaneousItem. Expected String description.");
            default:
                throw new IllegalArgumentException("Unknown type; if not belonging to a known type, then use the miscellaneous category.");
                //^ Will not be called as every enum value is covered; still here for potential external errors.
        }
    }
}
