package com.brinkbros;

import java.util.ArrayList;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

public class BasePage extends WebPage {

    private static final long serialVersionUID = 1L;

    public BasePage(final PageParameters parameters) {
        super(parameters);

        add(new ButtonForm("buttons"));
        add(new Overview("mycal"));
        add(new FeedbackPanel("feedback"));

    }

    private class ButtonForm extends Form {

        private ButtonForm(String name) {
            super(name);
            //to be replaced by a method that checks what buttons a user is allowed to see
            ArrayList<PageType> buttonList = new ArrayList();
//            buttonList.add(PageType.INPUTFORM);
            buttonList.add(PageType.OVERVIEW);

            ListView<PageType> options = new ListView<PageType>("buttonRow", buttonList) {
                @Override
                protected void populateItem(ListItem<PageType> item) {
                    Button button;
                    switch (item.getModelObject()) {
                        case INPUTFORM:
                            button = new Button("button", new Model(item.getModelObject().getName())) {
                                @Override
                                public void onSubmit() {
                                    BasePage.this.remove("mycal");
                                    BasePage.this.add(new InputFormPanel("mycal"));
                                }
                            };
                            break;

                        case OVERVIEW:
                            button = new Button("button", new Model(item.getModelObject().getName())) {
                                @Override
                                public void onSubmit() {
                                    BasePage.this.remove("mycal");
                                    BasePage.this.add(new Overview("mycal"));
                                }
                            };
                            break;

                        default:
                            throw new IllegalArgumentException("Unknown ButtonType!");
                    }

                    button.setLabel(
                            new Model(item.getModelObject().getName()));
                    item.add(button);

                }
            };
            add(options);

        }

    }

    private enum PageType {

        INPUTFORM("InputForm"),
        OVERVIEW("Overview");
        private final String name;

        private PageType(String name) {
            this.name = name;
        }

        private String getName() {
            return name;
        }
    }

}
