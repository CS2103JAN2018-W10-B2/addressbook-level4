package seedu.address.commons.events.model;

import seedu.address.commons.events.BaseEvent;

//@@author XavierMaYuqian
/**
 * Indicates password cahnged
 * */
public class PasswordChangedEvent extends BaseEvent {

    public final String password;

    public PasswordChangedEvent(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "timetable entry added: " + this.password;
    }
}
