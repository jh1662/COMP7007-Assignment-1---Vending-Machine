import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

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

    public CustomerProxy(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
        this.payDue = 0.0;
        this.basket = new HashMap<>();
    }

    private void event(Object obj){
        this.setChanged();
        //^ from the 'Observable' superclass - marks this Observable object as having been changed.
        this.notifyObservers(obj);
        //^ from the 'Observable' superclass - if this object has changed, which it has in this scope as indicated by
        //^ the 'setChanged' method, then notify all of its observers.
    }

    @Override
    public void selectItem() {

    }

    @Override
    public void deselectItem() {

    }

    @Override
    public void checkout() {

    }

    @Override
    public void cancelOrder() {
        switch (this.vendingMachine.getState()){
            case IDLE -> this.event(STR."No ongoing order to cancel dear customer.");

            case ORDERING -> {
                this.basket.clear();
                this.event(STR."Order cancelled, basket cleared. sorry to see you go!");
            }

            case PAYING -> {
                this.basket.clear();
                this.event(STR."Order cancelled during payment. Sorry to see you go. Refunding processing...");


            }
        }
    }

    @Override
    public void depositCoins(CoinGBP coin) {
        try {
            this.vendingMachine.insertCoin(coin);
        }
        catch (IllegalArgumentException | IllegalStateException e){
            this.event(e);
            return;
        }
        this.payDue -= coin.getValue();
        this.event(STR."Deposited \{coin.toString()}. Current balance: £\{String.format("%.2f", this.payDue)}");
    }

    @Override
    public void GetBasket() {
        this.event(this.basket);
    }

    @Override
    public void GetItemStock() {
        this.event(this.vendingMachine.getItemStorage());
    }
}
