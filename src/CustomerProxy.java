import java.util.*;

/**
 * Represents the customer role for interacting with the corresponding vending machine.
 * <p>
 * "proxy design pattern" class implementing customer role actions for managing a vending machine.
 * Every permittable (customer) action are defined in the in this proxy and anything else is forbidden.
 * <p>
 * Also using the "observer design pattern" to serve as an intermediary between the vending machine and any customer user interface (e.g. 'CustomerDisplay').
 * 'java.util.Observable' superclass is extended/inherited to provide observable functionality (e.g. managing observers, notifying observers of events) instead of creating observable fields and methods from scratch ('setChanged','notifyObservers','addObserver','Vector&lt;Observer&gt;', etc.).
 * <p>
 * Observers are extendable by simply declaring 'this.observable.addObserver(this);' in the observer's constructor where 'observable' is the 'CustomerProxy' instance being observed.
 * <p>
 * Actions are implemented following the 'CustomerActions' contract; hence refer to that interface for deeper method documentations.
 */
public class CustomerProxy extends Observable implements ActionsCustomer {
    private final VendingMachine vendingMachine;
    private double payDue;
    //^ Total amount the customer needs to pay for selected items in their basket.
    //^ Unlike basket's item, each coin physically goes in one at a time instead of being "on paper" until transaction was complete.
    private double balance;
    //^ 'payDue' is used for calculating change/leftover coins, after payment, but balance to used know how much to refund when customer cancels order for whatever reason.
    private final Map<Item, Integer> basket;
    //^ Keeps track of items, selected by the customer, and their quantities.
    //^ Handled in customer proxy to separate customer actions from vending machine operations.
    private final CoinGBP[] acceptedCoinTypes;
    //^ Accepted coin types by the vending machine for payment, sorted in descending order of value for quicker change dispensing for both program and customer.
    //^ Is 'final' as accepted coin types cannot be physically changed inside vending machine once made.
    //^ As it is constant, it stored here to avoid repeated calling to 'this.vendingMachine' every time change is dispensed.
    //^! Despite being used for telling  the vending machine what coins to withdraw, it does not verify deposited coins as that is the vending machine's coin storage responsibility; it is so as it is the hardware that checks the coin as it is physically inserted.
    //^! On top of this, it also enforces SRP (not violating it).
    private boolean showBasket;
    //^ Helper field to inform 'CustomerDisplay' whether the current item list to display is the basket or the vending machine stock.

    /**
     * Constructor for CustomerProxy which initializes the proxy with a specific vending machine.
     * @param vendingMachine The vending machine instance to be managed by this customer proxy.
     */
    public CustomerProxy(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
        this.payDue = 0.0;
        this.basket = new HashMap<>();
        this.showBasket = true;

        //: Takes up more code lines but purposely made more readable for future maintainability.
        this.acceptedCoinTypes = this.vendingMachine.getCoinMaxes().keySet()
            //^ Get all supported coin types.
            .stream().sorted(Comparator.comparingDouble(CoinGBP::getValue).reversed()).toArray(CoinGBP[]::new);
            //^ Stream statement sort in descending order ('.reversed()') of value ('getValue') by comparing double values ('Comparator.comparingDouble').
    }

    /**
     * Observable-specific helper method to notify all observers of an event.
     * @param obj The event object to be sent to observers such as 'CustomerDisplay'.
     */
    private void event(Object obj){
        this.setChanged();
        //^ from the 'Observable' superclass - marks this Observable object as having been changed.
        this.notifyObservers(obj);
        //^ from the 'Observable' superclass - if this object has changed, which it has in this scope as indicated by
        //^ the 'setChanged' method, then notify all of its observers.
    }

    /**
     * Utility helper method helps prevent customer actions when vending machine is in maintenance mode.
     * @return true if vending machine is in maintenance mode; false otherwise.
     */
    private boolean inMaintenance(){
        boolean inMaintenance = this.vendingMachine.getState() == VendingMachineState.MAINTENANCE;
        if (inMaintenance){
            this.event(new IllegalStateException("This vending machine is currently under maintenance - customer actions are disabled. Please come back when maintainer has finished."));
            return true;
        }
        return false;
    }

    /**
     * Utility helper method to process coin withdrawal for change/refund.
     * @param dueAmount The total amount to be withdrawn/refunded.
     * @throws IllegalArgumentException If the vending machine cannot dispense the exact amount requested due to insufficient coin types/quantities.
     */
    private void processCoinWithdrawal(double dueAmount){
        //* No update message every time a coin is refunded or withdrawn change dispensing to avoid spamming customer display.
        //* This is unlike owner/admin coin deposit/withdraw where each operation of an action is notified.
        this.vendingMachine.changeState(VendingMachineState.REFUNDING);
        //^ Change state to REFUNDING to allow coin withdrawal/refunding.
        //^ It is very appropriate to change state here as this method is private - cannot be called by customer directly and thus not called carelessly/abusively (very secure).
        String displayRefundedAmount = String.format("%.2f", dueAmount);
        //^ Allows correct refunded amount to be displayed after withdrawal process (as 'dueAmount' is mutated in the process).
        this.event(STR."Processing money withdrawal of £\{displayRefundedAmount} for dear customer...");
        boolean allCoinTypesExhausted;
        while (dueAmount > 0.0){
            allCoinTypesExhausted = true;
            for (CoinGBP coin : acceptedCoinTypes){
                //^ Assumes 'acceptedCoinTypes' is sorted in descending order of value.
                if (coin.getValue() <= dueAmount){
                    try {
                        this.vendingMachine.withdrawCoin(coin);
                        this.event(STR."Dispensed \{coin.toString()} as change/refund.");
                    }
                    catch (IllegalArgumentException e){
                        //* IllegalStateException will never be thrown here as coin types are from acceptedCoinTypes.
                        continue;
                    }
                    dueAmount -= coin.getValue();
                    dueAmount = Math.round(dueAmount * 100.0) / 100.0;
                    //^ Important logic error avoidance - avoids floating point precision issues.
                    allCoinTypesExhausted = false;
                    break;
                }
            }
            if (allCoinTypesExhausted){ throw new IllegalArgumentException(STR."Oops, unexpected problem has been uncounted. Remaining change - \{dueAmount}GBP. Please call the nearest maintainance technician and show him/her this message."); }
            //^ When refunding (not giving payment change), this should never be executed (as order will be canceled before when change is needed but lacking) but external errors may cause unexpected behaviour.
            else { this.event(STR."£\{displayRefundedAmount} withdrawn sucessfully."); }
        }
    }

    /**
     * Helper method to calculate the total amount due for the items in the basket.
     * Updates the 'payDue' field with the calculated total.
     */
    private void fillPayDue(){
        for (Map.Entry<Item, Integer> entry : this.basket.entrySet()){
            this.payDue += entry.getKey().getPrice() * entry.getValue();
        }
    }

    /**
     * Utility helper method to reset the customer proxy state for a new order.
     * Clears the basket, resets payment due and balance, and reverts the vending machine state to IDLE.
     */
    private void fullReset(){
        //* When same customer wants to start a new order after completing/cancelling for starting another order.
        this.payDue = 0.0;
        this.balance = 0.0;
        this.basket.clear();
        this.vendingMachine.changeState(VendingMachineState.IDLE);
    }



    //: Not actions but are called by listeners to modify display behaviour.
    /**
     * Getter method for 'showBasket' field.
     * @return true if the current item list to display is the basket; false if it is the vending machine stock.
     */
    public boolean getShowBasket() {
        return showBasket;
    }
    /**
     * Resets the 'showBasket' field to false after the basket has been displayed.
     * Much simpler alternative than reading then mutating to boolean argument.
     */
    public void showBasketDefault() {
        this.showBasket = false;
    }

    /**
     * Starts a new order if the vending machine is in IDLE state.
     * <p>
     * One of the possible customer actions defined in the 'CustomerActions' interface.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Changes vending machine state to ORDERING upon successful start of order.
     */
    @Override
    public void startOrder() {
        if (this.inMaintenance()){ return; }

        if (this.vendingMachine.getState() == VendingMachineState.IDLE){
            this.vendingMachine.changeState(VendingMachineState.ORDERING);
            this.event("Order started. Please select items to add to your basket.");
            return;
        }
        this.event(new IllegalStateException("Cannot start a new order while another order is in progress. Please complete or cancel the ongoing order first."));
    }

    /**
     * Selects an item to add to the current order if the vending machine is in ORDERING state.
     * <p>
     * One of the possible customer actions defined in the 'CustomerActions' interface.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @param itemID The unique identifier of the item to select.
     */
    @Override
    public void selectItem(int itemID) {
        if (this.inMaintenance()){ return; }

        if (this.vendingMachine.getState() != VendingMachineState.ORDERING){
            this.event(new IllegalStateException("Cannot select items unless an order is in progress. Please start a new order first."));
            return;
        }

        int itemAvailableCount = 0;
        Item selectedItem = null;
        //^ Assigned to null to check if vending machine offer item but not enough stock or item is just not offered.
        Item basketItem = null;
        boolean itemFound = false;

        for (ItemSlot slot : this.vendingMachine.getItemStorage()) {
            if (slot == null){ continue; }
            //^ Skip unassigned slots.
            if (slot.checkID(itemID)){
                itemFound = true;
                itemAvailableCount += slot.getStock();
                //^ Does no break here as multiple slots may contain the same item.
                selectedItem = slot.getItem();
                //^ Avoids another for-loop when basket does not have item - reduces worst case time complexity from O(n^2) to O(n).
            }
        }
        if (!itemFound){
            this.event(STR."There is currently no item assaigned to ID \{itemID}. Please use a different ID instead.");
            return;
        }

        for (Item item : this.basket.keySet()){
            if (item.checkID(itemID)){
                basketItem = item;
                break;
            }
        }
        if (basketItem == null){
            this.basket.put(selectedItem, 0);
            basketItem = selectedItem;
        }

        if (this.basket.getOrDefault(basketItem, 0) + 1 > itemAvailableCount) {
            this.event(STR."There is currently not enough \{basketItem.getName()} stock in the vending machine. Please select another item instead.");
            //^ If basket already has all available items of that ID, or item is just out of stock, cannot select more.
        }
        this.basket.put(basketItem, (this.basket.getOrDefault(basketItem, 0) + 1));
        this.event(STR."One \{basketItem.getName()} has been added to your basket. Item details: \{basketItem.render()}");
    }

    /**
     * Deselects an item to remove from the current order if the vending machine is in ORDERING state.
     * <p>
     * One of the possible customer actions defined in the 'CustomerActions' interface.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * @param itemID The unique identifier of the item to deselect.
     */
    @Override
    public void deselectItem(int itemID) {
        if (this.inMaintenance()){ return; }

        //* Assumes the vending machine's interface is advanced enough to allow deselecting items instead of cancelling and redoing the entire order.

        Item basketItem = null;
        for (Item item : this.basket.keySet()){
            if (item.checkID(itemID)){
                basketItem = item;
                break;
            }
        }

        if (basketItem == null) {
            this.event(STR."There is no item with ID \{itemID} currently in the basket. Please select another item ID instead.");
            return;
        }
        if (this.basket.get(basketItem) == 1){
            //* Remove item from basket if only one left to prevent wasted space.
            this.event(STR."All \{basketItem.getName()} has been removed from your basket.");
            this.basket.remove(basketItem);
            return;
        }
        this.basket.put(basketItem, this.basket.get(basketItem) - 1);
        this.event(STR."One \{basketItem.getName()} has been removed from your basket.");
    }

    /**
     * Proceeds to checkout if the vending machine is in ORDERING state and the basket is not empty.
     * <p>
     * One of the possible customer actions defined in the 'CustomerActions' interface.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Changes vending machine state to PAYING upon successful checkout.
     */
    @Override
    public void checkout() {
        if (this.inMaintenance()){ return; }

        if (this.basket.isEmpty()){
            //^ 'this.payDue == 0.0' also works.
            //^ If basket is not empty, this means that vending machine is in ORDERING state.
            this.event(STR."Cannot checkout with an empty basket dear customer. Please select items first.");
            return;
        }
        this.fillPayDue();
        this.vendingMachine.changeState(VendingMachineState.PAYING);
        this.event(STR."Items in basket total to £\{String.format("%.2f", this.payDue)}. Please deposit coins to proceed with payment.");
    }

    /**
     * Cancels the current order if the vending machine is in ORDERING or PAYING state.
     * <p>
     * One of the possible customer actions defined in the 'CustomerActions' interface.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Changes vending machine state to IDLE upon successful cancellation. However, if external error (like stuck coin) prevents refunding is caught, changes state to MAINTENANCE instead to allow admin/owner to fix issue and prevents any other customer action until issue is fixed.
     */
    @Override
    public void cancelOrder() {
        if (this.inMaintenance()){ return; }

        switch (this.vendingMachine.getState()){
            case IDLE -> this.event(STR."No ongoing order to cancel dear customer.");

            case ORDERING -> {
                this.fullReset();
                //^ Customer has not deposited any coins yet, so no need to refund anything beforehand.
                this.event(STR."Order cancelled, basket cleared. sorry to see you go!");
            }

            case PAYING -> {
                this.event(STR."Order cancelled during payment. Sorry to see you go. Refunding processing...");
                //^ If vending machine is slow, see this message; otherwise the customer do not need time to read the redundant message.
                try { if (balance > 0) { this.processCoinWithdrawal(this.balance); } }

                catch (IllegalArgumentException e){
                    //* For whatever external/physical reason the vending machine cannot refund the customer.

                    //: Change state to IDLE first before MAINTENANCE to allow admin/owner to fix issue.
                    this.vendingMachine.changeState(VendingMachineState.IDLE);
                    this.vendingMachine.changeState(VendingMachineState.MAINTENANCE);
                    //^ Change to MAINTENANCE mode to prevent further customer actions until issue is resolved.

                    this.event(e);
                    return;
                }
                this.event(STR."Order cancelled during payment. Balance (£\{String.format("%.2f", this.balance)}) refunded. Sorry to see you go!");
                this.fullReset();
                //^ Reset done after displaying message to show the correct balance refunded.
            }
        }
    }

    /**
     * Deposits a coin for payment if the vending machine is in PAYING state.
     * <p>
     * One of the possible customer actions defined in the 'CustomerActions' interface.
     * <p>
     * See corresponding superclass's method documentation for more information.
     * <p>
     * Catches exceptions from invalid coin deposit and notifies customer accordingly.
     * <p>
     * @param coin The coin to be deposited for payment.
     */
    @Override
    public void depositCoin(CoinGBP coin) {
        if (this.inMaintenance()){ return; }

        if (this.vendingMachine.getState() != VendingMachineState.PAYING){
            this.event(new IllegalStateException("Cannot deposit coins unless in the payment phase of the order. Please checkout an order first."));
            return;
        }

        try {
            this.vendingMachine.insertCoin(coin);
        }
        catch (IllegalArgumentException | IllegalStateException e){
            this.event(e);
            return;
        }
        this.payDue -= coin.getValue();
        this.balance += coin.getValue();
        this.event(STR."Deposited \{coin.toString()}. Remaining price to pay: £\{String.format("%.2f", this.payDue)}");
        //^ If 'this.payDue' is negative, it means customer overpaid and is owed change.
        //^ If 'this.payDue' is zero, it means customer paid exact amount.
        //^ If 'this.payDue' is not positive, it means customer does not need to pay more; this means weather the customer sees this message or not does not matter.
        //^ Deliberate made to show negative 'this.payDue' to inform customer of overpayment - shows money to refund.
        if (this.payDue <= 0) {
            this.event("Dispensing all selected items for dear customer...");
            this.vendingMachine.changeState(VendingMachineState.DISPENSING);
            //^ Change state to DISPENSING to allow dispensing items.
            for (Item item : this.basket.keySet()){
                int quantity = this.basket.get(item);
                for (int i = 0; i < quantity; i++){
                    this.vendingMachine.dispenseItem(item.getID());
                    this.event(STR."Dispensed one \{item.getName()}.");
                }
            }
            this.event("All selected items dispensed successfully. Thank you for your purchase!");
            if (this.payDue != 0 ) {
                this.processCoinWithdrawal(this.balance);
                //^ Since 'this.balance' exists, might as well use it instead of 'this.payDue*-1' (negated).
            }
            this.fullReset();
        }
    }

    /**
     * Gets the current contents of the customer's basket if not empty.
     * <p>
     * One of the possible customer actions defined in the 'CustomerActions' interface.
     * <p>
     * Satisfaction of empty basket includes not being in ORDERING or PAYING state as basket is only filled in those states.
     * Therefore, direct state check is not needed.
     * <p>
     * See corresponding superclass's method documentation for more information.
     */
    @Override
    public void GetBasket() {
        if (this.inMaintenance()){ return; }

        if (this.basket.isEmpty()){
            //* Satisfaction condition includes not being in ORDERING or PAYING state as basket is only filled in those states.
            this.event(STR."Your basket is currently empty. Please select items to add to your basket.");
            return;
        }

        this.showBasket = true;
        this.event(this.basket);
    }

    /**
     * Gets the current stock of all items offered by the vending machine if in ORDERING state.
     * <p>
     * One of the possible customer actions defined in the 'CustomerActions' interface.
     * <p>
     * See corresponding superclass's method documentation for more information.
     */
    @Override
    public void GetItemStock() {
        if (this.inMaintenance()){ return; }

        if (this.vendingMachine.getState() != VendingMachineState.ORDERING){
            //* Does not cause unexpected behaviour if there is no state check; However, there is absolutely no reason
            //* for customer to view item stock unless they are choosing items to select for order.
            this.event(new IllegalStateException("Sorry dear customer, but you cannot view item stock unless in the selecting-items phase."));
            return;
        }

        ItemSlot[] itemStorage = this.vendingMachine.getItemStorage();
        Map<Item, Integer> itemStock = new HashMap<>();

        for (ItemSlot slot : itemStorage) {
            //* Includes items with zero stock.
            if (slot == null){ continue; }
            //^ Skip unassigned slots.
            Item item = slot.getItem();
            itemStock.put(item, itemStock.getOrDefault(item, 0) + slot.getStock());
            //^ Accumulates stock from multiple slots containing the same item.
        }
        this.event(itemStock);
    }
}
