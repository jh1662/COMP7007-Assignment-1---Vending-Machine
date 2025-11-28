import java.util.Map;
/**
 * Supports creation of valid specification vending machines
 * <p>
 * "factory method design pattern" implementation for creating valid vending machines (relieves vending machine class constructor of validation code).
 * Enforced single instance using the "singleton design pattern".
 * <p>
 * "factory method design pattern" is used instead of "abstract factory design pattern" as only one creation method is needed here instead of added complexity.
 * Only one class is made here, and its creation does not require complex logic.
 * <p>
 * Constructor is not needed for factory method pattern as the only initialization is the singleton instance.
 */
public class VendingMachineFactory {
    private static final VendingMachineFactory instance = new VendingMachineFactory();

    /**
     * Getter for the only instance of VendingMachineFactory ('VendingMachineFactory.instance').
     * <p>
     * Enforces singleton pattern.
     * @return instance reference of VendingMachineFactory instance.
     */
    public static VendingMachineFactory getInstance() { return instance; }

    /**
     * Common validation helper method for sizes (e.g., slot size, number of slots, coin storage sizes, etc.).
     * <p>
     * Purposely simple so developer can extend this method to add more restrictions, such as adding upper limit, if needed.
     * <p>
     * Only non-positive sizes are restricted for now.
     * @param size The size to validate.
     * @return 'true' if the size is valid; otherwise 'false'.
     */
    private boolean validateSize(int size){
        //* Common validation method for sizes (e.g., slot size, number of slots, coin storage sizes, etc.).
        return size > 0;
        //! Purposely simple (good SRP) so developer can extend this method to add more restrictions, such as adding upper limit, if needed.
        //! Specification did not mention the maximum size restrictions, so only non-positive sizes are restricted for now.
    }

    /**
     * Specific validation helper method for the "number of slots" vending machine specification argument.
     * <p>
     * Purposely simple so developer can extend this method to add more restrictions, such as adding upper limit, if needed.
     * <p>
     * Only non-positivity and unevenness are restricted for now.
     * @param numOfSlots The number of slots where the number is to be validated.
     * @return 'true' if the number of slots is valid; otherwise 'false'.
     */
    private boolean validateNumOfSlots(int numOfSlots){
        if (!this.validateSize(numOfSlots)) return false;
        if (numOfSlots % 2 != 0) return false;
        //^ Vending machine must have even numbed of slots to ensure balanced layout - vending machines with odd shapes
        //^ are very unappealing to the customer.
        //! if (...)  { return false; } <-- template to use when adding more restrictions (to readers and other developers).

        return true;

        //! Purposely simple (good SRP) so developer can extend this method to add more restrictions, such as adding upper limit, if needed.
    }

    /**
     * Specific validation helper method for the "slot size" vending machine specification argument.
     * <p>
     * Purposely simple so developer can extend this method to add more restrictions if needed.
     * <p>
     * Only non-positivity and excessive size (must be below 31) are restricted for now.
     * @param slotSize The slot size where the size is to be validated.
     * @return 'true' if the slot size is valid; otherwise 'false'.
     */
    private boolean validateSlotSize(int slotSize){
        if (!this.validateSize(slotSize)) return false;
        if (slotSize > 30) return false;
        //^ Makes zero sense to have item slots that can hold more than 30 items as that is excessive for a vending machine.
        //^ If much of one item is needed, simply use multiple item slots, for that item type, instead.
        //! if (...)  { return false; } <-- template to use when adding more restrictions (to readers and other developers).

        return true;

        //! Purposely simple (good SRP) so developer can extend this method to add more restrictions, such as adding upper limit, if needed.
    }

    /**
     * Specific validation helper method for the "coin storage" Map vending machine specification argument.
     * <p>
     * Purposely simple so developer can extend this method to add more restrictions, such as adding upper limit, if needed.
     * <p>
     * Only non-emptiness, non-positivity and null values are restricted for now.
     * @param coinStorage The coin storage Map where the size is to be validated.
     * @return 'true' if the coin storage Map is valid; otherwise 'false'.
     */
    private boolean validateCoinStorage(Map<CoinGBP, Integer> coinStorage){
        if (coinStorage.isEmpty()) return false;
        if (coinStorage.values().stream().anyMatch(capacity -> capacity == null)) return false;
        //^ Prevents 'NullPointerException' runtime error when validating the Map values.
        //^ Can use 'anyMatch(Objects::isNull)' instead but requires import ('import java.util.Objects;') - simpler
        //^ and more lightweight to just use 'anyMatch(capacity -> capacity == null)'.
        if (!coinStorage.values().stream().allMatch(this::validateSize)) return false;
        //! if (...)  { return false; } <-- template to use when adding more restrictions (to readers and other developers).

        //! Purposely simple (good SRP) so developer can extend this method to add more restrictions, such as adding upper limit, if needed.

        return true;
    }

    /**
     * Helper method is called to validate all vending machine specification arguments - simplifies validation call/process.
     * <p>
     * Calls specific and common validation methods for each specification argument.
     * <p>
     * Only helper method called by the vending machine creation method 'this.createVendingMachine'.
     * @param maxSlots    The maximum number of item slots specification argument.
     * @param slotSize    The item slot size specification argument.
     * @param coinStorage The coin storage Map specification argument.
     * @throws IllegalArgumentException If any of the specification arguments are invalid.
     */
    private void validateSpecifications(int maxSlots, int slotSize, Map<CoinGBP, Integer> coinStorage){
        if (!this.validateNumOfSlots(maxSlots)) throw new IllegalArgumentException("Number of item slots argument is invalid.");
        if (!this.validateSlotSize(slotSize)) throw new IllegalArgumentException("Item slot size argument is invalid.");
        if (!this.validateCoinStorage(coinStorage)) throw new IllegalArgumentException("Coin storage Map argument is invalid.");
    }

    /**
     * Factory method for creating vending machines based on the provided specifications.
     * <p>
     * Validates all specification arguments before creating the vending machine to ensure data integrity.
     * @param maxSlots    The maximum number of item slots in the vending machine.
     * @param slotSize    The item slot size in the vending machine.
     * @param coinStorage The coin storage Map in the vending machine.
     * @return A new VendingMachine instance with the specified configurations (if valid).
     * @throws IllegalArgumentException If any of the specification arguments are invalid.
     */
    public VendingMachine createVendingMachine(int maxSlots, int slotSize, Map<CoinGBP, Integer> coinStorage) {
        this.validateSpecifications(maxSlots, slotSize, coinStorage);
        return new VendingMachine(maxSlots, slotSize, coinStorage);
    }
}
