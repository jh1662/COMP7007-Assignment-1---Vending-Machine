import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class CustomerDisplay implements Observer {
    private final Observable observable;

    /**
     * Constructor for CustomerDisplay which observes an Observable (AdminProxy) for vending machine updates.
     * @param observable The observable object (AdminProxy) to observe for updates.
     */
    public CustomerDisplay(Observable observable) {
        this.observable = observable;

        this.observable.addObserver(this);
        //^ Adds this display/observer instance to the observable's (AdminProxy's) list of observers.
    }

    private String displayItemList(Map<Item, Integer> itemMap) {
        //* Helper method to format item list for either vending machine stock or basket contents.
        StringBuilder itemList = new StringBuilder();

        for (Map.Entry<Item, Integer> entry : itemMap.entrySet()) {
            itemList.append(STR."\{entry.getKey().render()} - Stock: \{entry.getValue()}\n");
        }

        return itemList.toString();
    }

    /**
     * Update method called when the observable notifies observers of a change.
     * @param o The observable object.
     * @param message The message object containing update information.
     */
    @Override
    public void update(Observable o, Object message) {
        //* Simpler to use 'Object' than either genetics or wildcard.
        switch (message.getClass().getSimpleName()){
            //* Enhanced switch statement for better readability.
            //: Few cases are needed as customer display only shows limited information (only information relevant to the customer).
            case "IllegalArgumentException" -> this.display(o, "Something went wrong - " + ((IllegalArgumentException) message).getMessage());
            case "IllegalStateException" -> this.display(o, "Something went wrong - " + ((IllegalStateException) message).getMessage());
            case "HashMap" -> this.display(o, "Items:\n" + this.displayItemList((Map<Item, Integer>) message));
            default ->  this.display(o, "Notice - " + message.toString());
            //^ Default case for String, int, etc; being default case, it also tackles external errors.
        }
    }

    /**
     * Displays the current vending machine specifications and a formatted message.
     * Priorities showing full and comprehensive disclosure to the owner/admin over pretty formatting.
     * Assumes that admin/owner's console wraps long lines automatically.
     * @param formattedMessage The primary information to be displayed to the admin/owner.
     * @param o The observable object (AdminProxy) providing the vending machine data.
     */
    public void display(Observable o, String formattedMessage) {
        StringBuilder consoleInterface = new StringBuilder();

        //: Fancier and less cluttering format for customer display; as they do not need as much technical detail as admin/owner.
        //: Customer display focuses on user-friendliness unlike admin display.
        consoleInterface.append("~Welcome to the Snack 'n' Drinks vending machine~\n");
        consoleInterface.append(formattedMessage + "\n");
        consoleInterface.append("~-----------------------------------------------~\n");

        System.out.print(consoleInterface);
    }
}
