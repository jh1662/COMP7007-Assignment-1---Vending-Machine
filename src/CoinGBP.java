/**
 * Enum representing various denominations of British Pound coins.
 * Each coin has an associated monetary value.
 * <p>
 * Note that comment thought the code repository, The different coins are referred to as coin "types" in some places and coin "denominations" in others; both terms refer to the same thing.
 */
public enum CoinGBP {
    ONE_PENNY(0.01),
    TWO_PENCE(0.02),
    //!^ Not listed in assessment brief but lecturer said that it does not matter.
    FIVE_PENCE(0.05),
    TEN_PENCE(0.10),
    TWENTY_PENCE(0.20),
    FIFTY_PENCE(0.50),
    ONE_POUND(1.00),
    TWO_POUNDS(2.00);

    private final double value;

    /**
     * Constructor assigns double representation of the item type for balance calculations.
     */
    CoinGBP(double value) { this.value = value; }
    //^ Assigns money value (double) to each coin (final constant).

    /**
     * Getter method for 'this.value'.
     * @return Monetary value of the coin.
     */
    public double getValue() { return value; }
    //^ Used for calculating/adding to vending machine balance.

    /**
     * Overrides default toString method to provide user-friendly string representation.
     * @return String representation of the coin denomination.
     */
    @Override
    public String toString() {
        //* Used to notify user what coins they inserted or what coin is responsible for a thrown error.
        return switch (this) {
            case ONE_PENNY -> "one penny coin";
            case TWO_PENCE -> "two pence coin";
            case FIVE_PENCE -> "five pence coin";
            case TEN_PENCE -> "ten pence coin";
            case FIFTY_PENCE -> "fifty pence coin";
            case ONE_POUND -> "one pound";
            default -> "two pounds coin";
            //^ All other enum values covered, hence default case corresponds to the 2 pound coin.
        };
    }
}