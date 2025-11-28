# COMP7007 - Assignment 1 (Vending Machine) documentation

Student username - jrh1

NOTICE - JAVA JDK VERSION 22 IS REQUIRED TO COMPILE AND RUN THIS PROGRAM, PLEASE ENSURE YOU HAVE IT INSTALLED.

## Introduction

This program models a vending machine (and the user roles that interact with it) that meets the requirements specified in the assignment brief. It is designed with extensibility, maintainability, and robustness in mind, utilizing various design patterns to achieve these goals.

## Contents

- [Introduction](#introduction)
- [List of all involved classes, why they are here, and what they do.](#list-of-all-involved-classes-why-they-are-here-and-what-they-do)
- [Design pattern choices](#design-pattern-choices)
- [Satisfying requirements](#satisfying-requirements)
- [Code design choices](#code-design-choices)
- [Satisfied coding fundamentals](#satisfied-coding-fundamentals)

## List of all involved classes, why they are here, and what they do.
Deeper explanations are in the source code comments.
### Interfaces
- ActionsAdmin - represents all admin actions that can be performed on the vending machine.
- ActionsCustomer - represents all customer actions that can be performed on the vending machine.
### Enumerations
- CoinGBP - represents British pound sterling coin denominations and their values.
- ItemType - represents different types of items available in the vending machine.
- VendingMachineState - represents the different states of the vending machine. Used for the vending machine's state pattern.
### Abstract class
- Item - represents a generic item in the vending machine.
### Concrete classes
#### Observer pattern classes
- AdminDisplay - represents the admin's display interface for receiving updates from the vending machine.
- CustomerDisplay - represents the customer's display interface for receiving updates from the vending machine.
#### Proxy pattern classes (also observables for observer pattern)
- AdminProxy - represents the admin user role that interacts with the vending machine.
- CustomerProxy - represents the customer user role that interacts with the vending machine.
#### Leaf classes (in composite pattern, not the terminal kind)
- CoinStorage - represents the coin storage system of the vending machine.
- ItemStorage - represents the item storage system of the vending machine.
- ItemSlot - represents a single item slot in the vending machine.
#### Child classes (instances made from the factory method pattern class)
- Drink - represents a drink item in the vending machine.
- Snack - represents a snack item in the vending machine.
- MiscellaneousItem - represents a miscellaneous item in the vending machine - example use of extensibility.
#### State pattern class
- VendingMachine - represents the vending machine hardware itself.
#### Factory method pattern and singleton pattern classes (2 in 1 patterns)
- ItemFactory - represents the factory for creating items in the vending machine.
- VendingMachineFactory - represents the factory for creating vending machine instances.
#### Command pattern class
- Main (as command pattern conceptually only) - Simulates and tests the vending machine's capabilities, features, and robustness. 

## Design pattern choices
Due to the complexity of the assignment brief's requirements, they are divided into 8 requirements with each one having a uniquely different, but most suitable, design pattern assigned to them.

| Requirement                                     | Recommended Design Pattern                   | Category    | Why                                                                                  |
|-------------------------------------------------|----------------------------------------------|-------------|--------------------------------------------------------------------------------------|
| Vending machine states                          | State Pattern                                | Behavioural | Clean separation of state logic and prevention of unexpected behaviour               |
| Creating physically different vending machines  | Factory Method Pattern and Singleton Pattern | Creational  | Creates vending machine types cleanly with validation                                |
| Items (drinks and snacks)                       | Factory Method Pattern and Singleton Pattern | Creational  | Creates item types cleanly with validation                                           |
| Owner/Admin and customer permission role system | Proxy Pattern                                | Structural  | Enforces role-based access control (RBAC) to the vending machine for security        |
| Displaying updates                              | Observer Pattern                             | Behavioural | Decouples UI (specifically CUI) from logic                                           |
| Coin payment storage, handling and payment      | Composition Pattern                          | Structural  | Enforces Single Responsibility Principle (SRP)                                       |
| Item storage and item slots                     | Composition Pattern                          | Structural  | Enforces Single Responsibility Principle (SRP)                                       |
| Testing (in main class)                         | Command Pattern                              | Behavioural | Enables audit/test simulation to show program's capabilities/features and robustness |

## Satisfying requirements
The program satisfies the following requirements as per the assignment brief:

- The machine accepts British pound sterling in the form of coins - implemented using the CoinGBP enumeration.
- The machine contains a variety of drinks and snacks - implemented using the Item abstract class and its subclasses (Drink, Snack, Miscellaneous) created via the Factory Method pattern.
- Users may deposit coins into the machine - implemented as the 'CoinStorage.deposit' method.
- Once a sufficient amount of money has been deposited, users may with
draw a drink or snack of their choice - implemented by:
  - Automatic state transition is handled in the 'CustomerProxy.depositCoin' method.
  - Item dispensing (once triggered) is handled in the 'ItemStorage.dispenseItem' method.
- Users may cancel their purchase and withdraw the money they have deposited - implemented in the 'CustomerProxy.cancelOrder' method (during when customer is selecting items or paying).
- Once a drink or snack has been purchased, the remaining change is returned to the user - implemented in 'CustomerProxy.processCoinWithdrawal' helper method.
- The owner of the vending machine can add new contents and deposit/withdraw money arbitrarily, but ordinary users cannot - implemented exclusively in the various public methods of 'AdminProxy'.
- The vending machine has a small screen that displays text notifications for each action the machine makes - implemented using Observer design pattern 'AdminDisplay' and 'CustomerDisplay'.
- Multiple variations on the vending machine will be produced in the future, some of which may be larger or smaller and stock a different variety of options - implemented via the vending machine's constructor parameters (item slots and coin storage capacity - see the source code for full details).

The program works by allowing interaction with the vending machine (via 2 proxies - admin and customer). Such interactions align with what it mentioned in the assignment brief too; those interactions being:  

- Purchasing
  - Users may deposit coins - handled in 'CustomerProxy.depositCoin' method.
  - Users may request a refund of coins - handled in 'CustomerProxy.cancelOrder' method.
  - Users may retrieve coins from the refund bucket - handled in 'CustomerProxy.processCoinWithdrawal' method.
  - Users may retrieve a purchased item - handled in 'ItemStorage.dispenseItem' method.
- Administering
  - Owners may add items to the machine - handled in the 'AdminProxy.stockItems' method.
  - Owners may deposit or withdraw money from the machine - handled in 'AdminProxy.depositCoins' and 'AdminProxy.withdrawCoins' methods.

## Code design choices
Readers must know that most code design choices are explained as code comments in the source code itself. Please refer there for a deeper understanding of the code design choices.

### Enforcing immutability and private members
To improve code safety and make sure that any extensibility (not modifications) does not cause unintended side effects, the program enforces immutability and encapsulation where possible. This includes using the 'private' access modifier (hence no public field being declared in this project) for encapsulation,  and the 'final' keyword (to control what can and cannot change) for class fields and methods where appropriate in the majority of the classes (and all concrete classes) in this project.

This gives the additional benefit of preventing unauthorized access that may cause modification of critical data or unexpected behaviour - ensuring robustness and security. This is especially the case in proxies ('CustomerProxy' and 'AdminProxy') classes where security, permitting what user can do what action and restricted from doing, is paramount.

### Paying with different coins
The best design pattern for the coin pattern would be the strategy pattern but due to coins being a fixed data type, instead of being behavioural, enumeration is better suited.

Other enumerations are also used for extensibility (easy to add new states or remove coins) and avoids string-based errors.

Requirements for this assignment only mention coins of being the only payment methods thus it was made so.

Extensibility: If developer really want to, they can add more coins like shillings by adding them (with their respective values) to the 'CoinGBP' enum, even though it will not be practically needed.

### Client interaction
An interpreter pattern would be used for the client's interaction with the vending machine; however, because the program cannot currently be tested on actual vending machine hardware, the interpreter pattern is therefore made redundant.

### Items
While it may be simpler to use Records to store items, instead of classes; snack and drink have varying fields and inherits item hence defining the as child classes are better suited.

Also, the author do not advise to make the item (parent class) abstract; this is because the owner may decide to add a new, but also miscellaneous, kind of item that is neither a drink nor snack, such as a wooden utensils pack. But the author does acknowledge the importance of an abstraction, such as abstract methods and enforced method overriding. Therefore, the 'Miscellaneous' subclass will be used to allow the creation of miscellaneous items without compromising the abstract nature of the 'Item' class.

'Miscellaneous' subclass has been implemented, in this project, as an example of utilizing the program's extensibility.

To Enforce Single Responsibility Principle (SRP), a separate class (stored in the vending machine object) is dedicated to the stored items inside.

In a vending machine, more than one item slot can hold the same item kind (used for more popular items). This makes Maps unsuitable as the item class cannot be the key (because it forbids duplicate keys) and neither will Arrays because they cannot hold more than one data type - cannot hold item object and its amount in one sub-array. To get around this, composite pattern is used where each object stored the item and data and behaviour, allowing the slots to be stored in a simple final array.

### this.[]
The 'this.' keyword is used across the codebase to improve code readability and avoid confusion between local variables and class fields. However, it is still used (in this project) without same-name class fields to improve code readability by making it clear that the variable is being used within the context of the current object.

### Extensibility considerations
In the program files, there are many comments that indicate points of extensibility. These comments are placed in strategic locations to guide future developers on where and how they can extend the program's functionality without modifying existing code.

To ensure extensibility, the program is designed with 2 key principles in mind:

- Loose Coupling - Components being independent of each other, allows changes in one part of the system without affecting others. Such example is the user role proxies being loosely coupled with the vending machine, allowing for easy addition of new user roles without modifying the core vending machine logic.

- SRP - Each class has a single responsibility. While loose coupling allows easy modification in only the places of interest, and not affecting other places, SRP makes the code more modular that allows easier understanding of what to extend and where to extend it. For example, coinGBP enum is solely responsible for defining coin types and their values, making it straight forward to add new coin types without impacting other parts of the system.

### Java's built-in observer/observable
This program uses Java's built-in observer/observable classes to implement the observer pattern. This is because they provide a simple and effective way to implement the observer pattern without having to write custom code for it (not having to re-invent the wheel). However, its main advantage its standardised and well-documented nature in the Java industry, making it easier for other developers to understand and hence able to extend the program, by making and adding new observer pattern classes, without compatibility issues. 

These advantages outweigh the disadvantages of using deprecated classes.

### Use of string builders
String builders are used in the display observers to build up a larger multi-line string, comprised of smaller appended strings, before rendering it to the use (via console output) all at once. String builders are used over string concatenation for much better code readability and better performance (as the char[] array is both mutable and resizable, unlike Strings, which saves of memory and processing) that scales with more larger texts.

StringBuilder's additional functionality (like inserting at specific index, deleting substrings, replacing substrings, etc.) makes it perfect for extendibility - allowing future developers to easily modify the display (observer) output without having to rewrite large portions of code.

### Prediction vs knowing stocked items
This program is made to communicate with the vending machine's hardware, which inherits its hardware limitations. Such limitation include the vending machine predicting what items the stocked items are, based on what item slot it is stocked into, but does not know if admin accidentally stock the incorrect items such as stocking Coke drinks into the salted crisps snack slot. This limitation is therefore reflected in the program design (the vending machine class not having any methods to check if the stocked items are correct or not) and makes admin responsible for stocking the correct items into the correct slots.

### Handling payment
Due to the complexity of coin payment and refunding (underflow and overflow error handling, depositing and refunding, representing the physical coin storage, fetching statistics, checking supported coin denominations, etc), it is better to separate this functionality into a leaf class of the vending machine to enforce SRP.

### Factory method over abstract factory
While abstract factory pattern is more powerful than factory method pattern, the vending machine only requires one family of items (drinks and snacks). Therefore, the factory method pattern is more suited as it is only consists of one API method and a few small helper methods.

Also because one factory class instance is ever needed to make all items (per program run), the factory method pattern is also made as a singleton to avoid unnecessary multiple instances.

### Polymorphism over generics
Not only does polymorphism improve code readability by avoiding large switch-statements and/or if-statements, it also improves extensibility by allowing new item types, states, commands, observers and proxies to be added without modifying existing code.

This can also be called runtime polymorphism  - allowing different item types (drink, snack, miscellaneous) to be treated uniformly as item type at runtime. At the same time, the use of the algorithm skeleton, to keep code concise and avoid code duplication, makes use of the template method.

### Admin exclusive loops
When looking between the admin and customer proxies, some admin commands (like for depositing items and coins) uses loops to process multiple vending machine actions. However, such action loops are not found in the customer commands - customers commands only doing one action each. This is because admin actions are usually done in bulk (like restocking multiple items or depositing multiple coins) whereas customers usually only do one action at a time (like buying one item or inserting one coin at a time). Therefore, to improve code readability and avoid confusion and trouble to admins, such loops are only found in the admin proxy commands.

### testing via commands
Testing is permitted to do be done in the main class, thus it is done so where it calls various proxy commands - making use of the command design pattern in Main for testing purposes. Everything is thoroughly tested here to show the program's capabilities, features and robustness. This includes admin and customer interactions, handling user-errors (misinputs, invalid actions, etc.), state transitions, and security against malicious actions.

Reader must note that the main class is only conceptually a command pattern implementation; it does not strictly follow the command pattern structure/behaviour as it is not necessary for the testing part of this project.

### Responsibility and importance of proxies
Proxies are important in this program as they enforce role-based access control (RBAC) to the vending machine. By using proxies, the program can ensure that only authorized users (admins) can perform certain actions (like restocking items, withdrawing coins in bulk, checking statistics, etc.) while regular customers can only perform actions that are allowed for them (like making orders and paying). This not only improves security by limiting the capabilities of each user role (admin and user), but also improves code readability and maintainability by separating the responsibilities of each user role into different proxies which is why admin and customer proxies do not share a common interface.

### Admin and customer separation (no common interface)
SRP dictates that each class should have one responsibility. Therefore, to enforce SRP, the admin and customer proxies do not share a common interface because they have different responsibilities and capabilities. The admin proxy is responsible for managing the vending machine (restocking items, withdrawing coins in bulk, checking statistics, etc.), while the customer proxy is responsible for making orders and paying. By separating these responsibilities into different proxies, the code not only becomes more modular and easier to maintain and extend (such as adding a restocker-only role), but also improves security by limiting the capabilities of each user role (admin and user).

### Error handling over error prevention
While error prevention is ideal, it is not always possible due to the unpredictability of user behaviour and external factors (such as inserting coin getting jammed). Therefore, this program focuses on robust error handling to ensure that the program can gracefully handle unexpected situations and continue to function correctly. This includes handling invalid user (customer and admin) inputs, insufficient funds, out-of-stock items, and other potential errors that may arise during the operation of the vending machine. By implementing comprehensive error handling, the program can provide a better user experience and maintain its integrity even in the face of unexpected challenges.

This is reflected in the simulation of various user-errors (mistakes, invalid inputs, malicious actions, etc.) in the main class to demonstrate the program's robustness.

### Extensibility over modification
Where possible, the program is designed to be extensible rather than modifiable. Extensibility has already been explained in this README document; but reader must note modification is not only ignored but also discouraged. This is reflected in the style of programming; such example of this is the use of single-line
loop and if-statements (single-line if the statement is not too long) without braces which trades less modification encouragement for better readability.

## Satisfied coding fundamentals
Basically an overview of this document. Reader is heavily advised to read the entire document for deeper understanding.
- Encapsulation: well covered across all components.
- Abstraction: achieved via interfaces/abstract classes for State, Item, Command, Proxy, Observer.
- Inheritance and Polymorphism: strong for State, Item/Factory, Command, Observer, Proxy.
- Open/Closed Principle: design supports extension (add new states, item types, observers, commands) without modifying existing classes.
- Single Responsibility Principle: each class has a focused responsibility (e.g., CoinStorage, ItemStorage, Proxies).
- Loose Coupling: components interact via interfaces, minimizing dependencies.
- Robust Error Handling: comprehensive checks and exception handling for invalid inputs and states.
- Readability and Maintainability: clear naming conventions, consistent formatting, and thorough comments.
- Extensibility: designed to allow easy addition of new features without altering existing code.
- Security: role-based access control via proxies to restrict actions based on user roles.
- Performance Considerations: Minimising use of iteration/recurrence where appropriate.
- Gang of Four: design patterns (creational, behavioural, and structural) used where applicable.
