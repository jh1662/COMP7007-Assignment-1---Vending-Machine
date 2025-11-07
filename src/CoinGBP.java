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

    CoinGBP(double value) { this.value = value; }
    //^ Assigns money value (double) to each coin (final constant).

    public double getValue() { return value; }
    //^ Used for calculating/adding to vending machine balance.

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