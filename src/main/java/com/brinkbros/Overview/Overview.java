package com.brinkbros.Overview;

import com.brinkbros.DatabaseConnector;
import com.brinkbros.DateEvent;
import com.brinkbros.SidePanel;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

public abstract class Overview extends Panel {

    private static final int THIS_MONTH;
    private static final int THIS_YEAR;

    private static final String MONTHNAME_ID = "monthName";
    private static final String TABLE_CONTAINER_ID = "tableContainer";
    private static final String TABLE_ID = "table";
    private static final String CELL_ID = "cell";
    private static final String DAY_ID = "dayNumber";
    private static final String EVENTS_ID = "events";
    private static final String EVENT_ID = "event";
    private static final String EVENT_LINK_ID = "eventLink";
    private static final String PREVIOUS_ID = "previousButton";
    private static final String PREVIOUS_VALUE = "Vorige";
    private static final String GOTO_NOW_ID = "nowButton";
    private static final String GOTO_NOW_VALUE = "Huidge Maand";
    private static final String NEXT_ID = "nextButton";
    private static final String NEXT_VALUE = "Volgende";

    protected abstract Properties getDBProps();

    protected abstract SidePanel getSidePanel();

    static {
        Calendar cal = Calendar.getInstance();
        THIS_MONTH = cal.get(MONTH);
        THIS_YEAR = cal.get(YEAR);
    }
    private int month;
    private int year;

    private CalendarTable table;

    public Overview(String id) {
        super(id);
        this.month = THIS_MONTH;
        this.year = THIS_YEAR;
        Label monthName = new Label(MONTHNAME_ID, new Model(getMonthName()));
        monthName.setOutputMarkupId(true);
        add(monthName);

        WebMarkupContainer tableContainer = new WebMarkupContainer(TABLE_CONTAINER_ID);
        table = new CalendarTable(TABLE_ID, new ODMonth(year, month, getDBProps()));
        table.setOutputMarkupId(true);
        tableContainer.add(table);
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        add(new AjaxLink(PREVIOUS_ID) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (month == 0) {
                    month = 11;
                    year--;
                } else {
                    month--;
                }
                monthName.setDefaultModelObject(getMonthName());
                table = new CalendarTable(TABLE_ID, new ODMonth(year, month, getDBProps()));
                tableContainer.replace(table);
                target.add(tableContainer);
                target.add(monthName);

            }

            @Override
            public void renderHead(IHeaderResponse response) {
                response
                        .render(OnDomReadyHeaderItem
                                .forScript(new StringBuilder()
                                        .append("document.getElementById(\"")
                                        .append(getMarkupId())
                                        .append("\").value=\"")
                                        .append(PREVIOUS_VALUE)
                                        .append("\";")
                                        .toString()));
                super.renderHead(response);
            }

        }).setOutputMarkupId(true);

        add(new AjaxLink(GOTO_NOW_ID) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                month = THIS_MONTH;
                year = THIS_YEAR;
                monthName.setDefaultModelObject(getMonthName());
                table = new CalendarTable(TABLE_ID, new ODMonth(year, month, getDBProps()));
                tableContainer.replace(table);
                target.add(tableContainer);
                target.add(monthName);
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                response
                        .render(OnDomReadyHeaderItem
                                .forScript(new StringBuilder()
                                        .append("document.getElementById(\"")
                                        .append(getMarkupId())
                                        .append("\").value=\"")
                                        .append(GOTO_NOW_VALUE)
                                        .append("\";")
                                        .toString()));
                super.renderHead(response);
            }

        }
        );
        add(new AjaxLink(NEXT_ID) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (month == 11) {
                    month = 0;
                    year++;
                } else {
                    month++;
                }
                monthName.setDefaultModelObject(getMonthName());
                table = new CalendarTable(TABLE_ID, new ODMonth(year, month, getDBProps()));
                tableContainer.replace(table);
                target.add(tableContainer);
                target.add(monthName);
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                response
                        .render(OnDomReadyHeaderItem
                                .forScript(new StringBuilder()
                                        .append("document.getElementById(\"")
                                        .append(getMarkupId())
                                        .append("\").value=\"")
                                        .append(NEXT_VALUE)
                                        .append("\";")
                                        .toString()));
                super.renderHead(response);
            }

        });

    }

    private String getMonthName() {
        switch (month) {
            case 0:
                return "Januari " + String.valueOf(year);
            case 1:
                return "Februari " + String.valueOf(year);
            case 2:
                return "Maart " + String.valueOf(year);
            case 3:
                return "April " + String.valueOf(year);
            case 4:
                return "Mei " + String.valueOf(year);
            case 5:
                return "Juni " + String.valueOf(year);
            case 6:
                return "Juli " + String.valueOf(year);
            case 7:
                return "Augustus " + String.valueOf(year);
            case 8:
                return "September " + String.valueOf(year);
            case 9:
                return "Oktober " + String.valueOf(year);
            case 10:
                return "November " + String.valueOf(year);
            case 11:
                return "December " + String.valueOf(year);
            default:
                return "Onbekende maand " + String.valueOf(year);
        }
    }

    private class CalendarTable extends DataView<ODWeek> {

        private CalendarTable(String id, ODMonth weekList) {
            super(id, new ListDataProvider(weekList));

        }

        @Override
        protected void populateItem(Item<ODWeek> item) {

            ODWeek dayList = item.getModelObject();

            item.add(new DataView<OverviewDate>(CELL_ID,
                    new ListDataProvider(dayList)) {

                        @Override
                        protected void populateItem(Item<OverviewDate> item) {
                            OverviewDate overviewDate = item.getModelObject();
                            item.add(new Label(DAY_ID,
                                            String.valueOf(overviewDate.getCalendar().get(DAY_OF_MONTH)) + (overviewDate.isToday() ? " Vandaag" : "")));

                            List<DateEvent> events = overviewDate.getEvents();
                            item.add(new DataView<DateEvent>(EVENTS_ID, new ListDataProvider(events)) {

                                @Override
                                protected void populateItem(Item<DateEvent> item) {
                                    DateEvent event = item.getModelObject();
                                    AjaxLink link = new AjaxLink(EVENT_LINK_ID) {

                                        @Override
                                        public void onClick(AjaxRequestTarget target) {
                                            try {
                                                getSidePanel().changeDetails(target, event);
                                            } catch (SQLException ex) {
                                                getSidePanel().changeDetails(target, ex.getMessage());
                                            }
                                        }

                                    };
                                    Label label = new Label(EVENT_ID, event.getName());
                                    label.add(new AttributeModifier("style", new Model(event.getColor().getStyleAttr())));
                                    link.add(label);
                                    item.add(link);
                                }
                            });
                        }
                    }
            );
        }
    }
}
