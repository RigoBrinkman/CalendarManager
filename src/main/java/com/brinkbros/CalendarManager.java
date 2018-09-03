package com.brinkbros;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class CalendarManager extends WebApplication {
  
  @Override
  public Class<? extends Page> getHomePage() {
    return BasePage.class;
  }

}
