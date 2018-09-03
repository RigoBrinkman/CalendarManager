package com.brinkbros.yearview;

import com.brinkbros.CalmanDate;
import com.brinkbros.CalmanEvent;
import com.brinkbros.SidePanel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

public class YearView extends Panel {

  private static final String YEAR_LABEL_ID = "yearLabel";
  private static final String TABLE_CONTAINER_ID = "tableContainer";
  private static final String MONTH_TABLE_ID = "monthTable";
  private static final String MONTH_NAME_ID = "monthName";
  private static final String DAY_ROW_ID = "dayRow";
  private static final String DAY_NO_ID = "dayNo";
  private static final String EVENTS_CELL_ID = "events";
  private static final String EVENT_LINK_ID = "eventLink";
  private static final String EVENT_ID = "eventLabel";
  private static final String PREVIOUS_ID = "previous";
  private static final String GOTO_NOW_ID = "gotoNow";
  private static final String NEXT_ID = "next";

  private static final String PREVIOUS_VALUE = "Vorig jaar";
  private static final String GOTO_NOW_VALUE = "Huidig jaar";
  private static final String NEXT_VALUE = "Volgend jaar";

  private Properties dbProps;
  private SidePanel sidePanel;
  private Label yearLabel;
  private WebMarkupContainer tableContainer;
  private int year;
  private YearviewYear yvYear;
  private DataView months;

  public YearView(String id, Properties dbProps, SidePanel sidePanel) {
    super(id);
    this.dbProps = dbProps;
    this.sidePanel = sidePanel;
    year = Calendar.getInstance().get(Calendar.YEAR);

    yearLabel = new Label(YEAR_LABEL_ID, new Model(String.valueOf(year)));
    yearLabel.setOutputMarkupId(true);
    add(yearLabel);

    tableContainer = new WebMarkupContainer(TABLE_CONTAINER_ID);
    tableContainer.setOutputMarkupId(true);
    add(tableContainer);
    {
      yvYear = YearviewYear.build(year, dbProps);

      months = getMonthsDataView(yvYear);
      months.setOutputMarkupId(true);
      tableContainer.add(months);
    }
    add(new AjaxLink(PREVIOUS_ID) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        year--;
        yearLabel.setDefaultModelObject(String.valueOf(year));
        yvYear = YearviewYear.build(year, dbProps);
        tableContainer.get(MONTH_TABLE_ID).replaceWith(getMonthsDataView(yvYear));
        target.add(tableContainer);
        target.add(yearLabel);
      }

      @Override
      public void renderHead(IHeaderResponse response) {
        response
            .render(OnDomReadyHeaderItem
                .forScript(new StringBuilder()
                    .append("document.getElementById(\"")
                    .append(getMarkupId())
                    .append("\").value=\"")
                    .append(PREVIOUS_VALUE)
                    .append("\";")
                    .toString()));
        super.renderHead(response);
      }

    }).setOutputMarkupId(true);

    add(new AjaxLink(GOTO_NOW_ID) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        year = Calendar.getInstance().get(Calendar.YEAR);
        yearLabel.setDefaultModelObject(String.valueOf(year));
        yvYear = YearviewYear.build(year, dbProps);
        tableContainer.get(MONTH_TABLE_ID).replaceWith(getMonthsDataView(yvYear));
        target.add(tableContainer);
        target.add(yearLabel);
      }

      @Override
      public void renderHead(IHeaderResponse response) {
        response
            .render(OnDomReadyHeaderItem
                .forScript(new StringBuilder()
                    .append("document.getElementById(\"")
                    .append(getMarkupId())
                    .append("\").value=\"")
                    .append(GOTO_NOW_VALUE)
                    .append("\";")
                    .toString()));
        super.renderHead(response);
      }

    }
    );
    add(new AjaxLink(NEXT_ID) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        year++;
        yearLabel.setDefaultModelObject(String.valueOf(year));
        yvYear = YearviewYear.build(year, dbProps);
        tableContainer.get(MONTH_TABLE_ID).replaceWith(getMonthsDataView(yvYear));
        target.add(tableContainer);
        target.add(yearLabel);
      }

      @Override
      public void renderHead(IHeaderResponse response) {
        response
            .render(OnDomReadyHeaderItem
                .forScript(new StringBuilder()
                    .append("document.getElementById(\"")
                    .append(getMarkupId())
                    .append("\").value=\"")
                    .append(NEXT_VALUE)
                    .append("\";")
                    .toString()));
        super.renderHead(response);
      }

    });

  }

  private DataView getMonthsDataView(YearviewYear yvYear) {
    return new DataView<ArrayList<CalmanDate>>(MONTH_TABLE_ID, new ListDataProvider(yvYear.getMonths())) {
      @Override
      protected void populateItem(Item<ArrayList<CalmanDate>> item) {
        ArrayList<CalmanDate> month = item.getModelObject();
        item.add(new Label(MONTH_NAME_ID, YearviewYear.getMonthName(item.getIndex())));
        item.add(new DataView<CalmanDate>(DAY_ROW_ID, new ListDataProvider(month)) {
          @Override
          protected void populateItem(Item<CalmanDate> item) {
            CalmanDate date = item.getModelObject();
            item.add(new Label(DAY_NO_ID, String.valueOf(date.get(Calendar.DAY_OF_MONTH))));
            item.add(new DataView<CalmanEvent>(EVENTS_CELL_ID, new ListDataProvider(date.getEvents())) {
              @Override
              protected void populateItem(Item<CalmanEvent> item) {
                CalmanEvent event = item.getModelObject();
                AjaxLink eventLink = new AjaxLink(EVENT_LINK_ID) {
                  @Override
                  public void onClick(AjaxRequestTarget target) {
                    try {
                      sidePanel.changeDetails(target, event);
                    } catch (SQLException ex) {
                      throw new RuntimeException(ex.getMessage());
                    }
                  }
                };
                item.add(eventLink);
                Label label = new Label(EVENT_ID, event.getTitle());
                event.getAttributeModifiers().stream().forEach(am -> label.add(am));
                eventLink.add(label);

              }
            });
          }
        });
      }
    };
  }

}
