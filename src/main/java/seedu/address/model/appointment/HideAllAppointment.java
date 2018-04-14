package seedu.address.model.appointment;

import java.util.function.Predicate;

//@@author XavierMaYuqian
/**
 * HideAllPerson
 */
public class HideAllAppointment implements Predicate<Appointment> {

    public HideAllAppointment() {}

    @Override
    public boolean test(Appointment appointment) {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }

}
