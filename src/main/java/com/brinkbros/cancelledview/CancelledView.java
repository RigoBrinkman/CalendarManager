package com.brinkbros.cancelledview;

import com.brinkbros.CalmanEvent;
import com.brinkbros.CalmanUser;
import com.brinkbros.DatabaseConnector;
import com.brinkbros.SidePanel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class CancelledView extends Panel {

  private static final String EVENTS_ID = "events";
  private static final String EVENT_LINK_ID = "eventLink";
  private static final String EVENT_ID = "event";

  private final Properties dbProps;
  private final SidePanel sidePanel;

  public CancelledView(String id, Properties dbProps, SidePanel sidePanel, CalmanUser currentUser) {
    super(id);

    this.dbProps = dbProps;
    this.sidePanel = sidePanel;

    ArrayList<CalmanEvent> events = new ArrayList();
    try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps);
        ResultSet rslts = conn.createStatement().executeQuery("SELECT * FROM PF_EVENTS l "
            + "LEFT JOIN PF_OPT_DATES r "
            + "ON l.event_id = r.event_id "
            + "WHERE l.status = 307 "
            + "AND l.end_date > DATE_SUB(NOW(), INTERVAL 2 MONTH)")) {
      while (rslts.next()) {
        events.add(new CalmanEvent(
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
            DatabaseConnector.stringToCal(rslts.getString(15))));
      }

    } catch (SQLException ex) {
      throw new RuntimeException(ex.getMessage());
    }

    ListView eventList;
    eventList = new ListView<CalmanEvent>(EVENTS_ID, events) {
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
        Label label = new Label(EVENT_ID, event.getTitle());
        event.getAttributeModifiers().stream().forEach((am) -> {
          label.add(am);
        });
        link.add(label);
        item.add(link);
      }
    };
    eventList.setOutputMarkupId(true);
    add(eventList);
  }
  protected Properties getDbProps() {
    return dbProps;
  }

}
