import java.util.HashMap;

/**
 * Main class to demonstrate example use-case of the vending machine system.
 * Testing is conducted through JUnit unit tests.
 */
public class Main {
    /**
     * Main method to run the example use-case.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        //* Example use-case.
        //! Vigorous testing done in JUnit unit tests.

        System.out.println("Starting example case");

        ItemFactory itemFactory = ItemFactory.getInstance();

        VendingMachine vendingMachine = new VendingMachine(9, 10, new HashMap<>() {{
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
        AdminDisplay adminDisplays = new AdminDisplay(adminProxy, vendingMachine.viewSpecifications(), vendingMachine.getCoinMaxes());
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

        adminProxy.assignItemSlot(0, itemFactory.createItem(ItemType.DRINK, "Coke", 26, 1.50, 330));
        adminProxy.assignItemSlot(1, itemFactory.createItem(ItemType.DRINK, "Coke", 26, 1.50, 330));
        adminProxy.assignItemSlot(2, itemFactory.createItem(ItemType.DRINK, "Coke", 27, 1.00, 300));
        adminProxy.assignItemSlot(3, itemFactory.createItem(ItemType.SNACK, "BBQ flavoured crisps", 28, 2.00, 22));
        adminProxy.assignItemSlot(3, itemFactory.createItem(ItemType.SNACK, "BBQ flavoured crisps", 28, 2.00, 22));

        adminProxy.viewItems();

        adminProxy.unassignItemSlot(3);

        adminProxy.viewItems();

        adminProxy.stockItems(0, 10);
        adminProxy.stockItems(1, 9);
        adminProxy.stockItems(2, 10);

        adminProxy.stockItems(3, 10);

        adminProxy.viewItems();

        adminProxy.removeItems(2, 1);

        adminProxy.viewItems();

        adminProxy.stopMaintenance();
    }
}