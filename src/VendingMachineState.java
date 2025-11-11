/**
 * States of the vending machine as part of the "state design pattern".
 * <p>
 * Description of each enum value is given as comments its scope.
 */
public enum VendingMachineState {

    //* Only stable states will be used - transient states will not be included
    //* as it cannot be simulated in current conditions.
    IDLE, //< No action done - waiting for new customer or admin.
    ORDERING, //< One or more items selected - waiting for more items selected or proceed to payment.
    PAYING, //< Order confirmed, now waiting for sufficient funds or cancellation.
    DISPENSING, //< Dispensing items.
    REFUNDING, //< Refunding coins.
    MAINTENANCE, //< Admin/owner only - restocking items or coins, or performing maintenance.

    //! No need a toString() method as enum's toString() is sufficient.
}
