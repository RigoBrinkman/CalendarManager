package com.brinkbros;

import static com.brinkbros.DateEvent.EventColor.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class DatabaseDummy {

    private static final List<DateEvent> events;

    static {
        events = new LinkedList();
        events.add(new DateEvent("Bestuursvergadering", new GregorianCalendar(2018, 4, 21), GREEN, 1));
        events.add(new DateEvent("Demo", new GregorianCalendar(2018, 4, 31), BLUE, 2));
        events.add(new DateEvent("Demo einde", new GregorianCalendar(2018, 4, 31), RED, 3));
        events.add(new DateEvent("Vaderdag", new GregorianCalendar(2018, 5, 17), YELLOW, 4));
        events.add(new DateEvent("event1", new GregorianCalendar(2018, 4, 27), RED, 5));
        events.add(new DateEvent("event2", new GregorianCalendar(2018, 4, 28), RED, 6));
        events.add(new DateEvent("event3", new GregorianCalendar(2018, 4, 29), RED, 7));
        events.add(new DateEvent("event4", new GregorianCalendar(2018, 5, 9), RED, 8));
        events.add(new DateEvent("event5", new GregorianCalendar(2018, 5, 10), RED, 9));
        events.add(new DateEvent("event6", new GregorianCalendar(2018, 5, 11), RED, 10));
        for(int i = 0; i < 10000; i++){
            events.add(new DateEvent("TestEvent", new GregorianCalendar(2018, 8, 8), YELLOW, i + 11));
        }

    }

    public static List<DateEvent> getEvents(Calendar start, Calendar end) {
        List<DateEvent> eventsInMonth = new LinkedList(events);
        ListIterator<DateEvent> li = eventsInMonth.listIterator();
        while (li.hasNext()) {
            DateEvent e = li.next();
            if (e.getCalendar().before(start)
                    || e.getCalendar().after(end)) {
                li.remove();
            }
        }
        return eventsInMonth;
    }
    
    public static List<DateEvent> getEvents(int amount){
        return new ArrayList(events).subList(0, amount);
    }
    
    public static String getDetails(int id){
        return "details for "+ id;
    }
}
