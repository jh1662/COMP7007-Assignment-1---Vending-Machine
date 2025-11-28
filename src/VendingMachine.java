import java.util.Map;

/**
 * VendingMachine class that represents the vending machine itself - item and coin storage, state management, and operations.
 * Methods are not actions, actions are handled by proxy classes.
 * <p>
 * The author cannot stress enough that the Single Responsibility Principle makes the vending machine class to not handle actions - it only handles operations.
 * Balance, basket, handling repeating operation calls (e.g. depositing 50 pence coin 3 times), and other action-related tasks are handled by the proxy classes.
 * <p>
 * "state design pattern" class managing different states of the vending machine, such as IDLE, PAYING, MAINTENANCE, etc.
 * Used for controlling the behavior of the vending machine based on its current state and who is using it (admin/owner or customer).
 * <p>
 * "composite design pattern" class delegating item storage to ItemStorage class and coin storage to CoinStorage class.
 */
public class VendingMachine {
    private VendingMachineState state;
    private final ItemStorage itemStorage;
    private final CoinStorage coinStorage;
    //! listeners (for displays) are handled by proxies (hence are not stored here).
    //! There is no (customer) balance field here as it is handles by the customer proxy class.

    /**
     * Constructor to initialize the vending machine with specified maximum slots, slot size, and coin storage capacities and acceptance.
     * All 3 specifications are immutable after vending machine creation; this is because one cannot physically change these properties of a vending machine.
     * @param maxSlots    Maximum number of item slots in the vending machine.
     * @param slotSize    Maximum number of items each slot can hold (consistent across all slots of vendoring machine instance).
     * @param coinStorage Initial coin storage represented as a map of British coins to their respective capacities.
     * @throws IllegalArgumentException if slotSize is less than or equal to 0
     * @throws IllegalArgumentException if any coin type has a capacity less than or equal to 0.
     */
    public VendingMachine(int maxSlots, int slotSize, Map<CoinGBP, Integer> coinStorage) {
        this.state = VendingMachineState.IDLE;
        this.itemStorage = new ItemStorage(slotSize, maxSlots);
        this.coinStorage = new CoinStorage(coinStorage);
    }

    //: State management methods.
    /**
     * Changes the state of the vending machine to a new state.
     * Must start from either IDLE or MAINTENANCE state to change state - only when owner/admin is using it or when no one was using it.
     * @param newState The new state to transition to.
     */
    public void changeState(VendingMachineState newState){
        //* Implementation for changing the state of the vending machine.
        //* Message intended for admin/owner.
        if (!(this.state == VendingMachineState.IDLE) && newState == VendingMachineState.MAINTENANCE) {
            //* Indentation used instead as single-line is too long
            throw new IllegalStateException("Cannot enter MAINTENANCE mode when not in IDLE state");
        }
        this.state = newState;
    }
    /**
     * Gets current state to help proxy classes determine allowed actions.
     * <p>
     * Called in the user role proxies (AdminProxy and CustomerProxy) to determine allowed actions based on current state.
     * @return The current state of the vending machine.
     */
    public VendingMachineState getState() {
        return this.state;
    }

    /**
     * Views the specifications of the vending machine - including maximum slots and slot size.
     * <p>
     * Called in admin proxy constructor (not need for customers).
     * Called once per constructor creation to initialize display listeners.
     * Unfrequent calls because specifications do not change after vending machine creation - immutable.
     * @return An array containing the maximum slots and slot size of the vending machine respectively.
     */
    public int[] viewSpecifications() {
        //* Implementation for viewing the specifications of the vending machine.
        //* Message intended for admin/owner.
        return new int[]{
            this.itemStorage.getMaxSlots(),
            this.itemStorage.getMaxAmount()
        };
        //! For code simplicity, the accepted coins specs are fetched and interpreted from 'this.insertCoin'
    }

    //: Coin storage getter methods.
    /**
     * Views the coin storage maximums of the vending machine.
     * <p>
     * Called in admin proxy constructor (not need for customers).
     * Called once per proxy instance creation to initialize display listeners.
     * Unfrequent calls because coin capacities do not change after vending machine creation - immutable.
     * @return A map containing the maximum capacities for each accepted coin type.
     */
    public Map<CoinGBP, Integer> getCoinMaxes(){
        //* Implementation for viewing the coin storage maximums of the vending machine.
        //* Message intended for admin/owner.
        //* One-shot method.
        return this.coinStorage.getCoinMaxes();
    }
    /**
     * Gets the current coin storage counts of the vending machine.
     * <p>
     * Not to be confused with 'getCoinMaxes()' which gets the maximum capacities for each coin type.
     * @return A map containing the current counts for each coin type in the vending machine.
     * @throws IllegalStateException if the vending machine is not in MAINTENANCE state.
     */
    public Map<CoinGBP, Integer> getCoinStorage() {
        if (this.state != VendingMachineState.MAINTENANCE) throw new IllegalStateException("Cannot view coin storage when not in MAINTENANCE state");
        //^ Would only be satisfied when owner/admin is not in maintenance mode.
        return this.coinStorage.getCoinCounts();
    }
    /**
     * Withdraws a coin from the vending machine.
     * <p>
     * Used for returning unaccepted coins when coin stock is too full or is unsupported by the vending machine instance.
     * Used for admin/owner withdrawing coins or customer refunding coins - can be called from both proxies.
     * @param coin The coin to be withdrawn.
     * @throws IllegalStateException if the vending machine is not in REFUNDING or MAINTENANCE state.
     */
    public void withdrawCoin(CoinGBP coin) {
        //* Implementation for returning unaccepted coins when coin stock is too full or is unsupported by the vending
        //* machine instance.
        if (state != VendingMachineState.REFUNDING && state != VendingMachineState.MAINTENANCE) {
            //^ REFUNDING for customer refunding coins or getting change; MAINTENANCE for admin/owner withdrawing coins for revenue (and not letting the capacity fill up).
            throw new IllegalStateException("Cannot withdraw coin when not in REFUNDING or MAINTENANCE state");
        }
        this.coinStorage.withdraw(coin);
    }
    /**
     * Inserts a coin into the vending machine.
     * <p>
     * Used for customer paying or owner/admin restocking coins.
     * If for customer, balance updated in customer proxy class.
     * @param coin The coin to be inserted.
     * @throws IllegalStateException if the vending machine is not in PAYING or MAINTENANCE state.
     */
    public void insertCoin(CoinGBP coin) {
        //* Implementation for inserting a coin into the vending machine.
        //* Used for customer paying or owner/admin restocking coins.
        //* If for customer, balance updated in customer proxy class.
        if (this.state != VendingMachineState.PAYING && this.state != VendingMachineState.MAINTENANCE) {
            //* Coin can only be inserted when in PAYING (customer) or MAINTENANCE (admin/owner) state.
            this.withdrawCoin(coin);
            throw new IllegalStateException("Only insert coin when in PAYING or MAINTENANCE state");
            //^ Message intended for customer
        }
        this.coinStorage.deposit(coin);
    }

    /**
     * Gets the current item storage of the vending machine.
     * <p>
     * Called by both admin/owner and customer proxies to view item storage.
     * @return Array of ItemSlot objects representing the current status of each item slot in the vending machine.
     * @throws IllegalStateException if the vending machine is not in MAINTENANCE or ORDERING state.
     */
    public ItemSlot[] getItemStorage() {
        if (this.state != VendingMachineState.MAINTENANCE && this.state != VendingMachineState.ORDERING) {
            //* Would only be satisfied when owner/admin is not in maintenance mode.
            throw new IllegalStateException("Cannot view item storage when not in MAINTENANCE or ORDERING state");
        }
        return this.itemStorage.render();
    }
    /**
     * Stocks an item in the vending machine by item ID.
     * <p>
     * Used for restocking items by admin/owner only.
     * @param slotNum The ID of the item slot to be restocked.
     * @throws IllegalStateException if the vending machine is not in MAINTENANCE state.
     */
    public void stockItem(int slotNum) {
        //* Implementation for restocking item by item ID.
        //* Used for admin/owner restocking items only.
        if (state != VendingMachineState.MAINTENANCE) throw new IllegalStateException("Cannot restock item when not in MAINTENANCE state");
        //^ Would only be satisfied when owner/admin is not in maintenance mode.

        this.itemStorage.restockItem(slotNum);
    }
    /**
     * Dispenses an item from the vending machine by item ID.
     * <p>
     * Used for dispensing items to customers or admin/owner taking out expired items.
     * @param iD The ID of the item to be dispensed.
     * @throws IllegalStateException if the vending machine is not in DISPENSING or MAINTENANCE state.
     */
    public void dispenseItem(int iD) {
        //* Implementation for getting item by item ID.
        //* Used for dispensing item to customer or admin/owner taking out expired items.
        if (state != VendingMachineState.DISPENSING && this.state != VendingMachineState.MAINTENANCE) {
            //* Would only be satisfied when owner/admin is not in maintenance mode.
            throw new IllegalStateException("Cannot dispense item when not in DISPENSING or MAINTENANCE state");
            //^ Customer message.
        }
        this.itemStorage.dispenseItem(iD, this.state == VendingMachineState.MAINTENANCE);
    }
    /**
     * Assigns an item slot to a specific item in the vending machine.
     * <p>
     * Used for admin/owner assigning item slots - called in admin proxy class.
     * @param slotNum The slot number to assign the item to.
     * @param item    The item to be assigned to the slot.
     * @throws IllegalStateException if the vending machine is not in MAINTENANCE state.
     */
    public void assignSlot(int slotNum, Item item){
        //* Implementation for assigning an item slot to an item.
        //* Used for admin/owner assigning item slots.
        if (state != VendingMachineState.MAINTENANCE) throw new IllegalStateException("Cannot assign item slot when not in MAINTENANCE state");
        //^ Would only be satisfied when owner/admin is not in maintenance mode.

        this.itemStorage.assignSlot(slotNum, item);
    }
    /**
     * Unassigns an item slot from the vending machine.
     * <p>
     * Used for admin/owner unassigning item slots - called in admin proxy class.
     * @param slotNum The slot number to unassign.
     * @throws IllegalStateException if the vending machine is not in MAINTENANCE state.
     */
    public void unassignSlot(int slotNum){
        //* Implementation for unassigning an item slot to an item.
        //* Used for admin/owner unassigning item slots.
        if (state != VendingMachineState.MAINTENANCE) throw new IllegalStateException("Cannot unassign item slot when not in MAINTENANCE state");
        //^ Would only be satisfied when owner/admin is not in maintenance mode.

        this.itemStorage.unassignSlot(slotNum);
    }
}
