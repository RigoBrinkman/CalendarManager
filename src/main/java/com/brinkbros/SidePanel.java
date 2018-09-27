package com.brinkbros;

import com.brinkbros.addevent.AddEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

public class SidePanel extends Panel {

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
  private static final String STATUS_OPTIONS_ID = "statusOptions";
  private static final String EDIT_STATUS_FORM_ID = "editStatusForm";
  private static final String EDIT_STATUS_ID = "editStatus";
  private static final String EDIT_STATUS_SUBMIT_ID = "editStatusSubmit";
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
  private static final String EDIT_EVENT_CONTAINER_ID = "editEventCont";
  private static final String EDIT_EVENT_ID = "editEvent";

  private static final AttributeModifier RED_TEXT_MODIFIER = new AttributeModifier("style", new Model("color: red;"));
  private static final AttributeModifier BLACK_TEXT_MODIFIER = new AttributeModifier("style", new Model("color: black;"));

  Properties dbProps;
  BasePage basePage;
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
  DropDownChoice statusOptions;
  AjaxLink editStatus;
  Button editStatusSubmit;
  AjaxLink editEvent;

  RepeatingView assignments;
  RepeatingView subevents;
  WebMarkupContainer parentCont;
  WebMarkupContainer subLinkCont;
  WebMarkupContainer assignmentCont;
  WebMarkupContainer editEventCont;

  Form editStatusForm;

  private final String id;

  protected SidePanel(String id, Properties dbProps, BasePage basePage) {
    super(id);
    this.id = id;
    this.dbProps = dbProps;
    this.basePage = basePage;

    title = new Label(TITLE_ID, new Model("Details"));
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

    editStatusForm = new Form(EDIT_STATUS_FORM_ID);
    editStatusForm.setOutputMarkupId(true);
    add(editStatusForm);

    statusOptions = new DropDownChoice(STATUS_OPTIONS_ID,
        new Model(CalmanEvent.Status.class.getFields()[0].getName()),
        (ArrayList<String>) Arrays.asList(CalmanEvent.Status.class.getFields()).stream().map(x -> x.getName()).collect(Collectors.toCollection(ArrayList<String>::new)));
    statusOptions.setOutputMarkupId(true);
    statusOptions.setOutputMarkupPlaceholderTag(true);
    statusOptions.setVisible(false);
    editStatusForm.add(statusOptions);

    editStatus = new AjaxLink(EDIT_STATUS_ID) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }
    };
    editStatus.setOutputMarkupId(true);
    editStatus.setOutputMarkupPlaceholderTag(true);
    editStatus.setVisible(false);
    editStatusForm.add(editStatus);

    editStatusSubmit = new Button(EDIT_STATUS_SUBMIT_ID);
    editStatusSubmit.setOutputMarkupId(true);
    editStatusSubmit.setOutputMarkupPlaceholderTag(true);
    editStatusSubmit.setVisible(false);
    editStatusForm.add(editStatusSubmit);

    editEventCont = new WebMarkupContainer(EDIT_EVENT_CONTAINER_ID);
    editEventCont.setOutputMarkupId(true);
    editEventCont.setOutputMarkupPlaceholderTag(true);
    editEventCont.setVisible(false);
    add(editEventCont);
    editEventCont.add(new AjaxLink(EDIT_EVENT_ID) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }
    });

    subLinkCont = new WebMarkupContainer(SUBEVENT_ID);
    subLinkCont.setOutputMarkupId(true);
    add(subLinkCont);
    {
      subevents = new RepeatingView(SUB_LINK_ID);
      subevents.setOutputMarkupId(true);
      subLinkCont.add(subevents);
    }

  }

  public void changeDetails(AjaxRequestTarget target, CalmanEvent event, CalmanUser currentUser) throws SQLException {
    Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps);

    title.setDefaultModelObject(event.getTitle());
    target.add(title);

    date.setDefaultModelObject(event.getCalendar().get(Calendar.DAY_OF_MONTH) + "/"
        + (event.getCalendar().get(Calendar.MONTH) + 1) + "/"
        + event.getCalendar().get(Calendar.YEAR));
    if (event.getStatusInt() < 306 && event.getCalendar().before(Calendar.getInstance())) {
      date.add(RED_TEXT_MODIFIER);
    } else {
      date.add(BLACK_TEXT_MODIFIER);
    }
    target.add(date);

    assignments.removeAll();
    WebMarkupContainer mtAssignment = new WebMarkupContainer(assignments.newChildId());
    mtAssignment.setOutputMarkupId(true);
    mtAssignment.add(new Label(ASSIGNMENT_NAME_ID, new Model("MT Verantwoordelijke: " + event.getMtAssignee().getName())));
    assignments.add(mtAssignment);

    WebMarkupContainer trekkerAssignment = new WebMarkupContainer(assignments.newChildId());
    trekkerAssignment.setOutputMarkupId(true);
    trekkerAssignment.add(new Label(ASSIGNMENT_NAME_ID, new Model("Trekker: " + event.getTrekkerAssignee().getName())));
    assignments.add(trekkerAssignment);

    if (!event.getAssignments(conn).isEmpty()) {
      for (int i = 0; i < event.getAssignments(conn).size(); i++) {
        WebMarkupContainer singleAssignment = new WebMarkupContainer(assignments.newChildId());
        singleAssignment.setOutputMarkupId(true);
        assignments.add(singleAssignment);

        singleAssignment.add(
            i == 0
                ? new MultiLineLabel(ASSIGNMENT_NAME_ID, new Model("Overige assignees:\n" + event.getAssignments().get(i).getName()))
                : new Label(ASSIGNMENT_NAME_ID, new Model(event.getAssignments().get(i).getName())));
        //singleAssignment.add(new Label(ASSIGNMENT_TYPE_ID, new Model(event.getAssignments().get(i).getTypeString())));
      }
    }
    target.add(assignmentCont);

    description.setDefaultModelObject(event.getDescription());
    target.add(description);

    if (event.getDeadlines().isPresent()) {
      conAh.setDefaultModelObject(
          "Bij afdelingshoofd: "
          + event.getDeadlines().get()[0].get(Calendar.DAY_OF_MONTH) + "/"
          + (event.getDeadlines().get()[0].get(Calendar.MONTH) + 1) + "/"
          + event.getDeadlines().get()[0].get(Calendar.YEAR));
      if (event.getStatusInt() < 302 && event.getDeadlines().get()[0].before(Calendar.getInstance())) {
        conAh.add(RED_TEXT_MODIFIER);
      } else {
        conAh.add(BLACK_TEXT_MODIFIER);
      }
      conDi.setDefaultModelObject(
          "Bij directeur: "
          + event.getDeadlines().get()[1].get(Calendar.DAY_OF_MONTH) + "/"
          + (event.getDeadlines().get()[1].get(Calendar.MONTH) + 1) + "/"
          + event.getDeadlines().get()[1].get(Calendar.YEAR));
      if (event.getStatusInt() < 303 && event.getDeadlines().get()[1].before(Calendar.getInstance())) {
        conDi.add(RED_TEXT_MODIFIER);
      } else {
        conDi.add(BLACK_TEXT_MODIFIER);
      }
      def.setDefaultModelObject(
          "Verzonden naar IZ: "
          + event.getDeadlines().get()[2].get(Calendar.DAY_OF_MONTH) + "/"
          + (event.getDeadlines().get()[2].get(Calendar.MONTH) + 1) + "/"
          + event.getDeadlines().get()[2].get(Calendar.YEAR));
      if (event.getStatusInt() < 304 && event.getDeadlines().get()[2].before(Calendar.getInstance())) {
        def.add(RED_TEXT_MODIFIER);
      } else {
        def.add(BLACK_TEXT_MODIFIER);
      }
    } else {
      conAh.setDefaultModelObject("");
      conDi.setDefaultModelObject("");
      def.setDefaultModelObject("");
    }
    target.add(conAh);
    target.add(conDi);
    target.add(def);

//        type.setDefaultModelObject(event.getType());
//        target.add(type);
    status.setDefaultModelObject(event.getStatus());
    target.add(status);

    editStatus = new AjaxLink(EDIT_STATUS_ID) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        statusOptions.setDefaultModelObject(CalmanEvent.Status.class.getFields()[event.getStatusInt() - 301].getName());
        statusOptions.setVisible(true);
        editStatusSubmit = new Button(EDIT_STATUS_SUBMIT_ID) {
          @Override
          public void onSubmit() {
            try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps)) {
              conn.createStatement().executeUpdate("UPDATE PF_EVENTS SET status = " + String.valueOf(Integer.parseInt(statusOptions.getInput()) + 301)
                  + " WHERE event_id = " + String.valueOf(event.getId()));
              basePage.resetPanels();
            } catch (SQLException ex) {
              throw new RuntimeException(ex.getMessage());
            }
          }
          @Override
          public void renderHead(IHeaderResponse response) {
            response
                .render(OnDomReadyHeaderItem
                    .forScript(new StringBuilder()
                        .append("document.getElementById(\"")
                        .append(getMarkupId())
                        .append("\").value=\"")
                        .append("Bevestigen")
                        .append("\";")
                        .toString()));
            super.renderHead(response);
          }
        };
        editStatusSubmit.setOutputMarkupId(true);
        editStatusSubmit.setVisible(true);
        editStatusForm.addOrReplace(editStatusSubmit);
        target.add(editStatusForm);
        this.setVisible(false);

        target.add(statusOptions);
        target.add(editStatus);
      }

      @Override
      public void renderHead(IHeaderResponse response) {
        response
            .render(OnDomReadyHeaderItem
                .forScript(new StringBuilder()
                    .append("document.getElementById(\"")
                    .append(getMarkupId())
                    .append("\").value=\"")
                    .append("Status veranderen")
                    .append("\";")
                    .toString()));
        super.renderHead(response);
      }
    };
    editStatus.setOutputMarkupId(true);
    editStatus.setVisible(true);
    editStatusForm.addOrReplace(editStatus);
    target.add(editStatusForm);

    statusOptions.setVisible(false);
    editStatusSubmit.setVisible(false);

    parentCont.removeAll();
    if (event.getParentEvent(conn).isPresent()) {
      parentLink = new AjaxLink(PARENT_LINK_ID) {
        @Override
        public void onClick(AjaxRequestTarget target) {
          try {
            changeDetails(target, event.getParentEvent(conn).get(), currentUser);
          } catch (SQLException ex) {
            changeDetails(target, ex);
          }
        }
      };
      parentLink.setOutputMarkupId(true);
      parentCont.addOrReplace(parentLink);
      parentLink.add(new Label(PARENT_TITLE_ID, event.getParentEvent(conn).get().getTitle()));
      parentLink.add(new Label(PARENT_DESCRIPTION_ID, event.getParentEvent(conn).get().getStatus()));

      parentLink.add(new Label(PARENT_DATE_ID, event.getParentEvent(conn).get().getCalendar().get(Calendar.DAY_OF_MONTH) + "/"
          + (event.getParentEvent(conn).get().getCalendar().get(Calendar.MONTH) + 1) + "/"
          + event.getParentEvent(conn).get().getCalendar().get(Calendar.YEAR)));
      for (AttributeModifier am : event.getParentEvent(conn).get().getAttributeModifiers()) {
        parentLink.add(am);
      }
      parentCont.setVisible(true);
      if (currentUser.getRole() < 2
          && currentUser != event.getParentEvent(conn).get().getMtAssignee()
          && currentUser != event.getParentEvent(conn).get().getTrekkerAssignee()
          && !event.getParentEvent(conn).get().getAssignments(conn).contains(currentUser)) {
        parentLink.setEnabled(false);
      } else {
        parentLink.setEnabled(true);
      }
    } else {
      parentCont.setVisible(false);
    }
    target.add(parentCont);

    subevents.removeAll();
    ArrayList<CalmanEvent> subeventList = event.getSubevents(conn);
    for (CalmanEvent de : subeventList) {
      AjaxLink subLink = new AjaxLink(subevents.newChildId()) {
        @Override
        public void onClick(AjaxRequestTarget target) {
          try {
            changeDetails(target, de, currentUser);
          } catch (SQLException ex) {
            changeDetails(target, ex);
          }
        }
      };
      de.getAttributeModifiers().stream().forEach(am -> subLink.add(am));
      subLink.add(new Label(SUB_TITLE_ID, new Model(de.getTitle())));
      subLink.add(new Label(SUB_DESCRIPTION_ID, new Model(de.getStatus())));
      subLink.add(new Label(SUB_DATE_ID, new Model(de.getCalendar().get(Calendar.DAY_OF_MONTH) + "/"
          + (de.getCalendar().get(Calendar.MONTH) + 1) + "/"
          + de.getCalendar().get(Calendar.YEAR))));
      //subLink.add(new AttributeModifier("style", new Model(de.getColor().getStyleAttr())));
      subevents.add(subLink);
    }
    target.add(subLinkCont);

    editEvent = new AjaxLink(EDIT_EVENT_ID) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        AddEvent newPage = new AddEvent(BasePage.PANEL_ID, dbProps, SidePanel.this, basePage, event);
        basePage.activePanels.remove(BasePage.PageType.ADDEVENT);
        basePage.replace(newPage);
        target.add(basePage);
      }

      @Override
      public void renderHead(IHeaderResponse response) {
        response
            .render(OnDomReadyHeaderItem
                .forScript(new StringBuilder()
                    .append("document.getElementById(\"")
                    .append(this.getMarkupId())
                    .append("\").value=\"")
                    .append("Bewerken")
                    .append("\";")
                    .toString()));
        super.renderHead(response);
      }
    };
    editEvent.setOutputMarkupPlaceholderTag(true);
    editEventCont.addOrReplace(editEvent);
    editEventCont.setVisible(currentUser.getRole() > 1);
    target.add(editEventCont);

  }

  public void changeDetails(AjaxRequestTarget target, Exception e) {
    Label newLabel = new Label(DESCRIPTION_ID, e.getMessage());
    description.replaceWith(newLabel);
    newLabel.setOutputMarkupId(true);
    target.add(newLabel);
    description = newLabel;
  }

}
