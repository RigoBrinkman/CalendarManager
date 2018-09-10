package com.brinkbros.deadlinesview;

import com.brinkbros.CalmanEvent;
import com.brinkbros.CalmanUser;
import com.brinkbros.DatabaseConnector;
import com.brinkbros.SidePanel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

public class DeadlinesView extends Panel {

  private static final String EVENTS_ID = "events";
  private static final String EVENT_LINK_ID = "eventLink";
  private static final String EVENT_ID = "event";
  private static final String EVENT_DATES_ID = "eventDates";

  public DeadlinesView(String id, Properties dbProps, SidePanel sidePanel, CalmanUser currentUser) {
    super(id);

    ArrayList<CalmanEvent> eventList = new ArrayList();
    try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps);
        Statement stmnt = conn.createStatement();
        ResultSet rslts = stmnt.executeQuery(
            "SELECT * FROM PF_EVENTS l "
            + "LEFT JOIN PF_OPT_DATES r on l.event_id = r.event_id "
            + "WHERE (l.status = 301 AND r.date_ah <= CURDATE()) "
            + "OR (l.status = 302 AND r.date_dr <= CURDATE()) "
            + "OR (l.status = 303 AND r.date_iz <= CURDATE()) "
            + "OR (l.status < 306 AND l.end_date <= CURDATE())"
        )) {
      while (rslts.next()) {
        eventList.add(new CalmanEvent(
            rslts.getInt(1),
            DatabaseConnector.stringToCal(rslts.getString(2)),
            rslts.getString(3),
            rslts.getString(4),
            rslts.getInt(5),
            rslts.getInt(6),
            rslts.getInt(7),
            rslts.getInt(8),
            CalmanUser.getUser(rslts.getInt(9)),
            CalmanUser.getUser(rslts.getInt(10)),
            DatabaseConnector.stringToCal(rslts.getString(13)),
            DatabaseConnector.stringToCal(rslts.getString(14)),
            DatabaseConnector.stringToCal(rslts.getString(15))
        )
        );
      }

    } catch (SQLException ex) {
      throw new RuntimeException(ex.getMessage());
    }
    
    eventList.sort((event1, event2) -> event1.getCalendar().compareTo(event2.getCalendar()));

    ListView events = new ListView<CalmanEvent>(EVENTS_ID, eventList) {
      @Override
      protected void populateItem(ListItem<CalmanEvent> item) {
        CalmanEvent event = item.getModelObject();
        AjaxLink link = new AjaxLink(EVENT_LINK_ID) {
          @Override
          public void onClick(AjaxRequestTarget target) {
            try {
              sidePanel.changeDetails(target, event, currentUser);
            } catch (SQLException ex) {
              sidePanel.changeDetails(target, ex);
            }
          }
        };
        link.setOutputMarkupId(true);
        item.add(link);
        event.getAttributeModifiers().stream().forEach(am -> link.add(am));
        link.add(new AttributeAppender("style", "color: black"));

        Label label = new Label(EVENT_ID, new Model(event.getTitle()));
        label.setOutputMarkupId(true);
        link.add(label);

        RepeatingView dateLabels = new RepeatingView(EVENT_DATES_ID, new Model());
        dateLabels.setOutputMarkupId(true);
        link.add(dateLabels);

        Calendar today = Calendar.getInstance();
        
        Label dateLabel = new Label(dateLabels.newChildId(), new Model(
            event.getCalendar().get(Calendar.DAY_OF_MONTH)
            + "/"
            + event.getCalendar().get(Calendar.MONTH)
            + "/"
            + event.getCalendar().get(Calendar.YEAR)));
        dateLabel.setOutputMarkupId(true);
        if(event.getCalendar().before(today)){
          dateLabel.add(new AttributeModifier("style", "color: red; float: right; white-space: pre"));
        }
        dateLabels.add(dateLabel);
        
        if (event.hasExtraDeadlines()) {
          for (int i = 2; i >= 0; i--) {
            Label deadlineLabel = new Label(dateLabels.newChildId(), new Model(
                event.getDeadlines().get()[i].get(Calendar.DAY_OF_MONTH)
                + "/"
                + event.getDeadlines().get()[i].get(Calendar.MONTH)
                + "/"
                + event.getDeadlines().get()[i].get(Calendar.YEAR)
                + " "));
            deadlineLabel.setOutputMarkupId(true);
            if(event.getStatusInt() < 302 + i && event.getDeadlines().get()[i].before(today)){
              deadlineLabel.add(new AttributeModifier("style", "color: red; float: right; white-space: pre"));
            }
            dateLabels.add(deadlineLabel);
          }
        }

//        Label datesLabel
//            = event.getDeadlines().isPresent()
//                ? new Label(EVENT_DATES_ID, new Model(
//                        event.getDeadlines().get()[0].get(Calendar.DAY_OF_MONTH)
//                        + "/"
//                        + event.getDeadlines().get()[0].get(Calendar.MONTH)
//                        + "/"
//                        + event.getDeadlines().get()[0].get(Calendar.YEAR)
//                        + " "
//                        + event.getDeadlines().get()[1].get(Calendar.DAY_OF_MONTH)
//                        + "/"
//                        + event.getDeadlines().get()[1].get(Calendar.MONTH)
//                        + "/"
//                        + event.getDeadlines().get()[1].get(Calendar.YEAR)
//                        + " "
//                        + event.getDeadlines().get()[2].get(Calendar.DAY_OF_MONTH)
//                        + "/"
//                        + event.getDeadlines().get()[2].get(Calendar.MONTH)
//                        + "/"
//                        + event.getDeadlines().get()[2].get(Calendar.YEAR)
//                        + " "
//                        + event.getCalendar().get(Calendar.DAY_OF_MONTH)
//                        + "/"
//                        + event.getCalendar().get(Calendar.MONTH)
//                        + "/"
//                        + event.getCalendar().get(Calendar.YEAR)))
//                : new Label(EVENT_DATES_ID, new Model(
//                        event.getCalendar().get(Calendar.DAY_OF_MONTH)
//                        + "/"
//                        + event.getCalendar().get(Calendar.MONTH)
//                        + "/"
//                        + event.getCalendar().get(Calendar.YEAR)));
//        datesLabel.setOutputMarkupId(true);
//        link.add(datesLabel);
      }
    };
    events.setOutputMarkupId(true);
    add(events);

  }

}
