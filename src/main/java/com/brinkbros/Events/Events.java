package com.brinkbros.Events;

import com.brinkbros.DatabaseConnector;
import com.brinkbros.DateEvent;
import com.brinkbros.SidePanel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

public abstract class Events extends Panel {

    private static final String TEST_ID = "TEST";
    private static final String EVENT_NAME_ID = "eventName";
    private static final String EVENT_SPAN_ID = "events";
    private static final String EVENT_ROW_ID = "eventRow";
    private static final String SHORT_DESCRIPTION_ID = "shortDescription";

    protected abstract Properties getDBProps();

    protected abstract SidePanel getSidePanel();
    private List<DateEvent> events;

    public Events(String id) {
        super(id);
        add(new Label(TEST_ID, new Model("HOOFD")));
        events = new ArrayList();
        try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), getDBProps());
                ResultSet rslts = DatabaseConnector.select(conn, DatabaseConnector.Table.EVENTS)) {
            while (rslts.next()) {
                events.add(new DateEvent(
                        rslts.getInt(1), //id
                        DatabaseConnector.stringToCal(rslts.getString(2)), //calendar
                        rslts.getString(3), //title
                        rslts.getString(4), //description
                        rslts.getInt(5), //category
                        rslts.getInt(6), //type
                        rslts.getInt(7) //status
                ));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        DataView<DateEvent> dView = new DataView<DateEvent>(EVENT_SPAN_ID, new ListDataProvider<>(events)) {
            @Override
            protected void populateItem(Item<DateEvent> item) {
                DateEvent event = item.getModelObject();
                AjaxLink link = new AjaxLink(EVENT_ROW_ID) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            getSidePanel().changeDetails(target, event);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex.getMessage());
                        }
                    }
                };
                link.add(new Label(EVENT_NAME_ID, new Model(event.getName()))
                        .add(new AttributeModifier("style", new Model(event.getColor().getStyleAttr()))));
                item.add(link);

            }

        };
        add(dView);
    }

}
