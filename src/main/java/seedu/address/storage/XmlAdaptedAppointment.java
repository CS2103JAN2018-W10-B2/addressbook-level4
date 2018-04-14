//@@author ongkuanyang
package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.AppointmentName;
import seedu.address.model.appointment.AppointmentTime;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * JAXB-friendly version of the Appointment.
 */
public class XmlAdaptedAppointment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Appointment's %s field is missing!";

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String time;
    @XmlElement
    private List<XmlAdaptedPerson> registered = new ArrayList<>();

    /**
     * Constructs an XmlAdaptedAppointment.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedAppointment() {}

    /**
     * Constructs an {@code XmlAdaptedAppointment} with the given appointment details.
     */
    public XmlAdaptedAppointment(String name, String time, String email, List<XmlAdaptedPerson> registered) {
        this.name = name;
        this.time = time;
        if (registered != null) {
            this.registered = new ArrayList<>(registered);
        }
    }

    /**
     * Converts a given Appointment into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedAppointment
     */
    public XmlAdaptedAppointment(Appointment source) {
        name = source.getName().name;
        time = source.getTime().toString();
        registered = new ArrayList<>();
        for (Person person : source.getPersons()) {
            registered.add(new XmlAdaptedPerson(person));
        }
    }

    /**
     * Converts this jaxb-friendly adapted appointment object into the model's Appointment object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted appointment
     */
    public Appointment toModelType() throws IllegalValueException {
        final UniquePersonList appointmentPersons = new UniquePersonList();
        for (XmlAdaptedPerson person : registered) {
            appointmentPersons.add(person.toModelType());
        }

        if (this.name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!AppointmentName.isValidName(this.name)) {
            throw new IllegalValueException(AppointmentName.MESSAGE_NAME_CONSTRAINTS);
        }
        final AppointmentName name = new AppointmentName(this.name);

        if (this.time == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                        AppointmentTime.class.getSimpleName()));
        }
        if (!AppointmentTime.isValidTime(this.time)) {
            throw new IllegalValueException(AppointmentTime.MESSAGE_TIME_CONSTRAINTS);
        }
        final AppointmentTime time = new AppointmentTime(this.time);

        Appointment appointment = new Appointment(name, time, appointmentPersons);

        return appointment;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedAppointment)) {
            return false;
        }

        XmlAdaptedAppointment otherAppointment = (XmlAdaptedAppointment) other;
        return Objects.equals(name, otherAppointment.name)
                && Objects.equals(time, otherAppointment.time)
                && registered.equals(otherAppointment.registered);
    }
}
//@@author
