public interface ActionsCustomer {

    //: Payment and ordering actions
    //! depositCoins is inherited from Actions
    void selectItem(int itemID);
    void deselectItem(int itemID);
    void checkout();
    void cancelOrder();
    void depositCoin(CoinGBP coin);

    //: Getters
    void GetBasket();
    void GetItemStock();
    //^ Views all offered items and their corresponding stock.
}
