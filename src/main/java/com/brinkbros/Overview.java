package com.brinkbros;

import com.brinkbros.OverviewDate.DayList;
import com.brinkbros.OverviewDate.WeekList;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

public class Overview extends Panel {

    private WeekList weekList;

    public Overview(String id) {
        super(id);

        add(new CalendarTable("tableRow", OverviewDate.getWeekList(2018, 4)));
//        add(new ButtonForm("buttons"));
    }

    private class CalendarTable extends DataView<DayList> {

        private CalendarTable(String id, WeekList weekList) {
            super(id, new ListDataProvider(weekList));

            Overview.this.weekList = weekList;

        }

        @Override
        protected void populateItem(Item<DayList> item) {

            DayList dayList = item.getModelObject();

            item.add(new DataView<OverviewDate>("cells",
                    new ListDataProvider(dayList)) {

                        @Override
                        protected void populateItem(Item<OverviewDate> item) {
                            OverviewDate overviewDate = item.getModelObject();
                            item.add(new Label("dayNumber", Integer.toString(overviewDate.getDay())));
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

//    private class ButtonForm extends Form {
//
//        public ButtonForm(String id) {
//            super(id);
//            add(new Button("previous", new Model("vorige maand")) {
//                @Override
//                public void onSubmit() {
//                    Overview.this.remove("tableRow");
//                    Overview.this
//                            .add(new CalendarTable(
//                                            "tableRow",
//                                            OverviewDate
//                                            .getPreviousWeekList(
//                                                    Overview.this.weekList.getYear(),
//                                                    Overview.this.weekList.getMonth())));
//                }
//            });
//            add(new Button("next", new Model("volgende maand")) {
//                @Override
//                public void onSubmit() {
//                    Overview.this.remove("tableRow");
//                    Overview.this
//                            .add(new CalendarTable(
//                                            "tableRow",
//                                            OverviewDate
//                                            .getNextWeekList(
//                                                    Overview.this.weekList.getYear(),
//                                                    Overview.this.weekList.getMonth())));
//                }
//            });
//        }
//
//    }

}
