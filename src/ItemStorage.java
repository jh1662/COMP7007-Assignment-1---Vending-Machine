import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class ItemStorage {
    private final int maxAmount;
    //^ How many items can one slot hold; cannot be physically changed.
    private final int maxSlots;
    //^ How many slots can the vending machine physically hold; cannot be physically changed.
    private final ItemSlot[] slots;
    //^ Admin/owner cannot physically change the slots after vending machine is created - hence 'final'.

    public ItemStorage(int maxAmount, int maxSlots) {
        this.maxAmount = maxAmount;
        this.maxSlots = maxSlots;
        this.slots =  new ItemSlot[maxSlots];
        //^ Just like coin storage fill, admin/owner can only insert items after vending machine is created.
    }

    private ItemSlot findID(int iD, boolean toRefill){
        //* Finds appropriate based on item ID and intent to refill or dispense.
        for (ItemSlot slot : this.slots) {
            if (slot.checkID(iD)){
                if (toRefill){ if (!slot.isFull()){ return slot; } }
                if (!slot.isEmpty()){ return slot; }
            }
        }
        return null;
    }

    public int getMaxAmount(){
        return this.maxAmount;
    }
    public int getMaxSlots(){
        return this.maxSlots;
    }

    public void dispenseItem(int iD){
        //* When machine dispenses an item (by customer, or admin/owner, entered item ID).
        ItemSlot slot = findID(iD,false);
        if (slot == null){ throw new IllegalArgumentException("Item not found or out of stock."); }
        slot.removeItem();
    }
    public void restockItem(int iD){
        //* When machine restock an item (by admin/owner only entered item ID).
        ItemSlot slot = findID(iD,true);
        if (slot == null){ throw new IllegalArgumentException("Item not found or is at full stock."); }
        slot.addItem();
    }

    //: Admin/owner only - assign or unassign entire slot from/to vending machine.
    public void unassignSlot(int slotNum){
        if (slotNum < 0 || slotNum >= this.maxSlots){ throw new IllegalArgumentException("Slot number out of range."); }
        if (!this.slots[slotNum].isEmpty()){ throw new IllegalArgumentException("Cannot unassign a slot that physically have items inside it."); }
        this.slots[slotNum] = null;
    }
    public void assignSlot(int slotNum, Item item){
        if (slotNum < 0 || slotNum >= this.maxSlots){ throw new IllegalArgumentException("Slot number out of range."); }
        if (this.slots[slotNum] != null){ throw new IllegalArgumentException("Slot already assigned."); }
        this.slots[slotNum] = new ItemSlot(this.maxAmount, item);
    }

    public ItemSlot[] render() {
        return this.slots;
    }
    /*
    public Map<String, String>[] render() {
        ArrayList<Map<String, String>> slots = new ArrayList<>();
        for (ItemSlot slot : this.Slots){
            if (slot == null) {
                //* Expected to possibly have null entries - unassigned slots.
                slots.add(null);
                continue;
            }
            slots.add(slot.render());
        }
        return slots.toArray(new Map[0]);
        //^ Implicit conversion.
        //^ Slot ID is implied by array index.
    }
    */

}
