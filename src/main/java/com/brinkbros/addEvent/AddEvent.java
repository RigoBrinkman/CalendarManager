package com.brinkbros.addEvent;

import com.brinkbros.BasePage;
import com.brinkbros.DatabaseConnector;
import com.brinkbros.SidePanel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

public abstract class AddEvent extends Panel {

    private static final String EVENT_FORM_ID = "eventForm";
    private static final String DATE_FIELD_ID = "dateField";
    private static final String TITLE_FIELD_ID = "titleField";
    private static final String DESCRIPTION_AREA_ID = "descriptionArea";
    private static final String CATEGORY_GROUP_ID = "categoryRadio";
    private static final String TYPE_GROUP_ID = "typeRadio";
    private static final String STATUS_GROUP_ID = "statusRadio";
    private static final String SUBEVENT_LIST_ID = "subeventList";
    private static final String ADD_SUBEVENT_BUTTON_ID = "addSubeventButton";
    private static final String SUBMIT_BUTTON_ID = "subBttn";
    private static final ArrayList<String> categoryList = new ArrayList();
    private static final ArrayList<String> typeList = new ArrayList();
    private static final ArrayList<String> statusList = new ArrayList();

    static {
        categoryList.add("Categorie 1");
        categoryList.add("Categorie 2");
        categoryList.add("Categorie 3");
        categoryList.add("Categorie 4");

        typeList.add("Evenement");
        typeList.add("Dossier");

        statusList.add("In behandeling");
        statusList.add("Concept");
        statusList.add("On hold");
        statusList.add("Voltooid");
        statusList.add("Geannuleerd");
    }

    public abstract SidePanel getSidePanel();

    public abstract Properties getDBProps();

    private final String THIS_ID;
    private String title;
    private String description;
    private Date date;
    
    private List<Form> subeventList;

    public AddEvent(String id) {
        super(id);
        THIS_ID = id;
        this.title = "Titel";
        this.description = "Beschrijving";

        Form form = new Form(EVENT_FORM_ID, new Model(EVENT_FORM_ID));

        DateTextField dateField = new DateTextField(DATE_FIELD_ID, new PropertyModel<Date>(this, "date"), new StyleDateConverter("S-", true)) {
            @Override
            public String[] getInputTypes() {
                return new String[]{"date"};
            }
        };
        dateField.setOutputMarkupId(true);

        TextField<String> titleField = new TextField(TITLE_FIELD_ID, new PropertyModel(this, "title"));
        titleField.setOutputMarkupId(true);
        TextArea descriptionField = new TextArea(DESCRIPTION_AREA_ID, new Model(description));

        RadioChoice categoryGroup = new RadioChoice(CATEGORY_GROUP_ID, new Model(categoryList.get(0)), categoryList);
        RadioChoice typeGroup = new RadioChoice(TYPE_GROUP_ID, new Model(typeList.get(0)), typeList);
        RadioChoice statusGroup = new RadioChoice(STATUS_GROUP_ID, new Model(statusList.get(0)), statusList);

        Button submitButton = new Button(SUBMIT_BUTTON_ID, new Model("Versturen")) {
            @Override
            public void onSubmit() {
                setResponsePage(BasePage.class);
            }
        };
        submitButton.add((IValidatable<String> validatable) -> {
            try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), getDBProps())) {
                int eventID = DatabaseConnector.insertEvent(conn,
                        dateField.getInput(),
                        titleField.getInput(),
                        descriptionField.getInput(),
                        Integer.parseInt(categoryGroup.getInput()) + 101,
                        Integer.parseInt(typeGroup.getInput()) + 201,
                        Integer.parseInt(statusGroup.getInput()) + 301);
            } catch (SQLException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        });
        
        subeventList = new ArrayList();
        RepeatingView subEvents = new RepeatingView(SUBEVENT_LIST_ID);
        
        AjaxLink addSubevent = new AjaxLink(ADD_SUBEVENT_BUTTON_ID){

            @Override
            public void onClick(AjaxRequestTarget target) {
                
            }
        };
        
        

        

        form.add(dateField);

        form.add(titleField);

        form.add(descriptionField);

        form.add(categoryGroup);

        form.add(typeGroup);

        form.add(statusGroup);

        form.add(submitButton);

        add(form);
    }

    public static void main(String[] args) {
        System.out.println(reverseDate("12/06/2018"));
        System.out.println(DatabaseConnector.calToString(new GregorianCalendar(2018, 5, 12)));
    }

    private static String reverseDate(String input) {
        StringBuilder sb = new StringBuilder();
        sb
                .append(input.substring(6))
                .append('-')
                .append(input.substring(3, 5))
                .append('-')
                .append(input.substring(0, 2));
        return sb.toString();
    }

}
