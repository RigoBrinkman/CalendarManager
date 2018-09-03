package settings;

import com.brinkbros.DatabaseConnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class Settings extends Panel {

  private static final String PASSWORD_FORM_ID = "passwordForm";
  private static final String PASSWORD_FIELD_ID = "passwordField";
  private static final String PASSWORD_SUBMIT_ID = "passwordSubmit";

  private Properties dbProps;

  public Settings(String id, Properties dbProps) {
    super(id);
    this.dbProps = dbProps;

    Form passwordForm = new Form(PASSWORD_FORM_ID);
    passwordForm.setOutputMarkupId(true);
    add(passwordForm);
    {
      PasswordTextField passwordField = new PasswordTextField(PASSWORD_FIELD_ID, new Model()) {
        @Override
        protected void onComponentTag(final ComponentTag tag) {
          tag.put("placeholder", "Nieuw wachtwoord");
          super.onComponentTag(tag);
        }
      };
      passwordField.setOutputMarkupId(true);
      passwordForm.add(passwordField);

      Button passwordSubmit = new Button(PASSWORD_SUBMIT_ID, new Model("Wijzig wachtwoord")) {
        @Override
        public void onSubmit() {
          try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps);
              Statement stmnt = conn.createStatement()) {
            stmnt.executeUpdate("UPDATE PF_USERS SET password = MD5('" + passwordField.getInput() + "') WHERE user_id = 1");
          } catch (SQLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      };
      passwordSubmit.setOutputMarkupId(
          true);
      passwordForm.add(passwordSubmit);
    }
  }

}
