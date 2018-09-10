package com.brinkbros;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class CalendarManager extends WebApplication {
  
  public static String SESSION_COOKIE_NAME = "CalendarManagerSessionCookie";
  
  @Override
  public Class<? extends Page> getHomePage() {
    return BasePage.class;
  }

}
