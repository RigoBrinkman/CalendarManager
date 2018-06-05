package com.brinkbros;

import com.brinkbros.OverviewDate.ODWeek;
import com.brinkbros.OverviewDate.ODMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

public class Overview extends Panel {

    private ODMonth weekList;

    public Overview(String id) {
        super(id);

        add(new CalendarTable("tableRow", OverviewDate.getWeekList(2018, 5)));
    }

    private class CalendarTable extends DataView<ODWeek> {

        private CalendarTable(String id, ODMonth weekList) {
            super(id, new ListDataProvider(weekList));

            Overview.this.weekList = weekList;

        }

        @Override
        protected void populateItem(Item<ODWeek> item) {

            ODWeek dayList = item.getModelObject();

            item.add(new DataView<OverviewDate>("cells",
                    new ListDataProvider(dayList)) {

                        @Override
                        protected void populateItem(Item<OverviewDate> item) {
                            OverviewDate overviewDate = item.getModelObject();
                            item.add(new Label("dayNumber", Integer.toString(overviewDate.getCalendar().get(DAY_OF_MONTH))));
                            List<DateEvent> events = overviewDate.getEvents();
                            item.add(new DataView<DateEvent>("events", new ListDataProvider(events)) {

                                @Override
                                protected void populateItem(Item<DateEvent> item) {
                                    DateEvent event = item.getModelObject();
                                    Label label = new Label("event", event.getName());
                                    label.add(new AttributeModifier("style", new Model(event.getColor().getStyleAttr())));
                                    item.add(label);
                                }
                            });
                        }
                    }
            );
        }
    }
}
