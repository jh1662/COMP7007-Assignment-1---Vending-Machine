import java.util.Map;
import java.util.Observable;
//^ Using package simplifies code as does not require to implement an observer interface and observable superclass methods from scratch.

/**
 * Represents the admin user role for maintaining its corresponding vending machine.
 * <p>
 * "proxy design pattern" class implementing administrative role actions for managing a vending machine.
 * Every permittable (admin) action are defined in the in this proxy and anything else is forbidden.
 * <p>
 * Also using the "observer design pattern" to serve as an intermediary between the vending machine and any administrative user interface (e.g. 'AdminDisplay').
 * 'java.util.Observable' superclass is extended/inherited to provide observable functionality (e.g. managing observers, notifying observers of events) instead of creating observable fields and methods from scratch ('setChanged','notifyObservers','addObserver','Vector&lt;Observer&gt;', etc.).
 * <p>
 * Observers are extendable by simply declaring 'this.observable.addObserver(this);' in the observer's constructor where 'observable' is the 'AdminProxy' instance being observed.
 * This extension allows for multiple different admin user interfaces to be created and observe the same 'AdminProxy' instance if needed.
 * Such example can be a remote admin CLI or a GUI application, both observing the same 'AdminProxy' instance for the same vending machine.
 * <p>
 * Actions are implemented following the 'ActionsAdmin' contract; hence refer to that interface for deeper method documentations.
 */
public class AdminProxy extends Observable implements ActionsAdmin {
    private final VendingMachine vendingMachine;

    /**
     * Observable-specific helper method to notify all observers of an event.
     * @param obj The event object to be sent to all observers to be display.
     *            Uses polymorphism by passing different types from Maps to thrown exceptions.
     *            Polymorphism is handled by the observer's 'update' method by using 'getClass().getSimpleName()' checks in an enhanced switch.
     */
    private void event(Object obj){
        this.setChanged();
        //^ from the 'Observable' superclass - marks this Observable object as having been changed.
        this.notifyObservers(obj);
        //^ from the 'Observable' superclass - if this object has changed, which it has in this scope as indicated by
        //^ the 'setChanged' method, then notify all of its observers.
    }

    /**
     * Constructor initializes which vending machine to be managed.
     * @param vendingMachine The vending machine instance reference that this admin proxy will manage using maintenance actions.
     */
    public AdminProxy(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    /**
     * Utility helper method to check if the vending machine is in maintenance mode.
     * <p>
     * Notifies observers (such as 'AdminDisplay') if the vending machine is in maintenance mode or not.
     * <p>
     * Used by all admin actions except 'getState', 'getCoins', 'startMaintenance' and 'stopMaintenance' to prevent invalid actions from being performed when not in maintenance mode.
     * <p>
     * Is critical to prevent invalid actions from causing unexpected behaviour in the vending machine - such as withdrawing coins while a customer makes an order.
     * <p>
     * Purposely called by "!this.inMaintenanceMode" instead of "this.notInMaintenanceMode" for better readability.
     * @return true if the vending machine is in maintenance mode, false otherwise.
     */
    private boolean inMaintenanceMode() {
        //* Better readability for method to be called 'inMaintenanceMode', with the logical NOT operator, than 'inNotMaintenanceMode'.
        boolean inMaintenanceMode = this.vendingMachine.getState() == VendingMachineState.MAINTENANCE;
        if (!inMaintenanceMode) {
            this.event(new IllegalStateException("Vending machine is not in maintenance mode. Cannot perform admin actions (except entering and exiting maintenance mode)."));
            return false;
        }
        return true;
    }

    /**
     * Forwarder method calls 'this.vendingMachine.getState'.
     * Views the current state of the vending machine.
     * <p>
     * This is only called by the observers, hence not an implemented action in the 'ActionsAdmin' interface.
     * @return The current state of the vending machine as a VendingMachineState enum value.
     */
    public VendingMachineState getState() { return this.vendingMachine.getState(); }

    //: Most important actions - actually starting/stopping maintenance mode.
    /**
     * Caller method that starts maintenance mode on the vending machine.
     * <p>
     * Changes the vending machine's state (VendingMachineState enum) to 'MAINTENANCE'.
     * Allowing admin/owner to perform maintenance tasks.
     * <p>
     * See corresponding superclass's method documentation for more information.
     */
    @Override
    public void startMaintenance() {
        this.vendingMachine.changeState(VendingMachineState.MAINTENANCE);
        this.event("Vending machine is now in maintenance mode.");
    }
    /**
     * Caller method that ends maintenance mode on the vending machine.
     * <p>
     * Changes the vending machine's state (VendingMachineState enum) to 'IDLE'.
     * Allowing customer to use the vending machine.
     * <p>
     * See corresponding superclass's method documentation for more information.
     */
    @Override
    public void stopMaintenance() {
        this.vendingMachine.changeState(VendingMachineState.IDLE);
        this.event("Vending machine is now out of maintenance mode.");
    }

    //: Relating to coin storage.
    /**
     * Deposits a specified amount of coins of a specific type into the vending machine's coin storage.
     * <p>
     * One of the actions of the coin storage part of the administrative vending machine maintenance tasks.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Catches thrown IllegalArgumentException from 'this.vendingMachine.insertCoin' calls to notifies observers (such as 'AdminDisplay') of the error instead of throwing it further.
     * @param coin   The type of coin to be deposited.
     * @param amount The amount of coins to be deposited.
     */
    @Override
    public void depositCoins(CoinGBP coin, int amount) {
        if (!this.inMaintenanceMode()) return;

        this.event(STR."Depositing \{amount} \{coin.toString()}(s)..");
        try {
            for (int i = 0; i < amount; i++) {
                this.vendingMachine.insertCoin(coin);
                this.event(coin);
            }
        }
        catch (IllegalArgumentException e) {
            this.event(e);
            return;
            //^ Reduces indentation levels removing the 'finally' block.
        }
        this.event("All coins deposited successfully.");
    }
    /**
     * Withdraws a specified amount of coins of a specific type from the vending machine's coin storage.
     * <p>
     * One of the actions of the coin storage part of the administrative vending machine maintenance tasks.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Catches thrown IllegalArgumentException from 'this.vendingMachine.withdrawCoin' calls to notifies observers (such as 'AdminDisplay') of the error instead of throwing it further.
     * @param coin   The type of coin to be withdrawn.
     * @param amount The amount of coins to be withdrawn.
     */
    @Override
    public void withdrawCoins(CoinGBP coin, int amount) {
        if (!this.inMaintenanceMode()) return;

        this.event(STR."Withdrawing \{amount} \{coin.toString()}(s)..");
        try {
            for (int i = 0; i < amount; i++) {
                this.vendingMachine.withdrawCoin(coin);
                this.event(coin);
            }
        }
        catch (IllegalArgumentException e) {
            this.event(e);
            return;
            //^ Reduces indentation levels removing the 'finally' block.
        }
        this.event("All coins withdraw successfully.");
    }
    /**
     * Forwarder method views all supported coin types in the vending machine's coin storage.
     * <p>
     * calls and returns 'this.vendingMachine.getCoinStorage'.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Not one of the administrative actions but is called per observer display update, hence the MAINTENANCE guard clause is not needed here.
     * @return Map containing each coin denomination (key) and its respective current count (value) in the coin storage.
     */
    @Override
    public Map<CoinGBP, Integer> viewCoins() { return this.vendingMachine.getCoinStorage(); }

    //: Relating to item storage.
    /**
     * Stocks a specified amount of items into a specific slot of the vending machine's item storage.
     * <p>
     * One of the actions of the item storage part of the administrative vending machine maintenance tasks.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Catches thrown IllegalArgumentException from 'this.vendingMachine.stockItem' calls to notifies observers (such as 'AdminDisplay') of the error instead of throwing it further.
     * @param slotNum The slot number to stock items into.
     * @param amount  The number of items to stock.
     */
    @Override
    public void stockItems(int slotNum, int amount) {
        this.event(STR."attempting to stock \{amount} items into slot \{slotNum}...");
        try {
            for (int i = 0; i < amount; i++) {
                this.vendingMachine.stockItem(slotNum);
                this.event("Item stocked into slot #" + slotNum);
            }
        }
        catch (IllegalArgumentException e) {
                this.event(e);
                return;
            }
        this.event("All items stocked successfully.");
    }
    /**
     * Removes a specified amount of items from a specific slot of the vending machine's item storage.
     * <p>
     * One of the actions of the item storage part of the administrative vending machine maintenance tasks.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Catches thrown IllegalArgumentException from 'this.vendingMachine.dispenseItem' calls to notifies observers (such as 'AdminDisplay') of the error instead of throwing it further.
     * @param slotNum The slot number to remove items from.
     * @param amount  The number of items to remove.
     */
    @Override
    public void removeItems(int slotNum, int amount) {
        this.event(STR."attempting to remove \{amount} items from slot #\{slotNum}...");
        try {
            for (int i = 0; i < amount; i++) {
                this.vendingMachine.dispenseItem(slotNum);
                this.event("Item removed from slot #" + slotNum);
            }
        }
        catch (IllegalArgumentException e) {
            this.event(e);
            return;
        }
        this.event(STR."All wanted items (in slot #\{slotNum}) removed successfully.");
    }
    /**
     * Assigns an item to a specified slot in the vending machine's item storage.
     * <p>
     * One of the actions of the item storage part of the administrative vending machine maintenance tasks.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Catches thrown IllegalArgumentException from 'this.vendingMachine.assignSlot' calls to notifies observers (such as 'AdminDisplay') of the error instead of throwing it further.
     * @param slotNum The slot number to assign the item to.
     * @param item    The item to be assigned to the slot.
     */
    @Override
    public void assignItemSlot(int slotNum, Item item) {
        this.event(STR."Attempting to assign item \{item.getName()} to slot #\{slotNum}...");
        try{ this.vendingMachine.assignSlot(slotNum, item); }
        catch (IllegalArgumentException | IllegalStateException e){
            this.event(e);
            return;
        }
        this.event(STR."Slot #\{slotNum} assigned to item \{item.getName()} successfully.");
    }
    /**
     * Unassigns a specified slot in the vending machine's item storage.
     * <p>
     * One of the actions of the item storage part of the administrative vending machine maintenance tasks.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Catches thrown IllegalArgumentException from 'this.vendingMachine.unassignSlot' calls to notifies observers (such as 'AdminDisplay') of the error instead of throwing it further.
     * @param slotNum The slot number to unassign the item from.
     */
    @Override
    public void unassignItemSlot(int slotNum) {
        this.event(STR."Attempting to unassign item from slot #\{slotNum}...");
        try{ this.vendingMachine.unassignSlot(slotNum); }
        catch (IllegalArgumentException | IllegalStateException e){
            this.event(e);
            return;
        }
        this.event(STR."Slot #\{slotNum} unassigned successfully.");
    }
    /**
     * Forwarder method views all item slots in the vending machine's item storage.
     * <p>
     * calls and returns 'this.vendingMachine.getItemStorage'.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Not one of the administrative actions but is called per observer display update, hence the MAINTENANCE guard clause is not needed here.
     * @return All slot representations in the vending machine's item storage; this includes the empty ones which are represented by 'null'.
     */
    @Override
    public ItemSlot[] viewItems() { return this.vendingMachine.getItemStorage(); }
}
