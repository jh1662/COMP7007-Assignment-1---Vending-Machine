import java.util.HashMap;
import java.util.Map;

public class CoinStorage { //< Composite pattern class - stored in vending machine class for satisfying SRP.
    //* We assume that any deposited coin goes straight to the coin storage instead of a buffer storage, even if
    //* order has not been completed.

    //: Owner/admin cannot change coin capacity or supported coin types - hence the 'final' code.
    private final Map<CoinGBP, Integer> coinCounts = new HashMap<>();
    //^ Current amount of each coin type in the vending machine's coin storage.
    //^ Assigned now so final Map can be mutated in constructor.
    private final Map<CoinGBP, Integer> coinMaxes;
    //^ How much of each coin can be held.
    //^ As coins are different sizes, each coin type will have a different capacity.

    public CoinStorage(Map<CoinGBP, Integer> coinMaxes) {
        for (Map.Entry<CoinGBP, Integer> coinMax : coinMaxes.entrySet() ) {
            //* Prevents the law of physics from being broken.
            if (coinMax.getValue() < 0) { throw new IllegalArgumentException( String.format("Capacity of %s cannot be less than 0", coinMax.getKey().toString() )); }
        }
        //: Maps are used instead of int[] because some vending machine variants may not accept certain coin types;
        //: Such example is one that does not except pennies not 2-pence coins due to value being too low (assuming
        //: all item prices are multiples of 5p).
        this.coinMaxes = coinMaxes;
        for (CoinGBP coin :  coinMaxes.keySet()) { this.coinCounts.put(coin, 0); }
        //^ Admin/owner cannot deposit coins when vending machine is being made - only after creation can owner/
        //^ admin can deposit.
    }

    //: To be used by owner/admin for maintenance and customer to check order.
    //: Getter methods.
    public Map<CoinGBP, Integer> getCoinCounts(){ return this.coinCounts; }
    public Map<CoinGBP, Integer> getCoinMaxes(){ return this.coinMaxes; }

    private void checkSupportedCoins(CoinGBP coin) {
        //* Prevents unsupported coins from being deposited.
        if (!this.coinMaxes.containsKey(coin)) { throw new IllegalArgumentException(String.format("This vending machine does not accept/support %ss. Use another coin type", coin)); }
    }
    //: Calling multiple times, but with simpler parameters, is highly preferred over calling once with a Map argument
    //: in parameter for less complexity and thus better readability - comparing 'CoinGBP coin' parameter with '
    //: Map<CoinGBP, Integer> coins' parameter.
    public void deposit(CoinGBP coin){
        //* When customer deposit too much, all deposited will be refunded; but when owner/admin deposit too much, only
        //* the over flowing coins are refunded.
        //* This differance in behaviour is another reason for the method called per coin instead of multiple coins.
        this.checkSupportedCoins(coin);
        if((this.coinCounts.get(coin) + 1) > this.coinMaxes.get(coin)) { throw new IllegalArgumentException( String.format("Capacity of %s would be exceeded", coin.toString() )); }
        //^ Extra bracket set is not necessary but makes expression more readable.
        //^ Prevents too much coins to be deposited.
        this.coinCounts.put(coin, this.coinCounts.get(coin) + 1);
        //^ Method carrying out the verified operation.
    }
    public void withdraw(CoinGBP coin){
        this.checkSupportedCoins(coin);
        //* Same structure as 'this.deposit'
        if((this.coinCounts.get(coin) - 1) < 0) { throw new IllegalArgumentException( String.format("Do not have %ss to withdraw", coin.toString() )); }
        this.coinCounts.put(coin, this.coinCounts.get(coin) - 1);
    }
}

/* The complex deposit method if 'Map<CoinGBP, Integer> coins' parameter was used instead:
    public void deposit(Map<CoinGBP, Integer> coins){
        //* Admin/owner use only.
        for (Map.Entry<CoinGBP, Integer> coin : coins.entrySet()) {
            this.checkSupportedCoins(coin.getKey());
            if( (coin.getValue() + this.coinCounts.get(coin.getKey())) > this.coinMaxes.get(coin.getKey())) {
                //^ Extra bracket set is not necessary but makes expression more readable.
                //* Prevents too much coins to be deposited
                throw new IllegalArgumentException( String.format("Capacity of %s would be exceeded", coin.getKey().toString() ));
            }
        }
        for (Map.Entry<CoinGBP, Integer> coin : coins.entrySet()) { this.deposit(coin.getKey(), coin.getValue()); }
        //^ Method carrying out the verified operation.
    }
 */