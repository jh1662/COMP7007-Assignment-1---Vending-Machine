# COMP7007 - Assignment 1 (Vending Machine) documentation

## Design pattern choices
Due to the complexity of the assignment brief's requirements, they are divided into 6 requirements with each one having a uniquely different, but most suitable, design pattern assigned to them.

| Requirement                                | Recommended Pattern | Category   | Why                                            |
|--------------------------------------------|---------------------|------------|------------------------------------------------|
| Vending machine states                     | State Pattern       | Behavioral | Clean separation of state logic                |
| Items (drinks and snacks)                  | Factory Pattern     | Creational | Creates item types cleanly                     |
| Owner and customer permission system       | Proxy Pattern       | Structural | Enforces role-based access                     |
| Display updates                            | Observer Pattern    | Behavioral | Decouples UI (specifically CUI) from logic     |
| Coin payment storage, handling and payment | Composition Pattern | Structural | Enforces Single Responsibility Principle (SRP) |
| Action logging and testing                 | Command Pattern     | Behavioral | Enables audit/test simulation                  |

## Code design choices
### Paying with different coins
The best design pattern for the coin pattern would be the strategy pattern but due to coins being a fixed data type, instead of being behavioral, enumeration is better suited.
### Client interaction
An interpreter patter would be used for the client's interaction with the vending machine; however, because the program is only required to be tested and not used by actual clients, the interpreter pattern is made redundant.
### Items
While it may be simpler to use Records to store items, instead of classes, food and drink have varying behaviours hence defining the as child classes are better suited.

Also, it is not advised to make the item (parent class) abstract; this is because the owner may decide to add a new kind of item that is neither a drink nor food, such as a wooden utensils pack.
### Handling payment
Due to being required for customer to be refunded/given back his/her coins if needed, coin storage and coin payment/refund are required to be separate component classes (enforces SRP).
## Coding fundamentals
- Encapsulation: well covered across all components.
- Abstraction: achieved via interfaces/abstract classes for State, Item, Command, Proxy, Observer.
- Inheritance and Polymorphism: strong for State, Item/Factory, Command, Observer, Proxy.
- Open/Closed Principle: design supports extension (add new states, item types, observers, commands) without modifying existing classes.

