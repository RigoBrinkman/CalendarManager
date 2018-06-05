package com.brinkbros;

import static com.brinkbros.DateEvent.EventColor.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DatabaseDummy {

    private static List<DateEvent> events;

    static {
        events = new ArrayList();
        //events.add(new DateEvent("Bestuursvergadering", new GregorianCalendar(2018, 4, 21), GREEN));
        //events.add(new DateEvent("Demo", new GregorianCalendar(2018, 4, 31), BLUE));
        //events.add(new DateEvent("Demo einde", new GregorianCalendar(2018, 4, 31), RED));
        events.add(new DateEvent("Vaderdag", new GregorianCalendar(2018, 5, 17), YELLOW));

    }

    public static List<DateEvent> getEvents(Calendar start, Calendar end) {
        return events;
    }
}
