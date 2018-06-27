package com.brinkbros;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;

public abstract class YearView extends Panel {

    private static final String TABLE_CONTAINER_ID = "tableContainer";
    private static final String MONTH_TABLE_ID = "monthTable";
    private static final String DAY_ROW_ID = "dayRow";
    private static final String DAY_NO_ID = "dayNo";
    private static final String EVENTS_CELL_ID = "events";
    private static final String EVENT_LINK_ID = "eventLink";
    private static final String EVENT_ID = "event";

    public abstract Properties getDBProps();

    public abstract SidePanel getSidePanel();

    private Calendar cal;

    public YearView(String id) {
        super(id);

        Calendar cal = Calendar.getInstance();

        WebMarkupContainer tableContainer = new WebMarkupContainer(TABLE_CONTAINER_ID);
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);
        DataView monthTable = new DataView(MONTH_TABLE_ID, new ListDataProvider(Arrays.asList(Month.values()))) {
            @Override
            protected void populateItem(Item item) {
                Month month = 
            }
        };
    }

    public enum Month {

        JANUARY("Januari"),
        FEBRUARY("Februari"),
        MARCH("Maart"),
        APRIL("April"),
        MAY("Mei"),
        JUNE("Juni"),
        JULY("Juli"),
        AUGUST("Augustus"),
        SEPTEMBER("September"),
        OCTOBER("Oktober"),
        NOVEMBER("November"),
        DECEMBER("December");

        private final String name;

        private Month(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
