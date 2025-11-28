import java.util.HashMap;

/**
 * Main class to demonstrate example use-case of the vending machine system.
 * Testing is conducted through JUnit unit tests.
 */
public class Main {
    /**
     * Main method to run the example use-case to test capabilities and robustness.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        //! Vigorous testing done in JUnit unit tests.

        System.out.println("> printed lines prefixed with '>' are comments explaining the program and the use case example.");

        System.out.println("> ItemFactory singleton instance created (singleton pattern class).");
        ItemFactory itemFactory = ItemFactory.getInstance();
        //^ Singleton pattern - only one instance of ItemFactory is needed, and it is fetched.
        VendingMachineFactory vendingMachineFactory = VendingMachineFactory.getInstance();
        //^ Singleton pattern - only one instance of VendingMachineFactory is needed, and it is fetched.

        System.out.println("> Starting example use-case.");
        Main.exampleUseCase(itemFactory, vendingMachineFactory);
        //^ Example use-case.

    }

    /**
     * Example use-case testing the vending machine system capabilities and robustness.
     * @param itemFactory The ItemFactory instance for creating items.
     * @param vendingMachineFactory The VendingMachineFactory instance for creating vending machines.
     */
    private static void exampleUseCase(ItemFactory itemFactory, VendingMachineFactory vendingMachineFactory) {
        System.out.println("> Starting example case.");

        System.out.println("> A vending machine is attempted to be made but specification is invalid (forgot to add coin capacity for twenty-coin coins).");
        try {
            VendingMachine vendingMachine = vendingMachineFactory.createVendingMachine(9, 10, new HashMap<>() {{
                //* The creation of a vending machine with 9 slots, each with a maximum capacity of 10 items, and accept all GBP dominions except.
                put(CoinGBP.TWO_PENCE, 50);
                put(CoinGBP.ONE_PENNY, 50);
                put(CoinGBP.FIVE_PENCE, 50);
                put(CoinGBP.TEN_PENCE, 50);
                put(CoinGBP.TWENTY_PENCE, null);
                put(CoinGBP.FIFTY_PENCE, 50);
                put(CoinGBP.ONE_POUND, 50);
                put(CoinGBP.TWO_POUNDS, 50);
            }});
        }
        catch (IllegalArgumentException e) { System.out.println(">! Caught expected exception: " + e.getMessage()); }


        System.out.println("> A vending machine (composite pattern class) is physically made with specific configurations (12 item slots holding 10 items each and also accepts all GBP dominions with a max capacity of 50 for each coin dominion).");
        VendingMachine vendingMachine = vendingMachineFactory.createVendingMachine(12, 10, new HashMap<>() {{
            //* The creation of a vending machine with 12 slots, each with a maximum capacity of 10 items, and accept all GBP dominions except.
            put(CoinGBP.TWO_PENCE, 50);
            put(CoinGBP.ONE_PENNY, 50);
            put(CoinGBP.FIVE_PENCE, 50);
            put(CoinGBP.TEN_PENCE, 50);
            put(CoinGBP.TWENTY_PENCE, 50);
            put(CoinGBP.FIFTY_PENCE, 50);
            put(CoinGBP.ONE_POUND, 50);
            put(CoinGBP.TWO_POUNDS, 50);
        }});

        System.out.println("> Create admin role (proxy pattern class).");
        AdminProxy adminProxy = new AdminProxy(vendingMachine);
        System.out.println("> Create customer role (proxy pattern class).");
        CustomerProxy customerProxy = new CustomerProxy(vendingMachine);

        System.out.println("> Create display component for admin (observer pattern class).");
        AdminDisplay adminDisplay = new AdminDisplay(adminProxy, vendingMachine.viewSpecifications(), vendingMachine.getCoinMaxes());
        System.out.println("> Create display component for customer (observer pattern class).");
        CustomerDisplay customerDisplay = new CustomerDisplay(customerProxy);

        //: In this single use case, both admin and customer use the vending machine.
        Main.adminUse(adminProxy, itemFactory);
        //^ Include item factory to create items.
        Main.customerUse(customerProxy);

        Main.maliciousUse(customerProxy, adminProxy);
        //^ While 'adminUse' and 'customerUse' methods tests appropriate uses and handling with user errors,
        //^ 'maliciousUse' tests purposely inappropriate uses to ensure the program's robustness.

        System.out.println("> Example case ended");
    }

    /**
     * The admin part of the testing.
     * @param adminProxy The AdminProxy instance for admin actions.
     * @param itemFactory The ItemFactory instance for creating items.
     */
    private static void adminUse(AdminProxy adminProxy, ItemFactory itemFactory) {
        System.out.println("> Admin switch vending machine to maintenance mode in order to start maintaining it.");
        adminProxy.startMaintenance();

        System.out.println("> Admin stocks 20 one-penny coins into the vending machine's coin storage by depositing them (via the physical coin slot) in bulk.");
        adminProxy.depositCoins(CoinGBP.ONE_PENNY, 20);
        System.out.println("> Admin stocks 20 two-pence coins into the vending machine's coin storage by depositing them (via the physical coin slot) in bulk.");
        adminProxy.depositCoins(CoinGBP.TWO_PENCE, 20);
        System.out.println("> Admin stocks 20 five-pence coins into the vending machine's coin storage by depositing them (via the physical coin slot) in bulk.");
        adminProxy.depositCoins(CoinGBP.FIVE_PENCE, 20);
        System.out.println("> Admin stocks 20 ten-pence coins into the vending machine's coin storage by depositing them (via the physical coin slot) in bulk.");
        adminProxy.depositCoins(CoinGBP.TEN_PENCE, 20);
        System.out.println("> Admin stocks 20 twenty-pence coins into the vending machine's coin storage by depositing them (via the physical coin slot) in bulk.");
        adminProxy.depositCoins(CoinGBP.TWENTY_PENCE, 20);
        System.out.println("> Admin stocks 20 fifty-pence coins into the vending machine's coin storage by depositing them (via the physical coin slot) in bulk.");
        adminProxy.depositCoins(CoinGBP.FIFTY_PENCE, 20);
        System.out.println("> Admin stocks 15 one-pound coins into the vending machine's coin storage by depositing them (via the physical coin slot) in bulk.");
        adminProxy.depositCoins(CoinGBP.ONE_POUND, 15);
        System.out.println("> Admin stocks 16 two-pence coins into the vending machine's coin storage by depositing them (via the physical coin slot) in bulk.");
        adminProxy.depositCoins(CoinGBP.TWO_POUNDS, 16);

        System.out.println("> Admin need one of the two-pound coins back for personal use, so withdraws one from the vending machine's coin storage.");
        adminProxy.withdrawCoins(CoinGBP.TWO_POUNDS, 1);

        System.out.println("> Admin makes and assigns the Coke drink to slot #0.");
        adminProxy.assignItemSlot(0, itemFactory.createItem(ItemType.DRINK, "Coke", 26, 1.50, 330));
        System.out.println("> Admin makes the same Coke drink and assign it to slot #1.");
        adminProxy.assignItemSlot(1, itemFactory.createItem(ItemType.DRINK, "Cok", 26, 1.50, 330));
        System.out.println("> Admin makes the a different variation of the Coke drink (slightly smaller) and assign it to slot #2.");
        adminProxy.assignItemSlot(2, itemFactory.createItem(ItemType.DRINK, "Small Coke", 27, 0.94, 300));
        System.out.println("> Admin makes and assigns the BBQ flavoured crisps snack to slot #3.");
        adminProxy.assignItemSlot(3, itemFactory.createItem(ItemType.SNACK, "BBQ flavoured crisps", 28, 2.00, 22));
        System.out.println("> Admin makes and attempts to assign the slat flavoured crisps snack to slot #3, but slot #3 was already assigned so it rejects assignment.");
        adminProxy.assignItemSlot(3, itemFactory.createItem(ItemType.SNACK, "Salt flavoured crisps", 29, 2.00, 22));
        System.out.println("> Admin assigns the slat flavoured crisps snack to slot #4 instead.");
        adminProxy.assignItemSlot(4, itemFactory.createItem(ItemType.SNACK, "Salt flavoured crisps", 29, 2.00, 22));
        System.out.println("> Admin realised that Coke was misspelt, as 'Cok' instead of 'Coke', so unassigns slot #1.");
        adminProxy.unassignItemSlot(1);
        System.out.println("> Admin makes and assigns the correctly spelt Coke drink to slot #1.");
        adminProxy.assignItemSlot(1, itemFactory.createItem(ItemType.DRINK, "Coke", 26, 1.50, 330));
        System.out.println("> Admin wants to assign bio-degradable cutlery to an item slot, but it is neither a drink nor snack, so admin makes use of the program's extensibility by creating a miscellaneous item and assigns it to slot #5.");
        adminProxy.assignItemSlot(5, itemFactory.createItem(ItemType.MISCELLANEOUS, "Bio-degradable cutlery", 30, 0.50, "Spork and knife set!"));

        System.out.println("> Admin fully fills slot #0 with 10 Cokes (bulk action).");
        adminProxy.stockItems(0, 10);
        System.out.println("> Admin has 9 Cokes left, so admin fills slot #1 with 9 Cokes (bulk action).");
        adminProxy.stockItems(1, 9);
        System.out.println("> Admin fully fills slot #0 with 10 of the smaller Cokes (bulk action).");
        adminProxy.stockItems(2, 10);
        System.out.println("> Admin fully fills slot #2 with 10 BBQ flavoured crisp packets (bulk action).");
        adminProxy.stockItems(3, 10);
        System.out.println("> Admin fully fills slot #2 with 10 Salt flavoured crisp packets (bulk action) but accidentally tried to cram one more; only 10 can fit so the last one is rejected.");
        adminProxy.stockItems(4, 11);
        System.out.println("> Admin fully fills slot #5 with 5 bio-degradable cutlery sets (bulk action).");
        adminProxy.stockItems(5, 5);

        System.out.println("> Admin felt thirsty, so removes one small Coke from slot #2 for personal consumption without paying because admin is also thw owner.");
        adminProxy.removeItems(2, 1);

        System.out.println(">! Unlike coins, the vending machine hardware cannot possibly detect which item the admin physically put in the item slots (only if an item was stocked or not), thus is also not included in the program. This means that it is the admin's responsibility to ensure that the correct items are stocked into their assigned slots.");

        System.out.println("> Admin ends maintenance mode so customers can start using it.");
        adminProxy.stopMaintenance();

        System.out.println(">! Admin has successfully added items to the vending machine, deposited money to the vending machine, and withdrawn money from the vending machine.");
        System.out.println(">! Every action updates the display which always display coin capacities, coin current counts, current item slot stock and assignment, number of physical item slots, and slot item capacity (all in a data-compact technical format).");

    }
    /**
     * The customer part of the testing.
     * @param customerProxy The CustomerProxy instance for customer actions.
     */
    private static void customerUse(CustomerProxy customerProxy) {
        System.out.println("> Customer starts order by tapping on the vending machine's touchscreen display.");
        customerProxy.startOrder();

        System.out.println("> Customer comes and select to view current offered items and their respective stock.");
        customerProxy.GetItemStock();
        System.out.println("> Customer selects to purchase one small Coke by typing in its item ID (27).");
        customerProxy.selectItem(27);
        System.out.println("> Customer attempts to select to purchase salted crisp by typing in its item ID (29) but accidentally types in 39 instead.");
        customerProxy.selectItem(39);
        System.out.println("> Customer corrects mistake by typing in item ID 29.");
        customerProxy.selectItem(29);
        System.out.println("> Customer selects to view current offered items and their respective stock again to see if there is anything else of interest.");
        customerProxy.GetItemStock();
        System.out.println("> Customer decided that is it, and views basket to confirm that the customer did indeed selected those 2 items.");
        customerProxy.GetBasket();

        System.out.println("> Customer confirms order to proceed to payment.");
        customerProxy.checkout();
        System.out.println("> Customer inserts coins a two-pound coin.");
        customerProxy.depositCoin(CoinGBP.TWO_POUNDS);
        System.out.println("> Customer inserts coins a fifty-pence coin.");
        customerProxy.depositCoin(CoinGBP.FIFTY_PENCE);
        System.out.println("> Customer realises that the amount inserted is insufficient, so cancels the order and thus gets a refund of the deposited money.");
        customerProxy.cancelOrder();

        System.out.println("> Customer starts order again.");
        customerProxy.startOrder();
        System.out.println("> Customer decides that only the small Coke is wanted, so selects it again by typing in its item ID (27).");
        customerProxy.selectItem(27);
        System.out.println("> Customer accidentally ordered the small Coke again.");
        customerProxy.selectItem(27);
        System.out.println("> Customer views basket but realised the double small Coke order mistake.");
        customerProxy.GetBasket();
        System.out.println("> Customer deselects one small Coke by typing in its item ID (27).");
        customerProxy.deselectItem(27);
        System.out.println("> Customer views basket again to confirm that only the small Coke is in there and the total price is below 2.50 GBP");
        customerProxy.GetBasket();

        System.out.println("> Customer proceeds to checkout for the second time.");
        customerProxy.checkout();
        System.out.println("> Customer inserts coins a two-pound coin, which lead to successful payment, 1.06 GBP change refund, and item dispensing of the small Coke.");
        customerProxy.depositCoin(CoinGBP.TWO_POUNDS);

        System.out.println(">! Customer has successfully deposited money, requested refund of coins (be cancelling order), physically retrieved dispensed coins, and physically retrieved dispensed item.");
        System.out.println(">! Every action updates the display which always displays only the information relevant to the customer in a more user-friendly format.");
    }
    /**
     * The malicious user part of the testing.
     * @param customerProxy The CustomerProxy instance for customer actions.
     * @param adminProxy The AdminProxy instance for admin actions.
     */
    private static void maliciousUse(CustomerProxy customerProxy, AdminProxy adminProxy) {
        System.out.println("> !Malicious user comes and attempts to mess with the vending machine");

        System.out.println("> Malicious user attempts to deposit pennies illegally, to cause malfunction to the vending machine, but cannot due to state restriction so any deposited coins immediately comes out.");
        customerProxy.depositCoin(CoinGBP.ONE_PENNY);
        System.out.println("> Malicious user steals an admin's identity, to then attempts deposit the pennies, but forgot to start maintenance mode so deposit fails and penny come out.");
        adminProxy.depositCoins(CoinGBP.ONE_PENNY, 1);
        System.out.println("> Malicious user manage uses admin's login to finally start maintenance mode.");
        adminProxy.startMaintenance();
        System.out.println("> Malicious user starts depositing pennies in attempt to overflow the coin storage, but can only deposit up to capacity and the rest are rejected.");
        adminProxy.depositCoins(CoinGBP.ONE_PENNY, 32);
        System.out.println("> Malicious user starts withdrawing all pennies in attempt to cause underflow error to the coin storage, but vending machine handles underflow withdraws gracefully.");
        adminProxy.depositCoins(CoinGBP.ONE_PENNY, 51);
        System.out.println("> Malicious user attempts to unassign item slots to prevent items from being bought, but vending machine prevents item slots with existing items stored inside to be unassigned.");
        adminProxy.unassignItemSlot(1);
        System.out.println("> With no time left, malicious user realises that malicious actions (to malfunction the vending machine) are futile; so malicious user stops maintenance mode to prevent further suspicion.");
        adminProxy.stopMaintenance();

        System.out.println("> !Malicious user's malicious attempts ends and leaves in hurry defeated ^_^.");
    }
}