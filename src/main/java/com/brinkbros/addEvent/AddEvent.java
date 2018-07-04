package com.brinkbros.addEvent;

import com.brinkbros.CalmanAssignment;
import com.brinkbros.CalmanUser;
import com.brinkbros.DatabaseConnector;
import com.brinkbros.DateEvent;
import com.brinkbros.SidePanel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public abstract class AddEvent extends Panel {

    private static final String EVENT_FORM_ID = "eventForm";
    private static final String DATE_FIELD_ID = "dateField";
    private static final String TITLE_FIELD_ID = "titleField";
    private static final String DESCRIPTION_AREA_ID = "descriptionArea";
    private static final String DEADLINE_CHECK_ID = "deadlineCheck";
    private static final String EXTRA_DATES_CONTAINER_ID = "extraDatesContainer";
    private static final String CON_AH_DATE_ID = "conAhDate";
    private static final String CON_AH_LABEL_ID = "conAhLabel";
    private static final String CON_DI_DATE_ID = "conDiDate";
    private static final String CON_DI_LABEL_ID = "conDiLabel";
    private static final String DEF_DATE_ID = "defDate";
    private static final String DEF_LABEL_ID = "defLabel";
    private static final String ASSIGNMENT_CONTAINER_ID = "assigneesContainer";
    private static final String ASSIGNMENT_ID = "assignment";
    private static final String ASSIGNMENT_NAME_ID = "assignmentName";
    private static final String ASSIGNMENT_TYPE_ID = "assignmentType";
    private static final String ADD_ASSIGNMENT_ID = "addAssignment";
    private static final String CATEGORY_GROUP_ID = "category";
    private static final String TYPE_GROUP_ID = "type";
    private static final String STATUS_GROUP_ID = "status";
    private static final String SUBEVENT_ID = "subevent";
    private static final String ADD_SUBEVENT_BUTTON_ID = "addSubeventButton";
    private static final String SUBMIT_BUTTON_ID = "subBttn";

    private static final String TITLE = "Titel";
    private static final String DESCRIPTION = "Beschrijving";
    private static final String DEADLINE_CHECK_LABEL = "Extra deadlines toevoegen";
    private static final String CON_AH_LABEL = "Concept bij afdelingshoofd";
    private static final String CON_DI_LABEL = "Concept bij directeur";
    private static final String DEF_LABEL = "Definitief";

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private static final ArrayList<String> categoryList = new ArrayList();
    private static final ArrayList<String> typeList = new ArrayList();
    private static final ArrayList<String> statusList = new ArrayList();
    private static final ArrayList<String> assignmentTypesList = new ArrayList();

    static {
        categoryList.add("Bestuursvergadering (Blauw)");
        categoryList.add("Webinar (Groen)");
        categoryList.add("Rood");
        categoryList.add("ALV (Geel)");

        typeList.add("Evenement");
        typeList.add("Dossier");

        statusList.add("In behandeling");
        statusList.add("Concept bij afdelingshoofd");
        statusList.add("Concept bij behandelaar");
        statusList.add("Concept bij directeur");
        statusList.add("Definitief");
        statusList.add("Voltooid");

        assignmentTypesList.add("MT verantwoordelijke");
        assignmentTypesList.add("Trekker");
        assignmentTypesList.add("Overig");
    }

    public abstract SidePanel getSidePanel();

    public abstract Properties getDBProps();

    private RepeatingView subEvents;
    private List<SubeventForm> subeventList;
    private ArrayList<CalmanUser> userList;
    private ArrayList<String> usernameList;

    private TextField conAhDate;
    private TextField conDiDate;
    private TextField defDate;

    private int eventId;
    private String date;
    private String title;
    private String description;
    private String deadline1;
    private String deadline2;
    private String deadline3;
    private String category;
    private String type;
    private String status;
    private String submitButtonText;
    private ArrayList<DropDownChoice> assignmentList;
    private ArrayList<DropDownChoice> assignmentTypeList;

    public AddEvent(String id) {
        this(
                id,
                -1,
                new ArrayList(),
                Calendar.getInstance(),
                "Titel",
                "Beschrijving",
                null,//new GregorianCalendar(2018, 6, 6),
                null,//new GregorianCalendar(2018, 6, 8),
                null,//new GregorianCalendar(2018, 6, 10),
                101,
                201,
                301);
    }

    public AddEvent(String id, DateEvent event) {
        this(
                id,
                event.getId(),
                event.getAssignments(),
                event.getCalendar(),
                event.getTitle(),
                event.getDescription(),
                event.getConAhCal().isPresent() ? event.getConAhCal().get() : null,
                event.getConDiCal().isPresent() ? event.getConDiCal().get() : null,
                event.getDefCal().isPresent() ? event.getDefCal().get() : null,
                event.getCategoryInt(),
                event.getTypeInt(),
                event.getStatusInt());
    }

    public AddEvent(String id, int eventId, ArrayList<CalmanAssignment> assignments, Calendar date, String title, String description, Calendar deadline1, Calendar deadline2, Calendar deadline3, int category, int type, int status) {
        super(id);
        this.eventId = eventId;
        this.date = formatter.format(date.getTime());
        this.title = title;
        this.description = description;
        this.deadline1 = deadline1 == null ? null : formatter.format(deadline1.getTime());
        this.deadline2 = deadline2 == null ? null : formatter.format(deadline2.getTime());
        this.deadline3 = deadline3 == null ? null : formatter.format(deadline3.getTime());
        this.category = categoryList.get(category - 101);
        this.type = typeList.get(type - 201);
        this.status = statusList.get(status - 301);
        this.submitButtonText = eventId == -1 ? "Versturen" : "Update";

        Form form = new Form(EVENT_FORM_ID, new Model(EVENT_FORM_ID));
        form.setOutputMarkupId(true);
        add(form);
        {
            TextField dateField = new TextField(DATE_FIELD_ID, new PropertyModel(this, "date")) {
                @Override
                public String[] getInputTypes() {
                    return new String[]{"date"};
                }
            };
            dateField.setOutputMarkupId(true);
            form.add(dateField);

            TextField<String> titleField = new TextField(TITLE_FIELD_ID, new PropertyModel(this, "title"));
            titleField.setOutputMarkupId(true);
            form.add(titleField);

            TextArea descriptionField = new TextArea(DESCRIPTION_AREA_ID, new PropertyModel(this, "description"));
            form.add(descriptionField);

            WebMarkupContainer extraDatesCont = new WebMarkupContainer(EXTRA_DATES_CONTAINER_ID);
            extraDatesCont.setOutputMarkupId(true);
            extraDatesCont.setOutputMarkupPlaceholderTag(true);
            extraDatesCont.setVisible(false);
            form.add(extraDatesCont);
            {
                conAhDate = new TextField(CON_AH_DATE_ID, this.deadline1 == null ? new Model() : new Model(this.deadline1)) {
                    @Override
                    public String[] getInputTypes() {
                        return new String[]{"date"};
                    }
                };
                conAhDate.setOutputMarkupId(true);
                extraDatesCont.add(conAhDate);

                Label conAhLabel = new Label(CON_AH_LABEL_ID, new Model(CON_AH_LABEL));
                conAhLabel.setOutputMarkupId(true);
                extraDatesCont.add(conAhLabel);

                conDiDate = new TextField(CON_DI_DATE_ID, this.deadline2 == null ? new Model() : new Model(this.deadline2)) {
                    @Override
                    public String[] getInputTypes() {
                        return new String[]{"date"};
                    }
                };
                conDiDate.setOutputMarkupId(true);
                extraDatesCont.add(conDiDate);

                Label conDiLabel = new Label(CON_DI_LABEL_ID, new Model(CON_DI_LABEL));
                conDiLabel.setOutputMarkupId(true);
                extraDatesCont.add(conDiLabel);

                defDate = new TextField(DEF_DATE_ID, this.deadline3 == null ? new Model() : new Model(this.deadline3)) {
                    @Override
                    public String[] getInputTypes() {
                        return new String[]{"date"};
                    }
                };
                defDate.setOutputMarkupId(true);
                extraDatesCont.add(defDate);

                Label defLabel = new Label(DEF_LABEL_ID, new Model(DEF_LABEL));
                defLabel.setOutputMarkupId(true);
                extraDatesCont.add(defLabel);
            }

            CheckBox deadlineCheck = new CheckBox(DEADLINE_CHECK_ID, new Model<Boolean>(false));
            deadlineCheck.setOutputMarkupId(true);
            deadlineCheck.setLabel(new Model(DEADLINE_CHECK_LABEL));
            deadlineCheck.add(new AjaxFormComponentUpdatingBehavior("oninput") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    extraDatesCont.setVisible(deadlineCheck.getModelObject());
                    target.add(extraDatesCont);
                }
            });
            form.add(deadlineCheck);
            usernameList = new ArrayList();
            {
                userList = CalmanUser
                        .getUsers()
                        .stream()
                        .sorted(
                                (x, y) -> x.getName().compareTo(y.getName()))
                        .collect(
                                Collectors.toCollection(ArrayList::new));
                for (CalmanUser cu : userList) {
                    usernameList.add(cu.getName());
                }
            }

            WebMarkupContainer outerAssignmentCont = new WebMarkupContainer(ASSIGNMENT_CONTAINER_ID);
            outerAssignmentCont.setOutputMarkupId(true);
            form.add(outerAssignmentCont);

            RepeatingView assignmentRepeater = new RepeatingView(ASSIGNMENT_ID);
            assignmentRepeater.setOutputMarkupId(true);
            outerAssignmentCont.add(assignmentRepeater);

            assignmentList = new ArrayList();
            assignmentTypeList = new ArrayList();
            {
                if (assignments.isEmpty()) {
                    WebMarkupContainer assignmentCont = new WebMarkupContainer(assignmentRepeater.newChildId());
                    assignmentCont.setOutputMarkupId(true);
                    assignmentRepeater.add(assignmentCont);

                    DropDownChoice assignment = new DropDownChoice(
                            ASSIGNMENT_NAME_ID,
                            new Model(usernameList.get(0)),
                            usernameList);
                    assignment.setOutputMarkupId(true);
                    assignmentList.add(assignment);
                    assignmentCont.add(assignment);

                    DropDownChoice assignmentType = new DropDownChoice(
                            ASSIGNMENT_TYPE_ID,
                            new Model(assignmentTypesList.get(0)),
                            assignmentTypesList);
                    assignmentType.setOutputMarkupId(true);
                    assignmentTypeList.add(assignmentType);
                    assignmentCont.add(assignmentType);
                } else {
                    for (int i = 0; i < assignments.size(); i++) {
                        WebMarkupContainer assignmentCont = new WebMarkupContainer(assignmentRepeater.newChildId());
                        assignmentCont.setOutputMarkupId(true);
                        assignmentRepeater.add(assignmentCont);

                        DropDownChoice assignment = new DropDownChoice(
                                ASSIGNMENT_NAME_ID,
                                new Model(assignments.get(i).getUser().getName()),
                                usernameList);
                        assignment.setOutputMarkupId(true);
                        assignmentList.add(assignment);
                        assignmentCont.add(assignment);

                        DropDownChoice assignmentType = new DropDownChoice(
                                ASSIGNMENT_TYPE_ID,
                                new Model(assignmentTypesList.get(assignments.get(i).getAssignmentType())),
                                assignmentTypesList);
                        assignmentType.setOutputMarkupId(true);
                        assignmentTypeList.add(assignmentType);
                        assignmentCont.add(assignmentType);
                    }
                }
            }
            AjaxLink addAssignment = new AjaxLink(ADD_ASSIGNMENT_ID) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    WebMarkupContainer singleAssignment = new WebMarkupContainer(assignmentRepeater.newChildId());
                    singleAssignment.setOutputMarkupId(true);
                    assignmentRepeater.add(singleAssignment);

                    DropDownChoice assignment = new DropDownChoice(ASSIGNMENT_NAME_ID, new Model(usernameList.get(0)), usernameList);
                    assignment.setOutputMarkupId(true);
                    singleAssignment.add(assignment);
                    assignmentList.add(assignment);

                    DropDownChoice assignmentType = new DropDownChoice(ASSIGNMENT_TYPE_ID, new Model(assignmentTypesList.get(0)), assignmentTypesList);
                    assignmentType.setOutputMarkupId(true);
                    singleAssignment.add(assignmentType);
                    assignmentTypeList.add(assignmentType);

                    target.add(outerAssignmentCont);
                }

                @Override
                public void renderHead(IHeaderResponse response) {
                    response
                            .render(OnDomReadyHeaderItem
                                    .forScript(new StringBuilder()
                                            .append("document.getElementById(\"")
                                            .append(getMarkupId())
                                            .append("\").value=\"")
                                            .append("Assignee toevoegen")
                                            .append("\";")
                                            .toString()));
                    super.renderHead(response);
                }
            };
            addAssignment.setOutputMarkupId(true);
            form.add(addAssignment);

            DropDownChoice categoryGroup = new DropDownChoice(CATEGORY_GROUP_ID, new PropertyModel(this, "category"), categoryList);
            form.add(categoryGroup);

            DropDownChoice typeGroup = new DropDownChoice(TYPE_GROUP_ID, new PropertyModel(this, "type"), typeList);
            form.add(typeGroup);

            DropDownChoice statusGroup = new DropDownChoice(STATUS_GROUP_ID, new PropertyModel(this, "status"), statusList);
            form.add(statusGroup);

            subeventList = new ArrayList();

            subEvents = new RepeatingView(SUBEVENT_ID);
            subEvents.setOutputMarkupId(true);
            form.add(subEvents);

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
                public void onError() {
                    throw new RuntimeException(dateField.getInput().toString());
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
            form.add(addSubeventButton);

            Button submitButton = new Button(SUBMIT_BUTTON_ID, new PropertyModel(this, "submitButtonText")) {
                @Override
                public void onSubmit() {
                    getSession().invalidateNow();
                }
            };

            if (eventId == -1) {
                submitButton.add((IValidatable<String> validatable) -> {
                    try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), getDBProps())) {
                        int eventID = DatabaseConnector.insert(conn, DatabaseConnector.Table.EVENTS, new String[]{
                            dateField.getInput(),
                            deadlineCheck.getModelObject() ? conAhDate.getInput() : null,
                            deadlineCheck.getModelObject() ? conDiDate.getInput() : null,
                            deadlineCheck.getModelObject() ? defDate.getInput() : null,
                            titleField.getInput(),
                            descriptionField.getInput(),
                            String.valueOf(Integer.parseInt(categoryGroup.getInput()) + 101),
                            String.valueOf(Integer.parseInt(typeGroup.getInput()) + 201),
                            String.valueOf(Integer.parseInt(statusGroup.getInput()) + 301),
                            null,
                            null});
                        for (int i = 0; i < assignmentList.size(); i++) {
                            DatabaseConnector.insert(conn, DatabaseConnector.Table.ASSIGNMENTS, new String[]{
                                String.valueOf(eventID),
                                String.valueOf(CalmanUser.getUser(usernameList.get(Integer.parseInt(assignmentList.get(i).getInput()))).getUserId()),
                                String.valueOf(Integer.parseInt(assignmentTypeList.get(i).getInput()))
                            });
                        }
                        for (SubeventForm sef : subeventList) {
                            int subEventID = DatabaseConnector.insert(conn, DatabaseConnector.Table.EVENTS, new String[]{
                                sef.getDate(),
                                sef.getConAhDate(),
                                sef.getConDiDate(),
                                sef.getDefDate(),
                                sef.getTitle(),
                                sef.getDescription(),
                                String.valueOf(sef.getCategory()),
                                String.valueOf(sef.getType()),
                                String.valueOf(sef.getStatus()),
                                String.valueOf(eventID),
                                null});
                            for (int i = 0; i < sef.assignmentList.size(); i++) {
                                DatabaseConnector.insert(conn, DatabaseConnector.Table.ASSIGNMENTS, new String[]{
                                    String.valueOf(subEventID),
                                    String.valueOf(CalmanUser.getUser(usernameList.get(Integer.parseInt(sef.assignmentList.get(i).getInput()))).getUserId()),
                                    String.valueOf(Integer.parseInt(assignmentTypeList.get(i).getInput()))
                                });
                            }
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                });
            } else {
                submitButton.add((IValidatable<String> validatable) -> {
                    try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDBURL(), getDBProps())) {
                        DatabaseConnector.update(
                                conn,
                                DatabaseConnector.Table.EVENTS,
                                deadlineCheck.getModelObject()
                                        ? new String[]{
                                    "END_DATE",
                                    "CON_AH_DATE",
                                    "CON_DI_DATE",
                                    "DEF_DATE",
                                    "TITLE",
                                    "DESCRIPTION",
                                    "CATEGORY",
                                    "TYPE",
                                    "STATUS"}
                                : new String[]{
                                    "END_DATE",
                                    "TITLE",
                                    "DESCRIPTION",
                                    "CATEGORY",
                                    "TYPE",
                                    "STATUS"},
                                deadlineCheck.getModelObject()
                                        ? new String[]{
                                    dateField.getInput(),
                                    conAhDate.getInput(),
                                    conDiDate.getInput(),
                                    defDate.getInput(),
                                    titleField.getInput(),
                                    descriptionField.getInput(),
                                    String.valueOf(Integer.parseInt(categoryGroup.getInput()) + 101),
                                    String.valueOf(Integer.parseInt(typeGroup.getInput()) + 201),
                                    String.valueOf(Integer.parseInt(statusGroup.getInput()) + 301)}
                                : new String[]{
                                    dateField.getInput(),
                                    titleField.getInput(),
                                    descriptionField.getInput(),
                                    String.valueOf(Integer.parseInt(categoryGroup.getInput()) + 101),
                                    String.valueOf(Integer.parseInt(typeGroup.getInput()) + 201),
                                    String.valueOf(Integer.parseInt(statusGroup.getInput()) + 301)},
                                "EVENT_ID",
                                eventId);
                        for (SubeventForm sef : subeventList) {
                            int subEventID = DatabaseConnector.insert(conn, DatabaseConnector.Table.EVENTS, new String[]{
                                sef.getDate(),
                                sef.getConAhDate(),
                                sef.getConDiDate(),
                                sef.getDefDate(),
                                sef.getTitle(),
                                sef.getDescription(),
                                String.valueOf(sef.getCategory()),
                                String.valueOf(sef.getType()),
                                String.valueOf(sef.getStatus()),
                                String.valueOf(eventId),
                                null});
                            for (int i = 0; i < sef.assignmentList.size(); i++) {
                                DatabaseConnector.insert(conn, DatabaseConnector.Table.ASSIGNMENTS, new String[]{
                                    String.valueOf(subEventID),
                                    String.valueOf(CalmanUser.getUser(usernameList.get(Integer.parseInt(sef.assignmentList.get(i).getInput()))).getUserId()),
                                    String.valueOf(Integer.parseInt(assignmentTypeList.get(i).getInput()))
                                });
                            }
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                });
            }
            form.add(submitButton);
        }
    }

    private class SubeventForm extends Form {

        private static final String SUB_DATE_ID = "subDate";
        private static final String SUB_TITLE_ID = "subTitle";
        private static final String SUB_DESCRIPTION_ID = "subDescription";
        private static final String SUB_DEADLINE_CHECK_ID = "subDeadlineCheck";
        private static final String SUB_EXTRA_DATES_CONTAINER_ID = "subExtraDatesContainer";
        private static final String SUB_CON_AH_DATE_ID = "subConAhDate";
        private static final String SUB_CON_AH_LABEL_ID = "subConAhLabel";
        private static final String SUB_CON_DI_DATE_ID = "subConDiDate";
        private static final String SUB_CON_DI_LABEL_ID = "subConDiLabel";
        private static final String SUB_DEF_DATE_ID = "subDefDate";
        private static final String SUB_DEF_LABEL_ID = "subDefLabel";
        private static final String SUB_ASSIGNMENT_CONTAINER_ID = "subAssigneesContainer";
        private static final String SUB_ASSIGNMENT_ID = "subAssignment";
        private static final String SUB_ASSIGNMENT_NAME_ID = "subAssignmentName";
        private static final String SUB_ASSIGNMENT_TYPE_ID = "subAssignmentType";
        private static final String SUB_ADD_ASSIGNMENT_ID = "subAddAssignment";
        private static final String SUB_CATEGORY_ID = "subCategory";
        private static final String SUB_TYPE_ID = "subType";
        private static final String SUB_STATUS_ID = "subStatus";
        private static final String SUB_DELETE_ID = "deleteSubevent";

        private TextField dateField;
        private TextField titleField;
        private TextArea descriptionField;
        private CheckBox subDeadlineCheck;
        private TextField subConAhDate;
        private TextField subConDiDate;
        private TextField subDefDate;
        private RepeatingView assignmentRepeater;
        private DropDownChoice assignment;
        private DropDownChoice category;
        private DropDownChoice type;
        private DropDownChoice status;

        public ArrayList<DropDownChoice> assignmentList;
        public ArrayList<DropDownChoice> assignmentTypeList;

        public SubeventForm(String id, IModel model) {
            super(id, model);
            dateField = new TextField(SUB_DATE_ID, new Model()) {
                @Override
                public String[] getInputTypes() {
                    return new String[]{"date"};
                }
            };
            dateField.setOutputMarkupId(true);
            add(dateField);

            WebMarkupContainer subExtraDatesCont = new WebMarkupContainer(SUB_EXTRA_DATES_CONTAINER_ID);
            subExtraDatesCont.setOutputMarkupId(true);
            subExtraDatesCont.setOutputMarkupPlaceholderTag(true);
            subExtraDatesCont.setVisible(false);
            add(subExtraDatesCont);
            {
                subConAhDate = new TextField(SUB_CON_AH_DATE_ID, new Model()) {
                    @Override
                    public String[] getInputTypes() {
                        return new String[]{"date"};
                    }
                };
                subConAhDate.setOutputMarkupId(true);
                subExtraDatesCont.add(subConAhDate);

                Label subConAhLabel = new Label(SUB_CON_AH_LABEL_ID, new Model(CON_AH_LABEL));
                subConAhLabel.setOutputMarkupId(true);
                subExtraDatesCont.add(subConAhLabel);

                subConDiDate = new TextField(SUB_CON_DI_DATE_ID, new Model()) {
                    @Override
                    public String[] getInputTypes() {
                        return new String[]{"date"};
                    }
                };
                subConDiDate.setOutputMarkupId(true);
                subExtraDatesCont.add(subConDiDate);

                Label subConDiLabel = new Label(SUB_CON_DI_LABEL_ID, new Model(CON_DI_LABEL));
                subConDiLabel.setOutputMarkupId(true);
                subExtraDatesCont.add(subConDiLabel);

                subDefDate = new TextField(SUB_DEF_DATE_ID, new Model()) {
                    @Override
                    public String[] getInputTypes() {
                        return new String[]{"date"};
                    }
                };
                subDefDate.setOutputMarkupId(true);
                subExtraDatesCont.add(subDefDate);

                Label subDefLabel = new Label(SUB_DEF_LABEL_ID, new Model(DEF_LABEL));
                subDefLabel.setOutputMarkupId(true);
                subExtraDatesCont.add(subDefLabel);
            }
            subDeadlineCheck = new CheckBox(SUB_DEADLINE_CHECK_ID, new Model<Boolean>(false));
            subDeadlineCheck.setOutputMarkupId(true);
            subDeadlineCheck.setLabel(new Model(DEADLINE_CHECK_LABEL));
            subDeadlineCheck.add(new AjaxFormComponentUpdatingBehavior("oninput") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    subExtraDatesCont.setVisible(subDeadlineCheck.getModelObject());
                    target.add(subExtraDatesCont);
                }
            });
            add(subDeadlineCheck);

            titleField = new TextField(SUB_TITLE_ID, new Model("Titel"));
            titleField.setOutputMarkupId(true);
            add(titleField);

            descriptionField = new TextArea(SUB_DESCRIPTION_ID, new Model("Beschrijving"));
            descriptionField.setOutputMarkupId(true);
            add(descriptionField);

            assignmentList = new ArrayList();
            assignmentTypeList = new ArrayList();

            WebMarkupContainer assignmentCont = new WebMarkupContainer(SUB_ASSIGNMENT_CONTAINER_ID);
            assignmentCont.setOutputMarkupId(true);
            add(assignmentCont);

            assignmentRepeater = new RepeatingView(SUB_ASSIGNMENT_ID);
            assignmentRepeater.setOutputMarkupId(true);
            assignmentCont.add(assignmentRepeater);
            {
                WebMarkupContainer singleAssignment = new WebMarkupContainer(assignmentRepeater.newChildId());
                singleAssignment.setOutputMarkupId(true);
                assignmentRepeater.add(singleAssignment);

                assignment = new DropDownChoice(SUB_ASSIGNMENT_NAME_ID, new Model(usernameList.get(0)), usernameList);
                assignment.setOutputMarkupId(true);
                singleAssignment.add(assignment);

                DropDownChoice assignmentType = new DropDownChoice(SUB_ASSIGNMENT_TYPE_ID, new Model(assignmentTypesList.get(0)), assignmentTypesList);
                assignmentType.setOutputMarkupId(true);
                singleAssignment.add(assignmentType);

                assignmentList.add(assignment);
                assignmentTypeList.add(assignmentType);
            }
            AjaxLink addAssignment = new AjaxLink(SUB_ADD_ASSIGNMENT_ID) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    WebMarkupContainer singleAssignment = new WebMarkupContainer(assignmentRepeater.newChildId());
                    singleAssignment.setOutputMarkupId(true);
                    assignmentRepeater.add(singleAssignment);

                    assignment = new DropDownChoice(SUB_ASSIGNMENT_NAME_ID, new Model(usernameList.get(0)), usernameList);
                    assignment.setOutputMarkupId(true);
                    singleAssignment.add(assignment);

                    DropDownChoice assignmentType = new DropDownChoice(SUB_ASSIGNMENT_TYPE_ID, new Model(assignmentTypesList.get(0)), assignmentTypesList);
                    assignmentType.setOutputMarkupId(true);
                    singleAssignment.add(assignmentType);

                    assignmentList.add(assignment);
                    assignmentTypeList.add(assignmentType);
                    target.add(assignmentCont);
                }

                @Override
                public void renderHead(IHeaderResponse response) {
                    response
                            .render(OnDomReadyHeaderItem
                                    .forScript(new StringBuilder()
                                            .append("document.getElementById(\"")
                                            .append(getMarkupId())
                                            .append("\").value=\"")
                                            .append("Assignee toevoegen")
                                            .append("\";")
                                            .toString()));
                    super.renderHead(response);
                }
            };
            addAssignment.setOutputMarkupId(true);
            add(addAssignment);

            category = new DropDownChoice(SUB_CATEGORY_ID, new Model(categoryList.get(0)), categoryList);
            add(category);

            type = new DropDownChoice(SUB_TYPE_ID, new Model(typeList.get(0)), typeList);
            add(type);

            status = new DropDownChoice(SUB_STATUS_ID, new Model(statusList.get(0)), statusList);
            add(status);

            Button deleteButton = new Button(SUB_DELETE_ID, new Model("Verwijder subActiviteit")) {
                @Override
                public void onSubmit() {
                    subeventList.remove(SubeventForm.this);
                    SubeventForm.this.remove();
                }
            };
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

        public String getConAhDate() {
            return subDeadlineCheck.getModelObject() ? subConAhDate.getInput() : null;
        }

        public String getConDiDate() {
            return subDeadlineCheck.getModelObject() ? subConDiDate.getInput() : null;
        }

        public String getDefDate() {
            return subDeadlineCheck.getModelObject() ? subDefDate.getInput() : null;
        }

        private String getAssignment() {
            return assignment.getInput();
        }

        public int getCategory() {
            return Integer.parseInt(category.getInput()) + 101;
        }

        public int getType() {
            return Integer.parseInt(type.getInput()) + 201;
        }

        public int getStatus() {
            return Integer.parseInt(status.getInput()) + 301;
        }

    }
}
