import java.util.Map;

public interface ActionsOwner extends Actions {

    //: Relating to coin storage.
    public void depositCoins(CoinGBP coin, int amount);
    public void withdrawCoins(CoinGBP coin, int amount);
    public Map<CoinGBP, Integer> viewCoins();

    //: Relating to item storage.
    public void stockItems(int slotNum, int amount);
    public void removeItems(int slotNum, int amount);
    public void assignItemSlot(int slotNum, Item item);
    public void unassignItemSlot(int slotNum);
    public ItemSlot[] viewItems();

    //: Relating to vending machine itself.
    /// public int[] viewSpecifications();
    public void startMaintenance();
    public void stopMaintenance();
    public void getState();

}