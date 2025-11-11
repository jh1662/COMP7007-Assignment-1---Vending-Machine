import java.util.HashMap;
import java.util.Map;

/**
 * Item subclass that represents snack items in the vending machine.
 * Inherits from Item class and adds weight attribute.
 * <p>
 * It is a leaf class in the item hierarchy as it is stored in the "composite design pattern" 'ItemSlot' class.
 * <p>
 * See corresponding superclass method documentation for more information.
 */
public class Snack extends Item{
    private final int weight;
    //^ In grams.

    /**
     * Constructor expands on Item class's constructor by adding volume attribute.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @param name   What it is called for the customer's knowledge.
     * @param iD     The unique identifier (unique in subject vending machine) for the item.
     * @param price  How much, each of this specific item, costs.
     * @param weight How much it weighs, in grams.
     */
    public Snack(String name, int iD, double price, int weight) {
        super(name, iD, price);
        this.weight = weight;
    }

    /**
     * Implemented abstract method to specify item type as a snack.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @return ItemType.SNACK enum value to represent snack.
     */
    @Override
    ItemType getType() {
        return ItemType.SNACK;
    }

    /**
     * View method (specialized getter method) that expands on Item's (superclass) render method by adding weight detail.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @return Item's name, type (drink), ID, price, and weight in g.
     */
    @Override
    public Map<String, String> render() {
        Map<String, String> details = new HashMap<>(super.render());
        //^ 'new HashMap<>' makes copy to allow appending more details.
        details.put("weight", Integer.toString(this.weight));
        return details;
    }
}