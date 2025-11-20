/**
 * States of the vending machine as part of the "state design pattern".
 * <p>
 * Description of each enum value is given as comments its scope.
 */
public enum VendingMachineState {
    //* All stable states will be used - only some transient states will be included.
    //* Stable states prevent unpermitted actions from causing unexpected behaviour.
    //* Transient states prevent unpermitted actions from 2 proxies acting at the same time.
    //: Stable states.
    IDLE, //< No action done - waiting for new customer or admin.
    ORDERING, //< One or more items selected - waiting for more items selected or proceed to payment.
    PAYING, //< Order confirmed, now waiting for sufficient funds or cancellation.
    MAINTENANCE, //< Admin/owner only - restocking items or coins, or performing maintenance.
    //: Transient states.
    DISPENSING, //< Dispensing items.
    REFUNDING, //< Refunding coins.

    //! No need a toString() method as enum's default toString() is sufficient.
}
