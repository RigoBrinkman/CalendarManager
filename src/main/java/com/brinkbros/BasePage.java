package com.brinkbros;

import com.brinkbros.Events.Events;
import com.brinkbros.Overview.Overview;
import com.brinkbros.addEvent.AddEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class BasePage extends WebPage {

    private static final long serialVersionUID = 1L;

    private static final String PANEL_ID = "mycal";
    private static final String SIDE_PANEL_ID = "sidePanel";
    private static final String BUTTON_LIST_ID = "buttonList";
    private static final String BUTTON_CONTAINER_ID = "buttonContainer";
    private static final String BUTTON_ID = "buttonOption";

    private HashMap<PageType, Panel> activePanels;
    private static final PageType START_PAGE = PageType.OVERVIEW;

    private final Properties dbProps;

    private final SidePanel sidePanel;

    private ListView<PageType> options;
    private AjaxLink clickedButton;

    public BasePage(final PageParameters parameters) {
        super(parameters);
        dbProps = new Properties();
        dbProps.put("user", "thecorz0_planner");
        dbProps.put("password", "PFSPO2018test");
        dbProps.put("useLegacyDatetimeCode", "false");
        dbProps.put("serverTimezone", "UTC");
        sidePanel = new SidePanel(SIDE_PANEL_ID) {
            @Override
            public Properties getDBProps() {
                return dbProps;
            }
        };
        sidePanel.setOutputMarkupId(true);
        sidePanel.setOutputMarkupPlaceholderTag(true);
        add(sidePanel);

        activePanels = new HashMap();
        activePanels.put(START_PAGE, START_PAGE.getPanel(dbProps, sidePanel));
        add(activePanels.get(START_PAGE));

        WebMarkupContainer container = new WebMarkupContainer(BUTTON_CONTAINER_ID);

        List<PageType> buttonList = Arrays.asList(PageType.values());

        options = new ListView<PageType>(BUTTON_LIST_ID, buttonList) {
            @Override
            protected void populateItem(ListItem<PageType> item) {
                PageType buttonType = item.getModelObject();
                AjaxLink button = new AjaxLink(BUTTON_ID) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (!activePanels.containsKey(buttonType)) {
                            activePanels.put(buttonType, buttonType.getPanel(dbProps, sidePanel));
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

                item.add(button);
            }
        };
        container.add(options);
        add(container);

    }

    protected void setButtonEnabled(PageType buttonType, boolean isButtonEnabled) {
    }

    public enum PageType {

        OVERVIEW("Overzicht", "overviewButton", true) {
                    @Override
                    public Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel) {
                        return new Overview(panelId) {
                            @Override
                            public Properties getDBProps() {
                                return dbProps;
                            }

                            @Override
                            public SidePanel getSidePanel() {
                                return sidePanel;
                            }
                        };
                    }

                },
        YEARVIEW("JaarOverzicht", "yearviewButton", true){
            @Override
            protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel) {
                return new YearView(panelId){
                    @Override
                    public Properties getDBProps() {
                        return dbProps;
                    }

                    @Override
                    public SidePanel getSidePanel() {
                        return sidePanel;
                    }
                };
            }
        },
        EVENTS("Evenementen", "eventsButton", true) {
                    @Override
                    public Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel) {
                        return new Events(panelId) {
                            @Override
                            public Properties getDBProps() {
                                return dbProps;
                            }

                            @Override
                            public SidePanel getSidePanel() {
                                return sidePanel;
                            }
                        };
                    }
                },
        ADDEVENT("Nieuw evenement", "addEventButton", true) {
                    @Override
                    protected Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel) {
                        return new AddEvent(panelId) {
                            @Override
                            public Properties getDBProps() {
                                return dbProps;
                            }

                            @Override
                            public SidePanel getSidePanel() {
                                return sidePanel;
                            }
                        };
                    }

                };
        private final String id;
        private final String name;
        private final boolean isSidePanelVisible;

        protected abstract Panel createPanel(String panelId, Properties dbProps, SidePanel sidePanel);

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

        public Panel getPanel(Properties dbProps, SidePanel sidePanel) {
            Panel panel = createPanel(PANEL_ID, dbProps, sidePanel);
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
