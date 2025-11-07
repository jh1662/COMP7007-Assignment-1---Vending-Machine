import java.util.Map;

public class ItemSlot { //< Composite pattern class.
    private final int capacity;
    private final Item item;
    //^ Slot can change item but class will be re-instantiated instead of being mutated because of cleaner logic.
    private int itemCount;
    ItemSlot(int capacity, Item item) {
        this.capacity = capacity;
        this.item = item;
        this.itemCount = 0;
        //^ Slot must be assigned to an item before it can be populated.
    }

    public String toString(int slotNum) { return "Slot #"+slotNum+this.item.toString()+"; stock at "+this.itemCount+". "; }
    //^ Method overloading.

    //: Forwarder methods:
    public double getPrice(){ return this.item.getPrice(); }
    public boolean checkID(int iD){ return this.item.checkID(iD); }
    public int getID(){ return this.item.getID(); }

    //: predicate methods
    public boolean isEmpty(){ return this.capacity == 0; }
    public boolean isFull(){ return this.capacity == this.itemCount; }

    public int getStock(){ return this.itemCount; }
    //^ Getter method for current stock.

    //: Owner/admin or customer (customer remove only), one can only physically add or remove one item at a time.
    public void removeItem(){
        //^ Remove the physical item, not the assignment.
        if (this.itemCount == 0){ throw new IllegalArgumentException("Slot is empty"); }
        this.itemCount--;
    }
    public void addItem(){
        if (this.itemCount == this.capacity){ throw new IllegalArgumentException("Slot is full"); }
        this.itemCount++;
    }

    public Map<String, String> render() {
        //* Expects either a map or null.
        if (this.item == null){ return null; }
        //^ Means slot is unassigned to an item.
        Map<String, String> details = this.item.render();
        details.put("stock", Integer.toString(this.itemCount));
        return details;
    }
}
