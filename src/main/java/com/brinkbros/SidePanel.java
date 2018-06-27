package com.brinkbros;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final String TITLE_ID = "top";
    private static final String DATE_ID = "top2";
    private static final String DESCRIPTION_ID = "top3";
    private static final String CATEGORY_ID = "top4";
    private static final String TYPE_ID = "top5";
    private static final String STATUS_ID = "top6";
    private static final String SUBEVENT_ID = "subevent";
    private static final String SUB_LINK_ID = "subLink";
    private static final String SUB_TITLE_ID = "subTitle";
    private static final String SUB_DESCRIPTION_ID = "subDescription";
    private static final String SUB_DATE_ID = "subDate";

    public abstract Properties getDBProps();

    Label title;
    Label date;
    Label description;
    Label category;
    Label type;
    Label status;

    RepeatingView subevents;
    WebMarkupContainer subLinkCont;

    protected SidePanel(String id) {
        super(id);

        title = new Label(TITLE_ID, new Model("Titel"));
        title.setOutputMarkupId(true);
        add(title);

        date = new Label(DATE_ID, new Model("Datum"));
        date.setOutputMarkupId(true);
        add(date);

        description = new Label(DESCRIPTION_ID, new Model("Details"));
        description.setOutputMarkupId(true);
        add(description);

        category = new Label(CATEGORY_ID, new Model("Categorie"));
        category.setOutputMarkupId(true);
        add(category);

        type = new Label(TYPE_ID, new Model("Type"));
        type.setOutputMarkupId(true);
        add(type);

        status = new Label(STATUS_ID, new Model("Status"));
        status.setOutputMarkupId(true);
        add(status);

        subLinkCont = new WebMarkupContainer(SUBEVENT_ID);
        subLinkCont.setOutputMarkupId(true);
        subevents = new RepeatingView(SUB_LINK_ID);
        subevents.setOutputMarkupId(true);
        subLinkCont.add(subevents);
        add(subLinkCont);

    }

    public void changeDetails(AjaxRequestTarget target, DateEvent event) throws SQLException {

        title.setDefaultModelObject(event.getTitle());
        target.add(title);

        date.setDefaultModelObject(event.getCalendar().get(Calendar.DAY_OF_MONTH) + "/"
                + event.getCalendar().get(Calendar.MONTH) + "/"
                + event.getCalendar().get(Calendar.YEAR));
        target.add(date);

        description.setDefaultModelObject(event.getDescription());
        target.add(description);

        category.setDefaultModelObject(event.getCategory());
        target.add(category);

        type.setDefaultModelObject(event.getType());
        target.add(type);

        status.setDefaultModelObject(event.getStatus());
        target.add(status);

        subevents.removeAll();
        ArrayList<DateEvent> subeventList = event.getSubevents(getDBProps());
        for (DateEvent de : subeventList) {
            AjaxLink subLink = new AjaxLink(subevents.newChildId()) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try{
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
            subevents.add(subLink);
        }
        target.add(subLinkCont);
    }

    public void changeDetails(AjaxRequestTarget target, String newDetails) {
        Label newLabel = new Label(TITLE_ID, newDetails);
        description.replaceWith(newLabel);
        newLabel.setOutputMarkupId(true);
        target.add(newLabel);
        description = newLabel;
    }

}
