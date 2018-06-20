package com.brinkbros.addEvent;

import com.brinkbros.SidePanel;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

public abstract class AddEvent extends Panel {

    private static final String EVENT_FORM_ID = "eventForm";
    private static final String TITLE_FIELD_ID = "titleField";
    private static final String DESCRIPTION_AREA_ID = "descriptionArea";
    private static final String TYPE_GROUP_ID = "typeRadio";
    private static final String TYPE_RADIO_SPAN_ID = "typeRadioSpan";
    private static final String TYPE_RADIO_ID = "typeRadioChoice";
    private static final String TYPE_RADIO_LABEL_ID = "typeRadioLabel";
    private static final ArrayList<String> typeList = new ArrayList();
    static{
        typeList.add("Evenement");
        typeList.add("Dossier");
    }
    
    public abstract SidePanel getSidePanel();

    public AddEvent(String id) {
        super(id);
        Form form = new Form(EVENT_FORM_ID);
        form.add(new TextField(TITLE_FIELD_ID, new Model("Titel")));
        form.add(new TextArea(DESCRIPTION_AREA_ID, new Model("Beschrijving")));
        RadioGroup typeGroup = new RadioGroup(TYPE_GROUP_ID);
        DataView<String> typeChoices = new DataView<String>(TYPE_RADIO_SPAN_ID, new ListDataProvider(typeList)){
            @Override
            protected void populateItem(Item<String> item) {
                String type = item.getModelObject();
                Radio radio = new Radio(TYPE_RADIO_ID);
                radio.setOutputMarkupId(true);
                item.add(new Label(TYPE_RADIO_LABEL_ID, new Model(type)));
                item.add(radio);
            }
        };
        typeGroup.add(typeChoices);
        form.add(typeGroup);
        add(form);
    }

}
