package ch.want.mylyn.youtrack.model;

import com.fasterxml.jackson.databind.JsonNode;

public class CustomField {

    private String name;
    private JsonNode value;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(final JsonNode value) {
        this.value = value;
    }
}