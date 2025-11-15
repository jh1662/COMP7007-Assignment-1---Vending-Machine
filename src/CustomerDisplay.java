import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class CustomerDisplay implements Observer {
    private final Observable observable;
    //: No need to call fixed data multiple times (only called once); so it is stored locally to save compute power.
    private final int maxSlots;
    private final int maxItemsPerSlot;
    private final Map<CoinGBP, Integer> coinMaxes;
    //^ Coins that are not included here are not accepted by the vending machine;
    //^ hence unaccepted coin types implicitly have a max of 0.

    public CustomerDisplay(Observable observable, int[] specifications, Map<CoinGBP, Integer> coinMaxes) {
        this.observable = observable;
        this.maxSlots = specifications[0];
        this.maxItemsPerSlot = specifications[1];
        this.coinMaxes = coinMaxes;

        this.observable.addObserver(this);
        //^ Adds this display/observer instance to the observable's (AdminProxy's) list of observers.
    }

    public void update(Observable o, Object message) {
        //* Simpler to use 'Object' than either genetics or wildcard.
        switch (message.getClass().getSimpleName()){
            //* Enhanced switch statement for better readability.
            case "IllegalArgumentException" -> this.display(o, "INVALID OPERATION - " + ((IllegalArgumentException) message).getMessage());
            case "IllegalStateException" -> this.display(o, "INVALID STATE - " + ((IllegalStateException) message).getMessage());
            case "HashMap<CoinGBP, Integer>" -> this.display(o, "CURRENT COIN STORAGE - " + message.toString());
            case "HashMap<Item, Integer>[]" -> this.display(o, "CURRENT ITEM STORAGE BY SLOTS - " + message.toString());
            case "VendingMachineState" -> this.display(o, "CURRENT VENDING MACHINE STATE - " + message.toString());
            default ->  this.display(o, "NOTICE - " + message.toString());
            //^ Default case for String, int, etc; being default case, it also tackles external errors.
        }
    }

    public void display(Observable o, String formattedMessage) {

    }
}
