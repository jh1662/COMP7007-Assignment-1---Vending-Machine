public interface ActionsCustomer {

    //: Payment and ordering actions
    //! depositCoins is inherited from Actions
    void selectItem();
    void deselectItem();
    void checkout();
    void cancelOrder();
    void depositCoins(CoinGBP coin);

    //: Getters
    void GetBasket();
    void GetItemStock();
    //^ Views all offered items and their corresponding stock.
}
