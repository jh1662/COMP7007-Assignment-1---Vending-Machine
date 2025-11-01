public enum CoinGBP {
    ONE_PENCE(0.01),
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
        //* Used to notify user what coins they inserted.
        if (value < 1.0) { return String.format("%.0f pence coin", value * 100);}
        else { return String.format("£%.0f coin", value); }
    }
}