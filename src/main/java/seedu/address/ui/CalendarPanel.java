//@@author ongkuanyang
package seedu.address.ui;

import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.google.common.eventbus.Subscribe;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.model.appointment.Appointment;

/**
 * The Browser Panel of the App.
 */
public class CalendarPanel extends UiPart<Region> {

    private static final String FXML = "CalendarPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @FXML
    private CalendarView calendarView;

    private ObservableList<Appointment> appointmentList;

    private Calendar calendar;

    public CalendarPanel(ObservableList<Appointment> appointmentList) {
        super(FXML);
        registerAsAnEventHandler(this);

        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.showMonthPage();

        // Set CSS
        String fullPath = getClass().getResource("/view/calendar.css").toExternalForm();
        calendarView.getStylesheets().removeAll();
        calendarView.getStylesheets().add(fullPath);

        calendar = new Calendar("Appointments");
        calendar.setReadOnly(true);

        CalendarSource myCalendarSource = new CalendarSource("My Calendars");
        myCalendarSource.getCalendars().addAll(calendar);
        calendarView.getCalendarSources().addAll(myCalendarSource);

        this.appointmentList = appointmentList;
        syncAppointments();
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        calendarView = null;
    }

    /**
     * Switch to dark theme
     */
    public void switchDarkTheme() {
        Blend blendEffect = new Blend(BlendMode.DIFFERENCE);
        ColorInput colorInput = new ColorInput(0, 0, 2000, 2000, Color.gray(1.0));
        blendEffect.setTopInput(colorInput);
        calendarView.setEffect(blendEffect);
    }

    /**
     * Switch to light theme
     */
    public void switchLightTheme() {
        calendarView.setEffect(null);
    }

    /**
     * Synchronises the appointments and calendar entries
     */
    private void syncAppointments() {
        calendar.clear();
        for (Appointment appointment : appointmentList) {
            Entry<String> entry = new Entry(appointment.getName().name);
            entry.setInterval(appointment.getTime().time);
            entry.setLocation(appointment.getTime().time.format(DateTimeFormatter.ofPattern("VV")));
            calendar.addEntry(entry);
        }
    }

    @Subscribe
    private void handleAddressBookChangedEvent(AddressBookChangedEvent event) {
        syncAppointments();
    }
}
