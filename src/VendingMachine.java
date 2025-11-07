import java.util.Map;

public class VendingMachine {
    private VendingMachineState state;
    private final ItemStorage itemStorage;
    private final CoinStorage coinStorage;
    //! listeners (for displays) handled by proxies.
    //! There is no (customer) balance field here as it is handles by the customer proxy class.

    public VendingMachine(int slotNumber, int slotSize, Map<CoinGBP, Integer> coinStorage) {
        this.state = VendingMachineState.IDLE;
        this.itemStorage = new ItemStorage(slotNumber, slotSize);
        this.coinStorage = new CoinStorage(coinStorage);
    }

    public void changeState(VendingMachineState newState){
        //* Implementation for changing the state of the vending machine.
        //* Message intended for admin/owner.
        if (!(this.state == VendingMachineState.IDLE) && newState == VendingMachineState.MAINTENANCE) {
            throw new IllegalStateException("Cannot enter MAINTENANCE mode where not in IDLE state");
        }
        this.state = newState;
    }
    public VendingMachineState getState() {
        return this.state;
    }

    public int[] viewSpecifications() {
        //* Implementation for viewing the specifications of the vending machine.
        //* Message intended for admin/owner.
        return new int[]{
            this.itemStorage.getMaxSlots(),
            this.itemStorage.getMaxAmount()
        };
        //! For code simplicity, the accepted coins specs are fetched and interpreted from 'this.insertCoin'
    }
    public Map<CoinGBP, Integer> getCoinMaxes(){
        //* Implementation for viewing the coin storage maximums of the vending machine.
        //* Message intended for admin/owner.
        //* One-shot method.
        return this.coinStorage.getCoinMaxes();
    }

    //: Computed getter methods.
    public ItemSlot[] getItemStorage() {
        if (this.state != VendingMachineState.MAINTENANCE) {
            throw new IllegalStateException("Cannot view item storage when not in MAINTENANCE state");
        }
        return this.itemStorage.render();
    }
    public Map<CoinGBP, Integer> getCoinStorage() {
        if (this.state != VendingMachineState.MAINTENANCE) {
            throw new IllegalStateException("Cannot view coin storage when not in MAINTENANCE state");
        }
        return this.coinStorage.getCoinCounts();
    }

    public void stockItem(int iD) {
        //* Implementation for restocking item by item ID.
        //* Used for admin/owner restocking items only.
        if (state != VendingMachineState.MAINTENANCE) {
            throw new IllegalStateException("Cannot restock item when not in MAINTENANCE state");
        }
        this.itemStorage.restockItem(iD);
    }
    public void dispenseItem(int iD) {
        //* Implementation for getting item by item ID.
        //* Used for dispensing item to customer or admin/owner taking out expired items.
        if (state != VendingMachineState.DISPENSING && this.state != VendingMachineState.MAINTENANCE) {
            throw new IllegalStateException("Cannot dispense item when not in DISPENSING state");
            //^ Customer message.
        }
        this.itemStorage.dispenseItem(iD);
    }
    public void assignSlot(int slotNum, Item item){
        //* Implementation for assigning an item slot to an item.
        //* Used for admin/owner assigning item slots.
        if (state != VendingMachineState.MAINTENANCE) {
            throw new IllegalStateException("Cannot assign item slot when not in MAINTENANCE state");
        }
        this.itemStorage.assignSlot(slotNum, item);
    }
    public void unassignSlot(int slotNum){
        //* Implementation for unassigning an item slot to an item.
        //* Used for admin/owner unassigning item slots.
        if (state != VendingMachineState.MAINTENANCE) {
            throw new IllegalStateException("Cannot unassign item slot when not in MAINTENANCE state");
        }
        this.itemStorage.unassignSlot(slotNum);
    }

    public void withdrawCoin(CoinGBP coin) {
        //* Implementation for returning unaccepted coins when coin stock is too full or is unsupported by the vending
        //* machine instance.
        if (state != VendingMachineState.PAYING && state != VendingMachineState.MAINTENANCE) {
            this.coinStorage.withdraw(coin);
            throw new IllegalStateException("Cannot withdraw coin when not in PAYING or MAINTENANCE state");
            //^ Message intended for customer.
        }
        this.coinStorage.withdraw(coin);
    }
    public void insertCoin(CoinGBP coin) {
        //* Implementation for inserting a coin into the vending machine.
        //* Used for customer paying or owner/admin restocking coins.
        //* If for customer, balance updated in customer proxy class.
        if (this.state != VendingMachineState.PAYING && this.state != VendingMachineState.MAINTENANCE) {
            //* Coin can only be inserted when in PAYING (customer) or MAINTENANCE (admin/owner) state.
            this.withdrawCoin(coin);
            throw new IllegalStateException("Only insert coin when not in PAYING state");
            //^ Message intended for customer
        }
        this.coinStorage.deposit(coin);
    }
}
