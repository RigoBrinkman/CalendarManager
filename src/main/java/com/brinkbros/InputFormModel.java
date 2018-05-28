package com.brinkbros;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.wicket.util.io.IClusterable;

public class InputFormModel implements IClusterable {

    private URL urlProperty;
    private String radioGroup1;
    private String textField1 = "text";

    public InputFormModel() {
        try {
            urlProperty = new URL("http://localhost:8084/CalendarManager");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Saved data:").append("\n")
                .append("URL: ").append(urlProperty).append("\n")
                .append("Radio group: ").append(radioGroup1).append("\n")
                .append("Text field: ").append(textField1).append("\n");
        return sb.toString();
    }

    public String getRadioGroup1() {
        return radioGroup1;
    }

    public String getTextField1() {
        return textField1;
    }

    public URL getUrlProperty() {
        return urlProperty;
    }

    public void setRadioGroup1(String radioGroup1) {
        this.radioGroup1 = radioGroup1;
    }

    public void setTextField1(String textField1) {
        this.textField1 = textField1;
    }

    public void setUrlProperty(URL urlProperty) {
        this.urlProperty = urlProperty;
    }

}
