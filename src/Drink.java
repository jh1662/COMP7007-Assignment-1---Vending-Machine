import java.util.HashMap;
import java.util.Map;

/**
 * Item subclass that represents drink items in the vending machine.
 * Inherits from Item class and adds volume attribute.
 * <p>
 * It is a leaf class in the item hierarchy as it is stored in the "composite design pattern" 'ItemSlot' class.
 * <p>
 * See corresponding superclass method documentation for more information.
 */
public class Drink extends Item{
    private final int volume;
    //^ In ml units.

    /**
     * Constructor expands on Item class's constructor by adding volume attribute.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @param name   What it is called for the customer's knowledge.
     * @param iD     The unique identifier (unique in subject vending machine) for the item.
     * @param price  How much, each of this specific item, costs.
     * @param volume How much liquid it contains, in milliliters.
     */
    public Drink(String name, int iD, double price, int volume) {
        super(name, iD, price);
        this.volume = volume;
    }

    /**
     * Implemented abstract method to specify item type as a drink.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @return ItemType.DRINK enum value to represent drink.
     */
    @Override
    ItemType getType() { return ItemType.DRINK; }

    /**
     * View method (specialized getter method) that expands on Item's (superclass) render method by adding volume detail.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @return Item's name, type (drink), ID, price, and volume in ml.
     */
    @Override
    public Map<String, String> render() {
        Map<String, String> details = new HashMap<>(super.render());
        //^ 'new HashMap<>' makes copy to allow appending more details.
        details.put("volume", Integer.toString(this.volume));
        return details;
    }
}