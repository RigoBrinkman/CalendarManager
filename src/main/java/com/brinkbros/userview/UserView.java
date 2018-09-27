package com.brinkbros.userview;

import com.brinkbros.BasePage;
import com.brinkbros.CalmanUser;
import com.brinkbros.DatabaseConnector;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;

public class UserView extends Panel {

  private static final String USER_ID = "user";
  private static final String USER_LINK_ID = "userLink";
  private static final String NAME_ID = "name";
  private static final String NEW_USER_FORM_ID = "newUserForm";
  private static final String MAIL_FIELD_ID = "mailField";
  private static final String NAME_FIELD_ID = "nameField";
  private static final String ROLE_SELECT_ID = "roleSelect";
  private static final String NEW_USER_SUBMIT_ID = "newUserSubmit";
  private static final String ADD_USER_ID = "addUser";

  private static final ArrayList<String> ROLE_LIST;
  static {
    ROLE_LIST = new ArrayList(3);
    ROLE_LIST.add("Gebruiker");
    ROLE_LIST.add("Management");
    ROLE_LIST.add("Administrator");
  }

  private final Properties dbProps;
  private final BasePage basePage;

  public UserView(String id, Properties dbProps, BasePage basePage) {
    super(id);
    this.dbProps = dbProps;
    this.basePage = basePage;

    ArrayList<CalmanUser> userList = CalmanUser.getUsers();
    userList.sort((cu1, cu2) -> cu1.getName().compareTo(cu2.getName()));

    ListView users = new ListView<CalmanUser>(USER_ID, userList) {
      @Override
      protected void populateItem(ListItem<CalmanUser> item) {
        CalmanUser user = item.getModelObject();
        AjaxLink link = new AjaxLink(USER_LINK_ID) {
          @Override
          public void onClick(AjaxRequestTarget target) {
          }
        };
        item.add(link);
        link.add(new Label(NAME_ID, user.getName()));
      }
    };
    users.setOutputMarkupId(true);
    add(users);

    Form newUserForm = new Form(NEW_USER_FORM_ID);
    newUserForm.setOutputMarkupPlaceholderTag(true);
    add(newUserForm);
    newUserForm.setVisible(false);
    {
      TextField mailField = new TextField(MAIL_FIELD_ID);
      mailField.setOutputMarkupPlaceholderTag(true);
      newUserForm.add(mailField);

      TextField nameField = new TextField(NAME_FIELD_ID);
      nameField.setOutputMarkupPlaceholderTag(true);
      newUserForm.add(nameField);

      DropDownChoice<String> roleSelect = new DropDownChoice(ROLE_SELECT_ID, new Model(ROLE_LIST.get(0)), ROLE_LIST);
      roleSelect.setOutputMarkupPlaceholderTag(true);
      newUserForm.add(roleSelect);

      Button newUserSubmit = new Button(NEW_USER_SUBMIT_ID);
      newUserSubmit.setOutputMarkupPlaceholderTag(true);
      newUserForm.add(newUserSubmit);
      newUserSubmit.add(new IValidator() {
        @Override
        public void validate(IValidatable validatable) {
          try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps)) {
            conn.createStatement().executeUpdate(
                "INSERT INTO PF_USERS");
          } catch (SQLException ex) {
            validatable.error(new IValidationError() {
              @Override
              public Serializable getErrorMessage(IErrorMessageSource messageSource) {
                return ex.getMessage();
              }
            });
          }
        }
      });
    }
    AjaxLink addUser = new AjaxLink(ADD_USER_ID) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        newUserForm.setVisible(true);
        target.add(newUserForm);
        
        this.setVisible(false);
        target.add(this);
      }
      @Override
      protected void onComponentTag(final ComponentTag tag) {
        tag.put("value", "Gebruiker Toevoegen");
        super.onComponentTag(tag);
      }
    };
    addUser.setOutputMarkupPlaceholderTag(true);
    add(addUser);

  }

}
