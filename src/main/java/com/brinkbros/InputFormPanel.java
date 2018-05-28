package com.brinkbros;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;


public class InputFormPanel extends Panel {

    public InputFormPanel(String id) {
        super(id);
        add(new InputForm("inputForm"));
    }

    private class InputForm extends Form<InputFormModel> {

        private List<String> numbers;
        IModel model;

        public InputForm(String name) {
            super(name, new CompoundPropertyModel<>(new InputFormModel()));
            setEscapeModelStrings(false);

            add(new TextField("textField1")
                    .setLabel(LambdaModel
                            .of(getModel(), (x) -> x.getTextField1())));

            RadioGroup rGroup = new RadioGroup("radioGroup1");
            add(rGroup);

            numbers = new ArrayList();
            numbers.add("1");
            numbers.add("2");
            numbers.add("3");

            ListView<String> options
                    = new ListView<String>("numbers", numbers) {
                        @Override
                        protected void populateItem(ListItem<String> li) {
                            Radio<String> radio = new Radio<>("radio", li.getModel());
                            radio.setLabel(li.getModel());
                            li.add(radio);
                            li.add(new SimpleFormComponentLabel("number", radio));
                        }

                    };
            rGroup.add(options);

            add(new Button("saveButton") {
                @Override
                public void onSubmit() {
                    info(getForm().getModelObject().toString());
                }
            });

            add(new Button("resetButton") {
                @Override
                public void onSubmit() {
                    setResponsePage(BasePage.class);
                    info("RESET");
                }
            });

        }
    }
}
