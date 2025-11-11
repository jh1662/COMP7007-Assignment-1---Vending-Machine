import java.util.HashMap;
import java.util.Map;

/**
 * Item subclass that represents snack items in the vending machine.
 * Inherits from Item class and adds description attribute.
 * <p>
 * This class is used for items that do not fit into any other predefined category (snack and drink).
 * Examples include utensils, napkins, or promotional items.
 * If an item has many specific attributes or many items share similar attributes, consider creating a new item subclass/category instead (extending).
 * <p>
 * It is a leaf class in the item hierarchy as it is stored in the "composite design pattern" 'ItemSlot' class.
 * <p>
 * See corresponding superclass method documentation for more information.
 */
public class MiscellaneousItem extends Item{
    private final String description;
    //^ Due to no specialty (like weight or volume), its needs a description to explain its exact contents.
    //^ Recommended to keep said description short and concise, for example "holds two forks".

    /**
     * Constructor expands on Item class's constructor by adding volume attribute.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @param name        What it is called for the customer's knowledge.
     * @param iD          The unique identifier (unique in subject vending machine) for the item.
     * @param price       How much, each of this specific item, costs.
     * @param description Description of the miscellaneous item - explains why it is miscellaneous.
     */
    public MiscellaneousItem(String name, int iD, double price, String description) {
        super(name, iD, price);
        this.description = description;
    }

    /**
     * Implemented abstract method to specify item type as a miscellaneous item.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @return ItemType.MISCELLANEOUS enum value to represent miscellaneous.
     */
    @Override
    ItemType getType() {
        return ItemType.MISCELLANEOUS;
    }

    /**
     * View method (specialized getter method) that expands on Item's render (superclass) method by adding description.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @return Item's name, type (drink), ID, price, and description (explains why it is miscellaneous).
     */
    @Override
    public Map<String, String> render() {
        Map<String, String> details = new HashMap<>(super.render());
        //^ 'new HashMap<>' makes copy to allow appending more details.
        details.put("description", this.description);
        return details;
    }
}