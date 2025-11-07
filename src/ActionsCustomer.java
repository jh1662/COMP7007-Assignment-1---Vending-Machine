public interface ActionsCustomer extends Actions {

    //: Payment and ordering actions
    //! depositCoins is inherited from Actions
    public void selectItem();
    public void deselectItem();
    public void checkout();
    public void cancelOrder();

    //: Getters
    public void GetBasket();
    public void GetItemStock();
    //^ Views all offered items and their corresponding stock.
}
