# COMP7007 - Assignment 1 (Vending Machine) documentation

## Design pattern choices
Due to the complexity of the assignment brief's requirements, they are divided into 7 requirements with each one having a uniquely different, but most suitable, design pattern assigned to them.

| Requirement                                | Recommended Pattern | Category   | Why                                            |
|--------------------------------------------|---------------------|------------|------------------------------------------------|
| Vending machine states                     | State Pattern       | Behavioral | Clean separation of state logic                |
| Items (drinks and snacks)                  | Factory Pattern     | Creational | Creates item types cleanly                     |
| Owner and customer permission system       | Proxy Pattern       | Structural | Enforces role-based access                     |
| Display updates                            | Observer Pattern    | Behavioral | Decouples UI (specifically CUI) from logic     |
| Coin payment storage, handling and payment | Composition Pattern | Structural | Enforces Single Responsibility Principle (SRP) |
| Item storage                               | Composition Pattern | Structural | Enforces Single Responsibility Principle (SRP) |
| Action logging and testing                 | Command Pattern     | Behavioral | Enables audit/test simulation                  |

## Code design choices
### Paying with different coins
The best design pattern for the coin pattern would be the strategy pattern but due to coins being a fixed data type, instead of being behavioral, enumeration is better suited.

Other enumerations are also used for extensibility (easy to add new states or remove coins) and avoids string-based errors.
### Client interaction
An interpreter patter would be used for the client's interaction with the vending machine; however, because the program is only required to be tested and not used by actual clients, the interpreter pattern is made redundant.
### Items
While it may be simpler to use Records to store items, instead of classes; snack and drink have varying fields and inherits item hence defining the as child classes are better suited.

Also, the author do not advise to make the item (parent class) abstract; this is because the owner may decide to add a new, but also miscellaneous, kind of item that is neither a drink nor snack, such as a wooden utensils pack. But the author does acknowledge the importance of an abstraction, such as abstract methods and enforced method overriding. Therefore, the 'Miscellaneous' sub-class will be used to allow the creation of miscellaneous items without comprising the abstract nature of the 'Item' class.

To Enforce Single Responsibility Principle (SRP), a separate class (stored in the vending machine object) is dedicated to the stored items inside.

In a vending machine, more than one item slot can hold the same item kind (used for more popular items). This makes Maps unsuitable as the item class cannot be the key and neither will just Arrays because they cannot hold more than one data type - cannot hold item object and its amount in one sub-array. To get around this, composite pattern is used where each object stored the item and data and behaviour, allowing the slots to be stored in a simple final array.
### this.[]

### Handling payment
Due to being required for customer to be refunded/given back his/her coins if needed, coin storage and coin payment/refund are required to be separate component classes (enforces SRP).
## Coding fundamentals
- Encapsulation: well covered across all components.
- Abstraction: achieved via interfaces/abstract classes for State, Item, Command, Proxy, Observer.
- Inheritance and Polymorphism: strong for State, Item/Factory, Command, Observer, Proxy.
- Open/Closed Principle: design supports extension (add new states, item types, observers, commands) without modifying existing classes.

