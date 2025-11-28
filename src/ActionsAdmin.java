import java.util.Map;

/**
 * Proxy interface defining administrative actions for managing a vending machine.
 * <p>
 * Any role with administrative privileges must implement this interface to perform maintenance tasks.
 * <p> Non-void methods are considered informational (getter) actions called by the observers meaning that they are only indirectly tied to the actions of the admin role.
 */
public interface ActionsAdmin {

    //: Relating to coin storage.
    /**
     * Deposing an amount of coins into the vending machine's coin storage.
     * <p>
     * Admin/owner would deposit each coin type in bulk (rather than one at a time) during maintenance.
     * Because of this, the 'amount' parameter is included to specify how many coins of the given denomination to deposit.
     * This is different to the customer inserting coins one at a time during payment.
     * <p>
     * One of the actions of the coin storage part of the administrative vending machine maintenance tasks.
     * @param coin   The denomination of the coin to be deposited.
     * @param amount The number of coins to deposit.
     */
    void depositCoins(CoinGBP coin, int amount);
    /**
     * Withdrawing an amount of coins from the vending machine's coin storage.
     * <p>
     * Admin/owner would withdraw each coin type in bulk (rather than one at a time) during maintenance.
     * Because of this, the 'amount' parameter is included to specify how many coins of the given denomination to withdraw.
     * This is different to the customer receiving refunded coins one at a time during refunding.
     * <p>
     * One of the actions of the coin storage part of the administrative vending machine maintenance tasks.
     * @param coin   The denomination of the coin to be withdrawn.
     * @param amount The number of coins to withdraw.
     */
    void withdrawCoins(CoinGBP coin, int amount);
    /**
     * Views all supported coin types in the vending machine's coin storage.
     * @return Map containing each coin denomination (key) and its respective current count (value) in the coin storage.
     */
    Map<CoinGBP, Integer> viewCoins();

    //: Relating to item storage.
    /**
     * Stocks an amount of items into a specified slot of the vending machine's item storage.
     * <p>
     * Admin/owner would stock items in bulk during maintenance.
     * Because of this, the 'amount' parameter is included to specify how many items to stock into the given slot.
     * <p>
     * Only successful if slot is assigned to an item and is not fully populated.
     * <p>
     * One of the actions of the item storage part of the administrative vending machine maintenance tasks.
     * @param slotNum The slot number to stock items into.
     * @param amount  The number of items to stock.
     */
    void stockItems(int slotNum, int amount);
    /**
     * Removes an amount of items from a specified slot of the vending machine's item storage.
     * <p>
     * Admin/owner would remove items in bulk during maintenance.
     * Because of this, the 'amount' parameter is included to specify how many items to remove from the given slot.
     * <p>
     * Different to the customer purchasing items because they would not want to buy items in bulk from a vending machine.
     * <p>
     * Only successful if slot is assigned to an item and is not empty (zero population).
     * <p>
     * One of the actions of the item storage part of the administrative vending machine maintenance tasks.
     * @param slotNum The slot number to remove items from.
     * @param amount  The number of items to remove.
     */
    void removeItems(int slotNum, int amount);
    /**
     * Assigns an item to a specified slot in the vending machine's item storage.
     * <p>
     * Unlike stocking and dispensing (maintenance) items, this is not done in bulk because at most a few slots would be assigned to the same popular item.
     * <p>
     * Only successful if slot is currently not assigned to an item.
     * <p>
     * One of the actions of the item storage part of the administrative vending machine maintenance tasks.
     * @param slotNum The slot number to assign the item to.
     * @param item    The item to be assigned to the slot.
     */
    void assignItemSlot(int slotNum, Item item);
    /**
     * Unassigns a specified slot in the vending machine's item storage.
     * <p>
     * Unlike stocking and dispensing (maintenance) items, this is not done in bulk because unassigning multiple slots at the same time would be a too complicated action for the admin/owner.
     * <p>
     * Only successful if slot is currently assigned to an item and is empty (zero population).
     * <p>
     * One of the actions of the item storage part of the administrative vending machine maintenance tasks.
     * @param slotNum The slot number to unassign the item from.
     */
    void unassignItemSlot(int slotNum);
    /**
     * Views all item slots in the vending machine's item storage.
     * @return All slot representations in the vending machine's item storage; this includes the empty ones which are represented by 'null'.
     */
    ItemSlot[] viewItems();

    //: Relating to vending machine itself.
    /**
     * Starts maintenance mode on the vending machine.
     * Without this, no administrative maintenance actions can be performed on the vending machine.
     * <p>
     * One of the main actions of the administrative vending machine maintenance tasks.
     * <p>
     * Vending machine must be in IDLE state to start maintenance.
     */
    void startMaintenance();
    /**
     * Stops maintenance mode on the vending machine.
     * After this, no administrative maintenance actions can be performed on the vending machine until maintenance mode is started again.
     * Ending maintenance mode will return the vending machine to IDLE state for customers to use.
     * <p>
     * One of the main actions of the administrative vending machine maintenance tasks.
     * <p>
     * Vending machine should (not must) be in MAINTENANCE state to stop maintenance to prevent unexpected behaviour but is still allowed in case of unexpected situations.
     * Such unexpected situation could be when the customer expecting coin refunds, as change/leftover money from a purchase, but the physical mechanisms are jammed (i.e. external errors).
     */
    void stopMaintenance();
}