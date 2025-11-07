import java.util.Map;

public abstract class Item {
     /// private final ItemType type;
     private final String name;
     private final int iD;
     private final double price;

     public Item(String name, int iD, double price) {
         this.name = name;
         this.iD = iD;
         this.price = price;
     }

    //: Getter methods.
    public double getPrice() { return price; }
    //^ For customer purchases.
    public int getID() { return iD; }
    //^ For finding duplicate iDs in item storage.

    public boolean checkID(int iD){ return iD == this.iD; }
    //^ Predicate method.
    //^ Used to select item by ID.
    //^ More secure than just giving 'this.codeID'.

    public Map<String, String> render(){
         //* For both user and owner/admin - seeing what the vending machine offers.
         return Map.of(
             "name",this.name,
             /// "type",this.type.toString(),
             "ID", Integer.toString(this.iD),
             "Price", Double.toString(this.price));
    }
}
