package com.brinkbros;

import static com.brinkbros.DateEvent.EventColor.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class DatabaseDummy {

    public static List<DateEvent> getEvents(int year, int month) {
        List<DateEvent> events = new ArrayList();
        if (year == 2018 && month == 4) {
            events.add(new DateEvent("Bestuursvergadering", new GregorianCalendar(2018, 4, 21), GREEN));
            events.add(new DateEvent("Demo", new GregorianCalendar(2018, 4, 31), BLUE));
            events.add(new DateEvent("Demo einde", new GregorianCalendar(2018, 4, 31), RED));
        } else if (year == 2018 && month == 5) {
            events.add(new DateEvent("Vaderdag", new GregorianCalendar(2018, 5, 17), YELLOW));
        }
        return events;
    }
}
