/**
 * API for users interacting with the vending machine
 */
public interface actionsOwner {

    public void depositCoins();
    public void withdrawCoins();
    public void restockItems();
    public void addItems();
    public void removeItems();

    //: Getters.
    public void getItems();
    public void GetSpecifications();
}