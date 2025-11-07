import java.util.Map;
import java.util.Observable;
//^ Using package simplifies code as does not require to implement an observer interface and observable methods from scratch.

public class AdminProxy extends Observable implements ActionsOwner {
    /// private final DisplayListener[] displayListeners;
    private final VendingMachine vendingMachine;

    public AdminProxy(VendingMachine vendingMachine) {
        /// this.displayListeners = displayListeners;
        this.vendingMachine = vendingMachine;
    }

    public void event(Object obj){
        this.setChanged();
        this.notifyObservers(obj);
    }
    /*
    public <T> void event(T message) {
        //* Implementation for sending messages to display listeners.
        for (DisplayListener listener : this.displayListeners) { listener.listen(message); }
    }
    */

    //: Most important actions - actually starting/stopping maintenance mode.
    @Override
    public void startMaintenance() {
        this.vendingMachine.changeState(VendingMachineState.MAINTENANCE);
    }
        @Override
    public void stopMaintenance() {
        this.vendingMachine.changeState(VendingMachineState.IDLE);
    }
    public VendingMachineState getState() {
        return this.vendingMachine.getState();
    }

    //: Relating to coin storage.
    @Override
    public void depositCoins(CoinGBP coin, int amount) {
        this.event("NOTICE - Depositing coins:...");
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
        this.event("NOTICE - All coins deposited successfully.");
    }
    @Override
    public void withdrawCoins(CoinGBP coin, int amount) {
        this.event("NOTICE - Withdrawing coins:...");
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
        this.event("NOTICE - All coins withdraw successfully.");
    }
    @Override
    public Map<CoinGBP, Integer> viewCoins() {
        return vendingMachine.getCoinStorage();
    }

    //: Relating to item storage.
    @Override
    public void stockItems(int slotNum, int amount) {
        this.event("NOTICE - attempting to stock +" + amount + " items into slot " + slotNum + "...");
        try {
            for (int i = 0; i < amount; i++) {
                this.vendingMachine.stockItem(slotNum);
                this.event("Item stocked into slot " + slotNum);
            }
        }
        catch (IllegalArgumentException e) {
                this.event(e);
                return;
            }
        this.event("NOTICE - All items stocked successfully.");
    }
    @Override
    public void removeItems(int slotNum, int amount) {
        this.event(STR."NOTICE - attempting to remove +\{amount} items into slot #\{slotNum}...");
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
        this.event(STR."NOTICE - All wanted items (in slot #\{slotNum}) removed successfully.");
    }
    @Override
    public void assignItemSlot(int slotNum, Item item) {
        try{
            this.vendingMachine.assignSlot(slotNum, item);
        }
        catch (IllegalArgumentException | IllegalStateException e){
            this.event(e);
            return;
        }
        this.event(STR."NOTICE - Slot #\{slotNum} assigned to item \{item.render().get("name")} successfully.");
    }
    @Override
    public void unassignItemSlot(int slotNum) {
        try{
            this.vendingMachine.unassignSlot(slotNum);
        }
        catch (IllegalArgumentException | IllegalStateException e){
            this.event(e);
            return;
        }
        this.event(STR."NOTICE - Slot #\{slotNum} unassigned successfully.");
    }
    @Override
    public ItemSlot[] viewItems() {
        return this.vendingMachine.getItemStorage();
    }
}
