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
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

  public DeadlinesView(String id, Properties dbProps, SidePanel sidePanel, CalmanUser currentUser) {
    super(id);

    ArrayList<CalmanEvent> eventList = new ArrayList();
    try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps);
        Statement stmnt = conn.createStatement();
        ResultSet rslts = stmnt.executeQuery(
            "SELECT * FROM PF_EVENTS l "
                + "LEFT JOIN PF_OPT_DATES r on l.event_id = r.event_id "
                + "WHERE (l.status = 301 AND r.date_ah < CURDATE()) "
                + "OR (l.status = 302 AND r.date_dr < CURDATE()) "
                + "OR (l.status = 303 AND r.date_iz < CURDATE()) "
                + "OR (l.status < 306 AND l.end_date < CURDATE())"
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

        Label label = new Label(EVENT_ID, new Model(event.getTitle()));
        label.setOutputMarkupId(true);
        link.add(label);
        for (AttributeModifier am : event.getAttributeModifiers()) {
          label.add(am);
        }
      }
    };
    events.setOutputMarkupId(true);
    add(events);

  }

}
