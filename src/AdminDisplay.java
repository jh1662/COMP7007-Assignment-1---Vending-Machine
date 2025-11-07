import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class AdminDisplay implements Observer {
    private final Observable observable;
    //: No need to call fixed data multiple times (only called once); so it is stored locally to save compute power.
    private final int maxSlots;
    private final int maxItemsPerSlot;
    private final Map<CoinGBP, Integer> coinMaxes;
    //^ Coins that are not included here are not accepted by the vending machine;
    //^ hence unaccepted coin types implicitly have a max of 0.

    /**
     * Constructor for AdminDisplay which observes an Observable (AdminProxy) for vending machine updates.
     * @param observable The observable object (AdminProxy) to observe for updates.
     * @param specifications An array containing vending machine specifications: [maxSlots, maxItemsPerSlot].
     * @param coinMaxes A map containing the maximum capacities for each accepted coin type.
     */
    public AdminDisplay(Observable observable, int[] specifications, Map<CoinGBP, Integer> coinMaxes) {
        this.observable = observable;
        this.maxSlots = specifications[0];
        this.maxItemsPerSlot = specifications[1];
        this.coinMaxes = coinMaxes;
    }

    /**
     * Update method called when the observable notifies observers of a change.
     * @param o The observable object.
     * @param message The message object containing update information.
     */
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

    public String coinsToString(Map<CoinGBP, Integer> coinMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("Coin Storage Status:\n");
        for (Map.Entry<CoinGBP, Integer> entry : coinMap.entrySet()) {
            sb.append(String.format(" - %s: %d/%d\n", entry.getKey(), entry.getValue(), coinMaxes.get(entry.getKey())));
        }
        return sb.toString();
    }

    /**
     * Displays the current vending machine specifications and a formatted message.
     * Priorities showing full and comprehensive disclosure to the owner/admin over pretty formatting.
     * @param formattedMessage The primary information to be displayed to the admin/owner.
     * @param o The observable object (AdminProxy) providing the vending machine data.
     */
    public void display(Observable o, String formattedMessage) {
        System.out.println("================================= ADMIN CONSOLE DISPLAY =================================");
        //: immediate and frequent constant stats - admin/owner would always want to see these every time vending machine is in MAINTENANCE mode.
        System.out.println(STR."Current Vending Machine specifications: max slots - \{this.maxSlots}, max items per slot - \{this.maxItemsPerSlot}.");
        //^ Using 'STR' is much simpler and compact than using 'String.format' or concatenation for more than 2 concatenating components.
        System.out.println(STR."Accepted coin types:  \{String.join(", ", coinMaxes.keySet().stream().map(CoinGBP::toString).toList())}.");
        //^ '.stream()' is a single-line simple alternative to traditional 'for' loops for transforming collections; hence is used.
        //^ Stream converts CoinGBP[] coin kinds (keys of map) to a list of display strings using 'CoinGBP.toString' method.
        System.out.println(STR."Respective coin capacities: \{String.join(", ", coinMaxes.values().stream().map(String::valueOf).toList())}");
        //^ Stream converts coin capacities (values of map) to a list of display strings using 'String.valueOf' method.

        //: mutable but frequent stats - admin/owner would always want to see these every time vending machine is in MAINTENANCE mode.
        System.out.println(STR."Current vending machine mode - \{((AdminProxy) o).getState().toString()}.");
        System.out.printf(((AdminProxy)o).viewCoins().toString());
        System.out.printf(Arrays.toString(((AdminProxy) o).viewItems()));

        System.out.printf(formattedMessage);
        System.out.println("================================= ===================== =================================");
    }
}
