/// //^ Used for refunding when order is cancelled ('this.balance' is what is used for calculating and giving refund).

import java.util.*;

public class CustomerProxy extends Observable implements ActionsCustomer {
    private final VendingMachine vendingMachine;
    private double payDue;
    //^ Total amount the customer needs to pay for selected items in their basket.
    //^ Unlike basket's item, each coin physically goes in one at a time instead of being "on paper" until transaction was complete.
    private double balance;
    //^ 'payDue' is used for calculating change/leftover coins, after payment, but balance to used know how much to refund when customer cancels order for whatever reason.
    Map<Item, Integer> basket;
    //^ Keeps track of items, selected by the customer, and their quantities.
    //^ Handled in customer proxy to separate customer actions from vending machine operations.
    private final CoinGBP[] acceptedCoinTypes;
    //^ Accepted coin types by the vending machine for payment, sorted in descending order of value for quicker change dispensing for both program and customer.
    //^ Is 'final' as accepted coin types cannot be physically changed inside vending machine once made.
    //^ As it is constant, it stored here to avoid repeated calling to 'this.vendingMachine' every time change is dispensed.
    //^! Despite being used for telling  the vending machine what coins to withdraw, it does not verify deposited coins as that is the vending machine's coin storage responsibility; it is so as it is the hardware that checks the coin as it is physically inserted.
    //^! On top of this, it also enforces SRP (not violating it).

    public CustomerProxy(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
        this.payDue = 0.0;
        this.basket = new HashMap<>();

        //: Takes up more code lines but purposely made more readable for future maintainability.
        this.acceptedCoinTypes = this.vendingMachine.getCoinMaxes().keySet()
            //^ Get all supported coin types.
            .stream().sorted(Comparator.comparingDouble(CoinGBP::getValue).reversed()).toArray(CoinGBP[]::new);
            //^ Stream statement sort in descending order ('.reversed()') of value ('getValue') by comparing double values ('Comparator.comparingDouble').
    }

    private void event(Object obj){
        this.setChanged();
        //^ from the 'Observable' superclass - marks this Observable object as having been changed.
        this.notifyObservers(obj);
        //^ from the 'Observable' superclass - if this object has changed, which it has in this scope as indicated by
        //^ the 'setChanged' method, then notify all of its observers.
    }

    private boolean inMaintenance(){
        boolean inMaintenance = this.vendingMachine.getState() == VendingMachineState.MAINTENANCE;
        if (inMaintenance){
            this.event(new IllegalStateException("This vending machine is currently under maintenance - customer actions are disabled. Please come back when maintainer has finished."));
            return true;
        }
        return false;
    }

    private void processCoinWithdrawal(double dueAmount){
        //* No update message every time a coin is refunded or withdrawn change dispensing to avoid spamming customer display.
        //* This is unlike owner/admin coin deposit/withdraw where each operation of an action is notified.
        boolean allCoinTypesExhausted = false;
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
        }
    }

    private void fillPayDue(){
        for (Map.Entry<Item, Integer> entry : this.basket.entrySet()){
            this.payDue += entry.getKey().getPrice() * entry.getValue();
        }
    }

    private void fullReset(){
        //* When same customer wants to start a new order after completing/cancelling for starting another order.
        this.payDue = 0.0;
        this.balance = 0.0;
        this.basket.clear();
        this.vendingMachine.changeState(VendingMachineState.IDLE);
    }

    public void startOrder() {
        if (this.inMaintenance()){ return; }

        if (this.vendingMachine.getState() == VendingMachineState.IDLE){
            this.vendingMachine.changeState(VendingMachineState.ORDERING);
            this.event(STR."Order started. Please select items to add to your basket.");
            return;
        }
        this.event(new IllegalStateException("Cannot start a new order while another order is in progress. Please complete or cancel the ongoing order first."));
    }

    @Override
    public void selectItem(int itemID) {
        if (this.inMaintenance()){ return; }

        int itemAvailableCount = 0;
        Item selectedItem = null;
        //^ Assigned to null to check if vending machine offer item but not enough stock or item is just not offered.

        for (ItemSlot slot : this.vendingMachine.getItemStorage()) {
            if (slot == null){ continue; }
            //^ Skip unassigned slots.
            if (slot.checkID(itemID)){
                selectedItem = slot.getItem();
                //^ Avoids another for-loop when basket does not have item - reduces worst case time complexity from O(n^2) to O(n).
                itemAvailableCount++;
                //^ Does no break here as multiple slots may contain the same item.
            }
        }
        if (selectedItem == null){
            this.event(STR."There is currently no item assaigned to ID \{itemID}. Please use a different ID instead.");
            return;
        }
        if (this.basket.getOrDefault(itemID, 0) + 1 > itemAvailableCount) {
            this.event(STR."There is currently not enough \{selectedItem.render().get("name")} stock in the vending machine. Please select another item instead.");
            //^ If basket already has all available items of that ID, or item is just out of stock, cannot select more.
        }
        this.basket.put(selectedItem, (this.basket.getOrDefault(itemID, 0) + 1));
        this.event(STR."One \{selectedItem.render().get("name")} has been added to your basket.");
    }

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
            this.event(STR."All \{basketItem.render().get("name")} has been removed from your basket.");
            this.basket.remove(basketItem);
            return;
        }
        this.basket.put(basketItem, this.basket.get(basketItem) - 1);
        this.event(STR."One \{basketItem.render().get("name")} has been removed from your basket.");
    }

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

                this.fullReset();
                this.event(STR."Order cancelled during payment. Balance (£\{String.format("%.2f", this.balance)}) refunded. Sorry to see you go!");
            }
        }
    }

    @Override
    public void depositCoin(CoinGBP coin) {
        if (this.inMaintenance()){ return; }

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
        if (this.payDue < 0) {
            this.event("Dispensing all selected items...");
            this.vendingMachine.changeState(VendingMachineState.DISPENSING);
            //^ Change state to DISPENSING to allow dispensing items.
            for (Item item : this.basket.keySet()){
                int quantity = this.basket.get(item);
                for (int i = 0; i < quantity; i++){
                    this.vendingMachine.dispenseItem(item.getID());
                    this.event(STR."Dispensed one \{item.render().get("name")}.");
                }
            }
            this.event("All selected items dispensed successfully. Thank you for your purchase!");
        }
        if (this.payDue != 0 ) {
            this.processCoinWithdrawal(this.balance);
            //^ Since 'this.balance' exists, might as well use it instead of 'this.payDue*-1' (negated).
        }
        this.fullReset();
    }

    @Override
    public void GetBasket() {
        if (this.inMaintenance()){ return; }

        this.event(this.basket);
    }

    @Override
    public void GetItemStock() {
        if (this.inMaintenance()){ return; }

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
