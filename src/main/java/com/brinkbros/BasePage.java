package com.brinkbros;

import com.brinkbros.cancelledview.CancelledView;
import com.brinkbros.BasePage.PageType;
import com.brinkbros.overview.Overview;
import com.brinkbros.addevent.AddEvent;
import com.brinkbros.deadlinesview.DeadlinesView;
import com.brinkbros.login.LoginPanel;
import com.brinkbros.userview.UserView;
import com.brinkbros.yearview.YearView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import settings.Settings;

public class BasePage extends WebPage {

  private static final long serialVersionUID = 1L;

  public static final String PANEL_ID = "mycal";
  private static final String SIDE_PANEL_ID = "sidePanel";
  private static final String BUTTON_LIST_ID = "buttonList";
  private static final String BUTTON_CONTAINER_ID = "buttonContainer";
  private static final String BUTTON_ID = "buttonOption";
  private static final String USER_INFO_ID = "userInfo";
  private static final String DATE_ID = "date";
  private static final String ROLE_ID = "role";
  private static final String NAME_ID = "name";

  public HashMap<PageType, Panel> activePanels;
  private PageType startPage;

  private final Properties dbProps;

  private SidePanel sidePanel;
  private HashMap<PageType, AjaxLink> buttonMap;
  private ListView<PageType> options;
  private AjaxLink clickedButton;
  private WebMarkupContainer container;
  private WebMarkupContainer userInfo;
  private CalmanUser currentUser;

  public BasePage(final PageParameters parameters) {
    super(parameters);
    dbProps = new Properties();
    dbProps.put("user", "thecorz0_planner");
    dbProps.put("password", "PFSPO2018test");
    dbProps.put("useLegacyDatetimeCode", "false");
    dbProps.put("serverTimezone", "UTC");
    try (Connection conn = DriverManager.getConnection(DatabaseConnector.getDbUrl(), dbProps)) {
      CalmanUser.initialize(conn);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }

    sidePanel = new SidePanel(SIDE_PANEL_ID, dbProps, this);
    sidePanel.setOutputMarkupPlaceholderTag(true);
    add(sidePanel);

    container = new WebMarkupContainer(BUTTON_CONTAINER_ID);
    container.setOutputMarkupPlaceholderTag(true);
    add(container);

    userInfo = new WebMarkupContainer(USER_INFO_ID);
    userInfo.add(new Label(DATE_ID));
    userInfo.add(new Label(ROLE_ID));
    userInfo.add(new Label(NAME_ID));
    userInfo.setOutputMarkupId(true);
    container.add(userInfo);

    options = new ListView(BUTTON_LIST_ID, new ArrayList()) {
      @Override
      protected void populateItem(ListItem item) {
      }
    };
    options.setOutputMarkupId(true);
    container.add(options);

    //TODO build Cookie functionality to see if someone should already be logged in
    if (currentUser == null) {
      startPage = PageType.LOGIN;
    } else {
      startPage = PageType.OVERVIEW;
      showButtons();
    }

    activePanels = new HashMap();
    activePanels.put(startPage, startPage.getPanel(dbProps, sidePanel, this, currentUser));
    add(activePanels.get(startPage));

  }

  public void showButtons() {

    buttonMap = new HashMap();

    options = new ListView<PageType>(BUTTON_LIST_ID, Arrays.asList(PageType.values())) {
      @Override
      protected void populateItem(ListItem<PageType> item) {
        item.setOutputMarkupId(true);
        PageType buttonType = item.getModelObject();
        AjaxLink button = new AjaxLink(BUTTON_ID) {
          @Override
          public void onClick(AjaxRequestTarget target) {
            if (!activePanels.containsKey(buttonType)) {
              activePanels.put(buttonType, buttonType.getPanel(dbProps, sidePanel, BasePage.this, currentUser));
            }
            Panel newPanel = activePanels.get(buttonType);
            BasePage.this.get(PANEL_ID).replaceWith(newPanel);
            target.add(newPanel);
            sidePanel.setVisible(buttonType.isSidePanelVisible());
            target.add(sidePanel);
            /*
             clickedButton.setEnabled(true);
             target.add(clickedButton);
             clickedButton = this;
             clickedButton.setEnabled(false);
             target.add(clickedButton);
             */

          }

          @Override
          public void renderHead(IHeaderResponse response) {
            response.render(OnDomReadyHeaderItem.forScript(buttonType.getValueJavascript(getMarkupId())));
            super.renderHead(response);
          }

        };
        /*
         if (buttonType == START_PAGE) {
         clickedButton = button;
         clickedButton.setEnabled(false);
         }
         */
        button.setOutputMarkupId(true);
        button.setOutputMarkupPlaceholderTag(true);
        button.setVisible(true);
        buttonMap.put(buttonType, button);

        item.add(button);
      }
    };
    options.setOutputMarkupPlaceholderTag(true);
    options.setVisible(currentUser != null);
    container.addOrReplace(options);
    //buttonMap.forEach((x, y) -> {y.setVisible(x.isButtonVisible(role));});
    options.setModelObject(options
        .getModelObject()
        .stream()
        .filter(pt -> pt.isButtonVisible(currentUser.getRole()))
        .collect(Collectors.toCollection(ArrayList<PageType>::new)));
    options.setVisible(true);

    WebMarkupContainer userInfo = new WebMarkupContainer(USER_INFO_ID);
    userInfo.setOutputMarkupId(true);
    container.replace(userInfo);

    Calendar today = Calendar.getInstance();

    Label dateLabel = new Label(DATE_ID, new Model(
        today.get(Calendar.DAY_OF_MONTH)
        + "/"
        + today.get(Calendar.MONTH)
        + "/"
        + today.get(Calendar.YEAR)));
    dateLabel.setOutputMarkupId(true);
    userInfo.add(dateLabel);

    Label roleLabel;
    switch (currentUser.getRole()) {
      case 0:
        roleLabel = new Label(ROLE_ID, new Model("Gebruiker"));
        break;
      case 1:
        roleLabel = new Label(ROLE_ID, new Model("Management"));
        break;
      case 2:
        roleLabel = new Label(ROLE_ID, new Model("Administrator"));
        break;
      default:
        roleLabel = new Label(ROLE_ID, "ONBEKENDE ROL");
    }
    roleLabel.setOutputMarkupId(true);
    userInfo.add(roleLabel);

    Label nameLabel = new Label(NAME_ID, new Model(currentUser.getName()));
    nameLabel.setOutputMarkupId(true);
    userInfo.add(nameLabel);
  }

  public CalmanUser getCurrentUser() {
    return currentUser;
  }

  public Properties getDbProps() {
    return dbProps;
  }

  public void setCurrentUser(CalmanUser currentUser) {
    this.currentUser = currentUser;
  }

  /** Resets the main panel and side panel. This method Should only be called when re-rendering the page!*/
  public void resetPanels() {
    SidePanel newSidePanel = new SidePanel(SIDE_PANEL_ID, dbProps, this);
    this.sidePanel.replaceWith(newSidePanel);
    this.sidePanel = newSidePanel;
    this.activePanels.clear();
    Panel newOverviewPanel = PageType.OVERVIEW.getPanel(dbProps, sidePanel, this, currentUser);
    this.get(PANEL_ID).replaceWith(newOverviewPanel);
    this.activePanels.put(PageType.OVERVIEW, newOverviewPanel);
  }

  public enum PageType {

    LOGIN("Inloggen", "loginButton", false) {
          @Override
          protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            return new LoginPanel(panelId, basePage);
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return false;
          }
        },
    OVERVIEW("Overzicht", "overviewButton", true) {
          @Override
          public Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            return new Overview(panelId, dbProps, sidePanel, currentUser);
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return true;
          }

        },
    YEARVIEW("JaarOverzicht", "yearviewButton", true) {
          @Override
          protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            return new YearView(panelId, dbProps, sidePanel, currentUser);
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return true;
          }
        },
    ADDEVENT("Nieuw evenement", "addEventButton", true) {
          @Override
          protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            return new AddEvent(panelId, dbProps, sidePanel, basePage);
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return role == 2;
          }

        },
    DEADLINES("Deadlines", "deadlinesButton", true) {
          @Override
          protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            return new DeadlinesView(panelId, dbProps, sidePanel, currentUser);
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return true;
          }
        },
    CANCELLEDVIEW("Geannuleerd", "cancelledButton", true) {
          @Override
          protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            return new CancelledView(panelId, dbProps, sidePanel, currentUser);
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return role == 2;
          }
        },
    SETTINGS("Instellingen", "settingsButton", true) {
          @Override
          protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            return new Settings(panelId, dbProps);
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return true;
          }

        },
    USERS("Gebruikers", "userButton", true) {
          @Override
          protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            return new UserView(panelId, dbProps, basePage);
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return role == 2;
          }
        },
    LOGOUT("Uitloggen", "logoutButton", true) {
          @Override
          protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
            basePage.getSession().invalidateNow();
            return null;
          }
          @Override
          protected boolean isButtonVisible(int role) {
            return true;
          }
        };
    private final String id;
    private final String name;
    private final boolean isSidePanelVisible;

    protected abstract Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser);
    protected abstract boolean isButtonVisible(int role);

    private PageType(String name, String id, boolean isSidePanelVisible) {
      this.name = name;
      this.id = id;
      this.isSidePanelVisible = isSidePanelVisible;
    }

    public String getValueJavascript(String markupId) {
      StringBuilder javascript = new StringBuilder();
      javascript.append("document.getElementById(\"")
          .append(markupId)
          .append("\").value=\"")
          .append(getName())
          .append("\";");
      return javascript.toString();
    }

    public Panel getPanel(Properties dbProps, SidePanel sidePanel, BasePage basePage, CalmanUser currentUser) {
      Panel panel = createPanel(PANEL_ID, dbProps, sidePanel, basePage, currentUser);
      panel.setOutputMarkupId(true);
      return panel;
    }

    public String getName() {
      return name;
    }

    public String getId() {
      return id;
    }

    public boolean isSidePanelVisible() {
      return isSidePanelVisible;
    }

  }

}
