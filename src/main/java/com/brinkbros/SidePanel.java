package com.brinkbros;

import com.brinkbros.addEvent.AddEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

public abstract class SidePanel extends Panel {

    private static final String TITLE_ID = "title";
    private static final String DATE_ID = "date";
    private static final String CON_AH_ID = "conAh";
    private static final String CON_DI_ID = "conDi";
    private static final String DEF_ID = "def";
    private static final String ASSIGNMENT_CONTAINER_ID = "assignmentCont";
    private static final String ASSIGNMENT_ID = "assignment";
    private static final String ASSIGNMENT_NAME_ID = "assignmentName";
    private static final String ASSIGNMENT_TYPE_ID = "assignmentType";
    private static final String DESCRIPTION_ID = "description";
    private static final String CATEGORY_ID = "category";
    private static final String TYPE_ID = "type";
    private static final String STATUS_ID = "status";
    private static final String PARENT_EVENT_ID = "parentevent";
    private static final String PARENT_LINK_ID = "parentLink";
    private static final String PARENT_TITLE_ID = "parentTitle";
    private static final String PARENT_DESCRIPTION_ID = "parentDescription";
    private static final String PARENT_DATE_ID = "parentDate";
    private static final String SUBEVENT_ID = "subevent";
    private static final String SUB_LINK_ID = "subLink";
    private static final String SUB_TITLE_ID = "subTitle";
    private static final String SUB_DESCRIPTION_ID = "subDescription";
    private static final String SUB_DATE_ID = "subDate";
    private static final String EDIT_EVENT_CONTAINER_ID = "editEventContainer";
    private static final String EDIT_EVENT_ID = "editEvent";

    public abstract BasePage getBasePage();

    public abstract Properties getDBProps();

    Label title;
    Label date;
    Label conAh;
    Label conDi;
    Label def;
    Label description;
    Label category;
    Label type;
    Label status;
    AjaxLink parentLink;
    AjaxLink editEvent;

    RepeatingView assignments;
    RepeatingView subevents;
    WebMarkupContainer parentCont;
    WebMarkupContainer subLinkCont;
    WebMarkupContainer assignmentCont;
    WebMarkupContainer editEventCont;

    protected SidePanel(String id) {
        super(id);

        title = new Label(TITLE_ID, new Model("Titel"));
        title.setOutputMarkupId(true);
        add(title);

        date = new Label(DATE_ID, new Model(""));
        date.setOutputMarkupId(true);
        add(date);

        conAh = new Label(CON_AH_ID, new Model(""));
        conAh.setOutputMarkupId(true);
        add(conAh);

        conDi = new Label(CON_DI_ID, new Model(""));
        conDi.setOutputMarkupId(true);
        add(conDi);

        def = new Label(DEF_ID, new Model(""));
        def.setOutputMarkupId(true);
        add(def);

        assignmentCont = new WebMarkupContainer(ASSIGNMENT_CONTAINER_ID);
        assignmentCont.setOutputMarkupId(true);
        add(assignmentCont);
        {
            assignments = new RepeatingView(ASSIGNMENT_ID);
            assignments.setOutputMarkupId(true);
            assignmentCont.add(assignments);
        }

        description = new Label(DESCRIPTION_ID, new Model(""));
        description.setOutputMarkupId(true);
        add(description);

//        type = new Label(TYPE_ID, new Model("Type"));
//        type.setOutputMarkupId(true);
//        add(type);
        status = new Label(STATUS_ID, new Model(""));
        status.setOutputMarkupId(true);
        add(status);

        parentCont = new WebMarkupContainer(PARENT_EVENT_ID);
        parentCont.setOutputMarkupId(true);
        parentCont.setOutputMarkupPlaceholderTag(true);
        parentCont.setVisible(false);
        add(parentCont);
        {
        }

        subLinkCont = new WebMarkupContainer(SUBEVENT_ID);
        subLinkCont.setOutputMarkupId(true);
        add(subLinkCont);
        {
            subevents = new RepeatingView(SUB_LINK_ID);
            subevents.setOutputMarkupId(true);
            subLinkCont.add(subevents);
        }

        editEventCont = new WebMarkupContainer(EDIT_EVENT_CONTAINER_ID);
        editEventCont.setOutputMarkupId(true);
        editEventCont.setOutputMarkupPlaceholderTag(true);
        editEventCont.setVisible(false);
        add(editEventCont);

    }

    public void changeDetails(AjaxRequestTarget target, DateEvent event) throws SQLException {

        title.setDefaultModelObject(event.getTitle());
        target.add(title);

        date.setDefaultModelObject(event.getCalendar().get(Calendar.DAY_OF_MONTH) + "/"
                + (event.getCalendar().get(Calendar.MONTH) + 1) + "/"
                + event.getCalendar().get(Calendar.YEAR));
        target.add(date);

        assignments.removeAll();
        event.getAssignments(getDBProps());
        for (int i = 0; i < event.getAssignments().size(); i++) {
            WebMarkupContainer singleAssignment = new WebMarkupContainer(assignments.newChildId());
            singleAssignment.setOutputMarkupId(true);
            assignments.add(singleAssignment);

            singleAssignment.add(new Label(ASSIGNMENT_NAME_ID, new Model(event.getAssignments().get(i).getTypeString() + ": " + event.getAssignments().get(i).getUser().getName())));
            //singleAssignment.add(new Label(ASSIGNMENT_TYPE_ID, new Model(event.getAssignments().get(i).getTypeString())));
        }
        target.add(assignmentCont);

        description.setDefaultModelObject(event.getDescription());
        target.add(description);

        conAh.setDefaultModelObject(
                event.getConAhCal().isPresent()
                        ? "Bij afdelingshoofd: "
                        + event.getConAhCal().get().get(Calendar.DAY_OF_MONTH) + "/"
                        + (event.getConAhCal().get().get(Calendar.MONTH) + 1) + "/"
                        + event.getConAhCal().get().get(Calendar.YEAR)
                        : "");
        conDi.setDefaultModelObject(
                event.getConDiCal().isPresent()
                        ? "Bij directeur: "
                        + event.getConDiCal().get().get(Calendar.DAY_OF_MONTH) + "/"
                        + (event.getConDiCal().get().get(Calendar.MONTH) + 1) + "/"
                        + event.getConDiCal().get().get(Calendar.YEAR)
                        : "");
        def.setDefaultModelObject(
                event.getDefCal().isPresent()
                        ? "Verzonden naar IZ: "
                        + event.getDefCal().get().get(Calendar.DAY_OF_MONTH) + "/"
                        + (event.getDefCal().get().get(Calendar.MONTH) + 1) + "/"
                        + event.getDefCal().get().get(Calendar.YEAR)
                        : "");
        target.add(conAh);
        target.add(conDi);
        target.add(def);

//        type.setDefaultModelObject(event.getType());
//        target.add(type);
        status.setDefaultModelObject(event.getStatus());
        target.add(status);

        parentCont.removeAll();
        if (event.getParentEvent() != null) {
            parentLink = new AjaxLink(PARENT_LINK_ID) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        changeDetails(target, event.getParentEvent());
                    } catch (SQLException ex) {
                        changeDetails(target, ex.getMessage());
                    }
                }
            };
            parentLink.setOutputMarkupId(true);
            parentCont.addOrReplace(parentLink);
            parentLink.add(new Label(PARENT_TITLE_ID, event.getParentEvent().getTitle()));
            parentLink.add(new Label(PARENT_DESCRIPTION_ID, event.getParentEvent().getStatus()));
            parentLink.add(new Label(PARENT_DATE_ID, event.getParentEvent().getCalendar().get(Calendar.DAY_OF_MONTH) + "/"
                    + (event.getParentEvent().getCalendar().get(Calendar.MONTH) + 1) + "/"
                    + event.getParentEvent().getCalendar().get(Calendar.YEAR)));
            parentLink.add(new AttributeModifier("style", event.getParentEvent().getColor().getStyleAttr()));
            parentCont.setVisible(true);
        } else {
            parentCont.setVisible(false);
        }
        target.add(parentCont);

        subevents.removeAll();
        ArrayList<DateEvent> subeventList = event.getSubevents(getDBProps());
        for (DateEvent de : subeventList) {
            AjaxLink subLink = new AjaxLink(subevents.newChildId()) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        changeDetails(target, de);
                    } catch (SQLException ex) {
                        changeDetails(target, ex.getMessage());
                    }
                }
            };
            subLink.add(new Label(SUB_TITLE_ID, new Model(de.getTitle())));
            subLink.add(new Label(SUB_DESCRIPTION_ID, new Model(de.getStatus())));
            subLink.add(new Label(SUB_DATE_ID, new Model(de.getCalendar().get(Calendar.DAY_OF_MONTH) + "/"
                    + (de.getCalendar().get(Calendar.MONTH) + 1) + "/"
                    + de.getCalendar().get(Calendar.YEAR))));
            subLink.add(new AttributeModifier("style", new Model(de.getColor().getStyleAttr())));
            subevents.add(subLink);
        }
        target.add(subLinkCont);

        editEvent = new AjaxLink(EDIT_EVENT_ID) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AddEvent newPage = new AddEvent(BasePage.PANEL_ID, event) {
                    @Override
                    public SidePanel getSidePanel() {
                        return SidePanel.this;
                    }

                    @Override
                    public Properties getDBProps() {
                        return SidePanel.this.getDBProps();
                    }
                };
                getBasePage().activePanels.remove(BasePage.PageType.ADDEVENT);
                getBasePage().replace(newPage);
                target.add(getBasePage());
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                response
                        .render(OnDomReadyHeaderItem
                                .forScript(new StringBuilder()
                                        .append("document.getElementById(\"")
                                        .append(getMarkupId())
                                        .append("\").value=\"")
                                        .append("Bewerken")
                                        .append("\";")
                                        .toString()));
                super.renderHead(response);
            }
        };
        editEvent.setOutputMarkupId(true);
        editEventCont.addOrReplace(editEvent);
        editEventCont.setVisible(true);
        target.add(editEventCont);

    }

    public void changeDetails(AjaxRequestTarget target, String newDetails) {
        Label newLabel = new Label(DESCRIPTION_ID, newDetails);
        description.replaceWith(newLabel);
        newLabel.setOutputMarkupId(true);
        target.add(newLabel);
        description = newLabel;
    }

}
