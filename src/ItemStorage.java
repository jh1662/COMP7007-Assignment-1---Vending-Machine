/**
 * "Composite design pattern" class that represents the item storage of the vending machine.
 * Manages item slots and their operations.
 * Each slot can hold a specific type of item up to a maximum amount.
 * <p>
 * This program assumes the vending machine is physically capable of detecting when an item is stocking of dispensing but not what item.
 * Therefore, no validation is done on whether the correct item is being stocked or dispensed.
 * Admin/owner is responsible for ensuring correct items are stocked in the correct slots.
 * <p>
 * Responsibility/purpose of vending machine's item storage and its logistics are delegated to this class.
 * <p>
 * Just like vending machine, item storage only handles physical operations and viewing status, not actions that require a series of operations to maintain SRP.
 * For example, instead of having a 'getItemCount' method for the customer proxy, it has a 'render' method (used by multiple proxies) that returns the current state of all slots where the customer proxy uses to calculate total item count.
 */
public class ItemStorage {
    private final int maxAmount;
    //^ How many items can one slot hold; cannot be physically changed.
    private final int maxSlots;
    //^ How many slots can the vending machine physically hold; cannot be physically changed.
    private final ItemSlot[] slots;
    //^ Admin/owner cannot physically change the slots after vending machine is created - hence 'final'.

    /**
     * Constructor to initialize the physical specifications of the item storage in the vending machine.
     * @param maxAmount Item capacity in each slot (consistent across all slots).
     * @param maxSlots  Maximum number of slots in the vending machine.
     */
    public ItemStorage(int maxAmount, int maxSlots) {
        this.maxAmount = maxAmount;
        this.maxSlots = maxSlots;
        this.slots =  new ItemSlot[maxSlots];
        //^ Just like coin storage fill, admin/owner can only insert items after vending machine is created.
    }

    /**
     * Finds appropriate slot based on item ID.
     * Slot must both be assigned to the item and hat at least one item in stock.
     * @param iD The unique identifier for the item.
     * @return The populated slot (assigned to the item and not empty); otherwise return 'null' indicated that no appropriate slot was found.
     */
    private ItemSlot findSlotByItem(int iD){
        //* Finds appropriate based on item ID and intent to refill or dispense.
        for (ItemSlot slot : this.slots) {
            if (slot == null) continue;
            //^ No point checking unassigned slots; also avoids null pointer exception ('NullPointerException').
            if (slot.checkID(iD) && !slot.isEmpty()) return slot;
        }
        return null;
    }
    /**
     * Helper method dispenses one item from a specific slot in the vending machine (one dispensed item per call).
     * Called locally by 'this.dispenseItem' method when in MAINTENANCE mode.
     * @param slotNum The slot number from which the item is to be dispensed (if possible).
     * @throws IllegalArgumentException If the slot number does not exist in vending machine, the slot is unassigned, or the slot is empty.
     */
    private void dispenseItemBySlotNum(int slotNum){
        //* When machine restock an item (by admin/owner only entered item ID).
        if (slotNum < 0 || slotNum >= this.slots.length) throw new IllegalArgumentException("Slot number out of range");
        ItemSlot slot = this.slots[slotNum];
        if (slot == null) throw new IllegalArgumentException("Cannot remove items from a unassigned slot.");
        if (slot.isEmpty()) throw new IllegalArgumentException("Cannot remove items in an empty slot.");
        slot.removeItem();
    }

    /**
     * Renders the current state of the item storage.
     * @return An array of item slot objects representing the current state of each slot in the vending machine.
     */
    public ItemSlot[] render() {
        return this.slots;
    }

    /**
     * Simple getter method for 'this.maxAmount'.
     * @return Item capacity in each slot.
     */
    public int getMaxAmount(){
        return this.maxAmount;
    }
    /**
     * Simple getter method for 'this.getMaxSlots'.
     * @return Total number of slots in the vending machine.
     */
    public int getMaxSlots(){
        return this.maxSlots;
    }
    /**
     * Restocks one item into the vending machine (one restocked item per call).
     * Can only be triggered by an admin/owner.
     * @param slotNum The slot number where the item is to be restocked.
     * @throws IllegalArgumentException If the slot number does not exist in vending machine, the slot is unassigned, or the slot is already full.
     */
    public void restockItem(int slotNum){
        //* When machine restock an item (by admin/owner only entered item ID).
        if (slotNum < 0 || slotNum >= this.slots.length) throw new IllegalArgumentException("Slot number out of range");
        ItemSlot slot = this.slots[slotNum];
        if (slot == null) throw new IllegalArgumentException("Cannot put items in a unassigned slot.");
        if (slot.isFull()) throw new IllegalArgumentException("Cannot put items in a completely populated slot.");
        slot.addItem();
    }
    /**
     * Dispenses an item from the vending machine (one dispensed item per call).
     * Can be triggered by either a customer purchasing an item or an admin/owner dispensing broken or expired item for maintenance.
     * @param identity   If 'isMaintenance' is true, this represents the slot number; otherwise, it represents the item ID.
     *                   This is because customer choose by item ID while admin/owner choose by slot number.
     * @param isMaintenance Indicates whether the action is performed by an admin/owner for maintenance purposes.
     *                      If true, 'identity' is treated as slot number; if false, 'identity' is treated as item ID.
     * @throws IllegalArgumentException if the slot number is out of range, the slot is unassigned, or the slot is empty.
     *                                  Also thrown if the item ID is not found or the corresponding slot is out of stock.
     */
    public void dispenseItem(int identity, boolean isMaintenance){
        //* When machine dispenses an item (by customer, or admin/owner, entered item ID).
        if (isMaintenance){
            //* When admin/owner entered slot number to dispense item.
            this.dispenseItemBySlotNum(identity);
            return;
        }
        //: When customer buys an item.
        ItemSlot slot = findSlotByItem(identity);
        if (slot == null) throw new IllegalArgumentException("Item not found or out of stock.");
        slot.removeItem();
    }

    //: Admin/owner only - assign or unassign entire slot from/to vending machine.
    /**
     * Assigns a slot from the vending machine.
     * Can only be performed if the slot currently unassigned.
     * @param slotNum The slot number to be unassigned.
     * @throws IllegalArgumentException If the slot number does not exist in vending machine or if the slot is already assigned.
     */
    public void assignSlot(int slotNum, Item item){
        if (slotNum < 0 || slotNum >= this.maxSlots) throw new IllegalArgumentException("Slot number out of range.");
        if (this.slots[slotNum] != null) throw new IllegalArgumentException("Slot already assigned.");
        this.slots[slotNum] = new ItemSlot(this.maxAmount, item);
    }
    /**
     * Unassigns a slot from the vending machine.
     * Can only be performed if the slot is empty.
     * @param slotNum The slot number to be unassigned.
     * @throws IllegalArgumentException If the slot number does not exist in vending machine or if the slot is not empty.
     */
    public void unassignSlot(int slotNum){
        if (slotNum < 0 || slotNum >= this.maxSlots) throw new IllegalArgumentException("Slot number out of range.");
        if (!this.slots[slotNum].isEmpty()) throw new IllegalArgumentException("Cannot unassign a slot that physically have items inside it.");
        this.slots[slotNum] = null;
    }
}
