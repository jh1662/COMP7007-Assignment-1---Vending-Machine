/**
 * Interface defining customer actions for a vending machine system.
 * This includes methods for starting orders, selecting items, checking out, depositing coins, etc.
 * <p>
 * Customer proxies must implement this interface to perform a standardised set of tasks.
 * <p>
 * Please notice that implementation of every method in this interface has the guard clause 'if (this.inMaintenance()){ return; }' to ensure that customer does not perform any action during MAINTENANCE state.
 * To prevent duplicate comments, this is only mentioned here!
 */
public interface ActionsCustomer {

    //: Payment and ordering actions
    /**
     * Starts a new order session for the customer.
     * Without this, customer cannot select items to order.
     * <p>
     * Customer can only start a new order when the vending machine is in IDLE state.
     * Changes the vending machine state to ORDERING.
     * <p>
     * One of the possible actions for customers when interacting with the vending machine.
     */
    void startOrder();
    /**
     * Selects an item to add to the current order.
     * <p>
     * Customer can only select items when the vending machine is in ORDERING state.
     * <p>
     * One of the possible actions for customers when interacting with the vending machine.
     * @param itemID The unique identifier of the item to select.
     */
    void selectItem(int itemID);

    void deselectItem(int itemID);
    void checkout();
    void cancelOrder();
    void depositCoin(CoinGBP coin);

    //: Getters
    void GetBasket();
    void GetItemStock();
    //^ Views all offered items and their corresponding stock.
}
