import java.util.Map;

/**
 * "composite design pattern" class that represents a slot in the vending machine assigned to holds a specific item type.
 * Stores the 'Item' class instance inside as the assigned item type.
 * Instances of this class are stored in an array inside the 'ItemStorage' class.
 * <p>
 * Responsibility/purpose of each vending machine slot is delegated to each instance of this class.
 */
public class ItemSlot { //< Composite pattern class.
    private final int capacity;
    private final Item item;
    //^ Slot can change item but class will be re-instantiated instead of being mutated because of cleaner logic.
    private int itemCount;

    /**
     * Constructor for initializing the physical capacity and what item type the slots is assigned to hold.
     * @param capacity Maximum number of items the slot can hold.
     * @param item     The specific item type assigned to this slot.
     */
    ItemSlot(int capacity, Item item) {
        this.capacity = capacity;
        this.item = item;
        this.itemCount = 0;
        //^ Slot must be assigned to an item before it can be populated.
    }

    //: Forwarder methods:
    /**
     * Forwarder method to 'this.item.getPrice' method.
     * @return Price of the assigned item.
     */
    public double getPrice(){ return this.item.getPrice(); }
    /**
     * Forwarder method to 'this.item.checkID' method.
     * @param iD The unique identifier to check against the assigned item.
     * @return 'true' if the assigned item's ID matches the provided ID; otherwise 'false'.
     */
    public boolean checkID(int iD){ return this.item.checkID(iD); }
    /**
     * Getter method for 'this.itemCount'.
     * @return Number of items currently in the slot.
     */
    public int getStock(){ return this.itemCount; }
    /**
     * Getter method for 'this.item'.
     * @return The assigned item in the slot; can be null if unassigned.
     */
    public Item getItem(){ return this.item; }

    //: predicate methods
    /**
     * Predicate method to check if the slot is empty - have no items.
     * <p>
     * Not related to whether the slot is assigned to an item or not.
     * @return 'true' if the slot has zero items; otherwise 'false'.
     */
    public boolean isEmpty(){ return this.itemCount == 0; }
    /**
     * Predicate method to check if the slot is full - have maximum items.
     * <p>
     * Not related to whether the slot is assigned to an item or not.
     * @return 'true' if the slot has reached its capacity; otherwise 'false'.
     */
    public boolean isFull(){ return this.itemCount == this.capacity; }

    //: Owner/admin or customer (customer remove only), one can only physically add or remove one item at a time.
    /**
     * Adds one item to the slot.
     * Triggered only by an admin/owner stocking the vending machine in MAINTENANCE mode.
     * <p>
     * Not directly relevant to whether the slot is assigned to an item or not.
     * @throws IllegalArgumentException if the slot is already full.
     */
    public void addItem(){
        if (this.itemCount == this.capacity) throw new IllegalArgumentException("Slot is full");
        this.itemCount++;
    }
    /**
     * Removes one item from the slot.
     * Can be triggered by either a customer purchasing an item or an admin/owner dispensing broken or expired item for maintenance.
     * <p>
     * Not directly relevant to whether the slot is assigned to an item or not.
     * @throws IllegalArgumentException if the slot is already empty.
     */
    public void removeItem(){
        //^ Remove the physical item, not the assignment.
        if (this.itemCount == 0) throw new IllegalArgumentException("Slot is empty");
        this.itemCount--;
    }

    /**
     * Expands upon the assigned item's render method ('Item.render') by adding current stock detail.
     * @return Map of the assigned item's details along with the current stock.
     *         If a slot is unassigned (item is null), returns null.
     */
    public Map<String, String> render() {
        //* Expects either a map or null.
        if (this.item == null) return null;
        //^ Means slot is unassigned to an item.
        Map<String, String> details = this.item.render();
        details.put("stock", Integer.toString(this.itemCount));
        return details;
    }
}
