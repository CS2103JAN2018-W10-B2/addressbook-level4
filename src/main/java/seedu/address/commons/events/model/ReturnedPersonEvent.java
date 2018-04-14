package seedu.address.commons.events.model;

import javafx.collections.ObservableList;
import seedu.address.commons.events.BaseEvent;
import seedu.address.model.person.Person;

//@@author XavierMaYuqian
/**
 * Indicates employees list returned.
 */
public class ReturnedPersonEvent extends BaseEvent {

    private final ObservableList<Person> employees;

    public ReturnedPersonEvent(ObservableList<Person> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public ObservableList<Person> getEmployees() {
        return employees;
    }
}
