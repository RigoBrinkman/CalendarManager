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
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentUpdatingBehavior;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

public abstract class AddEvent extends Panel {

    private static final String EVENT_FORM_ID = "eventForm";
    private static final String DATE_FIELD_ID = "dateField";
    private static final String TITLE_FIELD_ID = "titleField";
    private static final String DESCRIPTION_AREA_ID = "descriptionArea";
    private static final String CATEGORY_GROUP_ID = "categoryRadio";
    private static final String TYPE_GROUP_ID = "typeRadio";
    private static final String STATUS_GROUP_ID = "statusRadio";
    private static final String SUBEVENT_CONTAINER_ID = "subeventContainer";
    private static final String SUBEVENT_LIST_ID = "subeventList";
    private static final String SUBEVENT_ID = "subevent";
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
    public String title;
    private String description;
    private Date date;
    private RepeatingView subEvents;
    private List<SubeventForm> subeventList;

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

        TextField<String> titleField = new TextField(TITLE_FIELD_ID, new Model("Titel"));
        titleField.setOutputMarkupId(true);

        TextArea descriptionField = new TextArea(DESCRIPTION_AREA_ID, new Model(description));

        RadioChoice categoryGroup = new RadioChoice(CATEGORY_GROUP_ID, new Model(categoryList.get(0)), categoryList);
        RadioChoice typeGroup = new RadioChoice(TYPE_GROUP_ID, new Model(typeList.get(0)), typeList);
        RadioChoice statusGroup = new RadioChoice(STATUS_GROUP_ID, new Model(statusList.get(0)), statusList);

        subeventList = new ArrayList();
        subEvents = new RepeatingView(SUBEVENT_ID);
        subEvents.setOutputMarkupId(true);

        Button addSubeventButton = new Button(ADD_SUBEVENT_BUTTON_ID) {
            @Override
            public void onSubmit() {
                SubeventForm sef = new SubeventForm(subEvents.newChildId(), new Model(SUBEVENT_ID));
                sef.setOutputMarkupId(true);
                subEvents.add(sef);
                subeventList.add(sef);
                subEvents.add(sef);
                //target.add(form);
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                response
                        .render(OnDomReadyHeaderItem
                                .forScript(new StringBuilder()
                                        .append("document.getElementById(\"")
                                        .append(getMarkupId())
                                        .append("\").value=\"")
                                        .append("Nieuwe sub activiteit")
                                        .append("\";")
                                        .toString()));
                super.renderHead(response);
            }
        };
        addSubeventButton.setOutputMarkupId(true);
        //addSubeventButton.setDefaultFormProcessing(false);

        Button submitButton = new Button(SUBMIT_BUTTON_ID, new Model("Versturen")) {
            @Override
            public void onSubmit() {
                throw new RuntimeException("GOOd");
            }
            @Override
            public void onError(){
                getSession().invalidateNow();
            }
        };

        submitButton.add((IValidatable<String> validatable) -> {
            try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), getDBProps())) {
                int eventID = DatabaseConnector.insert(conn, DatabaseConnector.Table.EVENTS, new String[]{
                    dateField.getInput(),
                    titleField.getInput(),
                    descriptionField.getInput(),
                    String.valueOf(Integer.parseInt(categoryGroup.getInput()) + 101),
                    String.valueOf(Integer.parseInt(typeGroup.getInput()) + 201),
                    String.valueOf(Integer.parseInt(statusGroup.getInput()) + 301)});
                for (SubeventForm sef : subeventList) {
                    DatabaseConnector.insert(conn, DatabaseConnector.Table.SUBEVENTS, new String[]{
                        String.valueOf(eventID),
                        sef.getDate(),
                        sef.getTitle(),
                        sef.getDescription(),
                        String.valueOf(sef.getCategory()),
                        String.valueOf(sef.getType()),
                        String.valueOf(sef.getStatus())});
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        });

        form.add(dateField);
        form.add(titleField);
        form.add(descriptionField);
        form.add(categoryGroup);
        form.add(typeGroup);
        form.add(statusGroup);
        form.add(subEvents);
        form.add(addSubeventButton);
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

    private class SubeventForm extends Form {

        private static final String SUB_DATE_ID = "subDate";
        private static final String SUB_TITLE_ID = "subTitle";
        private static final String SUB_DESCRIPTION_ID = "subDescription";
        private static final String SUB_CATEGORY_ID = "subCategoryRadio";
        private static final String SUB_TYPE_ID = "subTypeRadio";
        private static final String SUB_STATUS_ID = "subStatusRadio";
        private static final String SUB_DELETE_ID = "deleteSubevent";

        private Date date;

        private DateTextField dateField;
        private TextField titleField;
        private TextArea descriptionField;
        private RadioChoice categoryRadio;
        private RadioChoice typeRadio;
        private RadioChoice statusRadio;

        public SubeventForm(String id, IModel model) {
            super(id, model);
            dateField = new DateTextField(SUB_DATE_ID, new PropertyModel<Date>(this, "date"), new StyleDateConverter("S-", true)) {
                @Override
                public String[] getInputTypes() {
                    return new String[]{"date"};
                }
            };
            dateField.setOutputMarkupId(true);

            titleField = new TextField(SUB_TITLE_ID, new Model("Titel"));
            titleField.setOutputMarkupId(true);

            descriptionField = new TextArea(SUB_DESCRIPTION_ID, new Model("Beschrijving"));
            descriptionField.setOutputMarkupId(true);

            categoryRadio = new RadioChoice(SUB_CATEGORY_ID, new Model(categoryList.get(0)), categoryList);
            typeRadio = new RadioChoice(SUB_TYPE_ID, new Model(typeList.get(0)), typeList);
            statusRadio = new RadioChoice(SUB_STATUS_ID, new Model(statusList.get(0)), statusList);

            Button deleteButton = new Button(SUB_DELETE_ID, new Model("Verwijder subActiviteit")) {
                @Override
                public void onSubmit() {
                    subeventList.remove(SubeventForm.this);
                    MarkupContainer form = SubeventForm.this.getParent().getParent();
                    SubeventForm.this.remove();
                }

                @Override
                public void renderHead(IHeaderResponse response) {
                    response
                            .render(OnDomReadyHeaderItem
                                    .forScript(new StringBuilder()
                                            .append("document.getElementById(\"")
                                            .append(getMarkupId())
                                            .append("\").value=\"")
                                            .append("Verwijder subActiviteit")
                                            .append("\";")
                                            .toString()));
                    super.renderHead(response);
                }
            };
            add(dateField);
            add(titleField);
            add(descriptionField);
            add(categoryRadio);
            add(typeRadio);
            add(statusRadio);
            add(deleteButton);
        }

        public String getDate() {
            return dateField.getInput();
        }

        public String getTitle() {
            return titleField.getInput();
        }

        public String getDescription() {
            return descriptionField.getInput();
        }

        public int getCategory() {
            return Integer.parseInt(categoryRadio.getInput()) + 101;
        }

        public int getType() {
            return Integer.parseInt(typeRadio.getInput()) + 201;
        }

        public int getStatus() {
            return Integer.parseInt(statusRadio.getInput()) + 301;
        }

    }

}
