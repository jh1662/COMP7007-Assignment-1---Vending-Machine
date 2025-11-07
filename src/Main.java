import java.util.HashMap;

void main() {
    //* Example use.
    //! Vigorous testing done in JUnit unit tests.

    VendingMachine vendingMachine = new VendingMachine(9, 10, new HashMap<CoinGBP, Integer>() {{
        //! This specific machine starts with 50 of each coin but does not except penny as prices are multiples of 2p.
        put(CoinGBP.TWO_PENCE, 50);
        put(CoinGBP.FIVE_PENCE, 50);
        put(CoinGBP.TEN_PENCE, 50);
        put(CoinGBP.TWENTY_PENCE, 50);
        put(CoinGBP.FIFTY_PENCE, 50);
        put(CoinGBP.ONE_POUND, 30);
        put(CoinGBP.TWO_POUNDS, 30);
    }});

    AdminProxy adminProxy = new AdminProxy(vendingMachine);
    vendingMachine.changeState(VendingMachineState.MAINTENANCE);
    AdminDisplay[] adminDisplays = new AdminDisplay[]{new AdminDisplay(adminProxy, vendingMachine.viewSpecifications(), vendingMachine.getCoinMaxes())};
    vendingMachine.changeState(VendingMachineState.IDLE);

    adminProxy.startMaintenance();

    adminProxy.viewCoins();

    adminProxy.depositCoins(CoinGBP.TWO_PENCE, 20);
    adminProxy.depositCoins(CoinGBP.FIVE_PENCE, 20);
    adminProxy.depositCoins(CoinGBP.TEN_PENCE, 20);
    adminProxy.depositCoins(CoinGBP.TWENTY_PENCE, 20);
    adminProxy.depositCoins(CoinGBP.FIFTY_PENCE, 20);
    adminProxy.depositCoins(CoinGBP.ONE_POUND, 15);
    adminProxy.depositCoins(CoinGBP.TWO_POUNDS, 16);

    adminProxy.viewCoins();

    adminProxy.withdrawCoins(CoinGBP.TWO_POUNDS, 1);

    adminProxy.viewCoins();

    adminProxy.viewItems();

    adminProxy.assignItemSlot(0, new Drink("Coke", 26, 1.50, 330));
    adminProxy.assignItemSlot(1, new Drink("Coke", 26, 1.50, 330));
    adminProxy.assignItemSlot(2, new Drink("water", 27, 1.00, 300));
    adminProxy.assignItemSlot(2, new Drink("BBQ Sandwich", 28, 1.00, 200));
    adminProxy.assignItemSlot(3, new Drink("BBQ Sandwich", 28, 1.00, 200));

    adminProxy.viewItems();

    adminProxy.unassignItemSlot(3);

    adminProxy.viewItems();

    adminProxy.stockItems(0, 10);
    adminProxy.stockItems(1, 9);
    adminProxy.stockItems(2, 10);
    adminProxy.stockItems(3, 10);

    adminProxy.viewItems();

    adminProxy.removeItems(3, 1);

    adminProxy.viewItems();

    adminProxy.stopMaintenance();
}