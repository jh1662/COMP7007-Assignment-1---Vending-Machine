import java.util.HashMap;
import java.util.Map;

public class Snack extends Item{
    private final int weight;
    //^ In grams.

    public Snack(String name, int iD, double price, int weight) {
        super(name, iD, price);
        this.weight = weight;
    }

    @Override
    public Map<String, String> render() {
        Map<String, String> details = new HashMap<>(super.render());
        //^ 'new HashMap<>' makes copy to allow appending more details.
        details.put("weight", Integer.toString(this.weight));
        return details;
    }
}