package com.brinkbros.Events;

import com.brinkbros.DatabaseDummy;
import com.brinkbros.DateEvent;
import com.brinkbros.SidePanel;
import java.util.List;
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
    
    protected abstract SidePanel getSidePanel();
    private List<DateEvent> events;

    public Events(String id) {
        super(id);
        add(new Label(TEST_ID, new Model("HOOFD")));
        events = DatabaseDummy.getEvents(20);
        DataView<DateEvent> dView = new DataView<DateEvent>(EVENT_SPAN_ID, new ListDataProvider<>(events)){
            @Override
            protected void populateItem(Item<DateEvent> item) {
                DateEvent event = item.getModelObject();
                AjaxLink link = new AjaxLink(EVENT_ROW_ID){
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getSidePanel().changeDetails(target, event);
                    }
                };
                link.add(new Label(EVENT_NAME_ID, new Model(event.getName()))
                        .add(new AttributeModifier("style", new Model(event.getColor().getStyleAttr()))));
                link.add(new Label(SHORT_DESCRIPTION_ID, new Model("Short description for " + event.getName())));
                item.add(link);
                
            }
            
        };
        add(dView);
    }
    
    

}
