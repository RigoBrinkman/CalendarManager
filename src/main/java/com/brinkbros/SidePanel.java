package com.brinkbros;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class SidePanel extends Panel {
    
    private static final String HEAD_ID = "top";
    Label details;

    protected SidePanel(String id) {
        super(id);
        details = new Label(HEAD_ID, "Details");
        details.setOutputMarkupId(true);
        add(details);
    }
    
    public void changeDetails(AjaxRequestTarget target, DateEvent event){
        Label newLabel = new Label(HEAD_ID, event.getDescription());
        details.replaceWith(newLabel);
        newLabel.setOutputMarkupId(true);
        target.add(newLabel);
        details = newLabel;
    }
    
    public void changeDetails(AjaxRequestTarget target, String newDetails){
        Label newLabel = new Label(HEAD_ID, newDetails);
        details.replaceWith(newLabel);
        newLabel.setOutputMarkupId(true);
        target.add(newLabel);
        details = newLabel;
    }

}
