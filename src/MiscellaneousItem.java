import java.util.HashMap;
import java.util.Map;

public class MiscellaneousItem extends Item{
    private final String description;
    //^ Due to no specialty (like weight or volume), its needs a description to explain its exact contents.
    //^ Recommended to keep said description short and concise, for example "holds two forks".

    public MiscellaneousItem(String name, int iD, double price, String description) {
        super(name, iD, price);
        this.description = description;
    }

    @Override
    public Map<String, String> render() {
        Map<String, String> details = new HashMap<>(super.render());
        //^ 'new HashMap<>' makes copy to allow appending more details.
        details.put("description", this.description);
        return details;
    }
}