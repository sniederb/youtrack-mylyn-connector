package ch.want.mylyn.youtrack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class IssueCustomField {

    private String id;
    private String name;
    @JsonProperty("$type")
    private String fieldType;
    private ProjectCustomField projectCustomField;
    private JsonNode value;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ProjectCustomField getProjectCustomField() {
        return projectCustomField;
    }

    public void setProjectCustomField(final ProjectCustomField projectCustomField) {
        this.projectCustomField = projectCustomField;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(final JsonNode value) {
        this.value = value;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(final String fieldType) {
        this.fieldType = fieldType;
    }
}