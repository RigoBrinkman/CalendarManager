package com.brinkbros.login;

import com.brinkbros.BasePage;
import com.brinkbros.CalmanUser;
import com.brinkbros.DatabaseConnector;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;

public class LoginPanel extends Panel {

  private static final String LOGIN_FORM_ID = "loginForm";
  private static final String USERNAME_FIELD_ID = "usernameField";
  private static final String PASSWORD_FIELD_ID = "passwordField";
  private static final String LOGIN_SUBMIT_ID = "loginSubmit";
  private static final String FEEDBACK_ID = "feedback";

  private final BasePage basePage;

  public LoginPanel(String id, BasePage basePage) {
    super(id);

    this.basePage = basePage;

    Form loginForm = new Form(LOGIN_FORM_ID);
    loginForm.setOutputMarkupId(true);
    add(loginForm);

    TextField usernameField = new TextField(USERNAME_FIELD_ID, new Model());
    usernameField.setOutputMarkupId(true);
    loginForm.add(usernameField);

    PasswordTextField passwordField = new PasswordTextField(PASSWORD_FIELD_ID, new Model());
    passwordField.setOutputMarkupId(true);
    loginForm.add(passwordField);

    Button loginSubmit = new Button(LOGIN_SUBMIT_ID) {
      @Override
      public void onSubmit() {
        basePage.showButtons();
        basePage.resetPanels();
      }
    };
    loginSubmit.setOutputMarkupId(true);
    loginSubmit.add((IValidatable<String> validatable) -> {
      try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), basePage.getDbProps());
          Statement stmnt = conn.createStatement();
          ResultSet rslts = stmnt.executeQuery("SELECT * FROM PF_USERS "
              + "WHERE mail = '" + usernameField.getInput()
              + "' AND password = MD5('" + passwordField.getInput() + "')")) {
        if(rslts.next()){
          basePage.setCurrentUser(CalmanUser.getUser(rslts.getInt(1)));
        }else{
          validatable.error(new IValidationError() {
            @Override
            public Serializable getErrorMessage(IErrorMessageSource messageSource) {
              return "Incorrecte login gegevens";
            }
          });
        }
      } catch (SQLException ex) {
        validatable.error(new IValidationError() {
          @Override
          public Serializable getErrorMessage(IErrorMessageSource messageSource) {
            return "SQLException: " + ex.getMessage();
          }
        });
      }
    });
    loginForm.add(loginSubmit);
    
    FeedbackPanel feedback = new FeedbackPanel(FEEDBACK_ID);
    feedback.setOutputMarkupId(true);
    add(feedback);

  }

}
