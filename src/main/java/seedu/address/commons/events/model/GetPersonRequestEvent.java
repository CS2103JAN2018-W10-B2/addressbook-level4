package seedu.address.commons.events.model;

import seedu.address.commons.events.BaseEvent;

//@@author XavierMaYuqian
/**
 *  Event to request current list of employees
 */
public class GetPersonRequestEvent extends BaseEvent {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
