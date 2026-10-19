package ch.want.mylyn.youtrack.model;

public class ProjectCustomField {

    private String id;
    private CustomField field;
    private EnumBundle bundle;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public CustomField getField() {
        return field;
    }

    public void setField(final CustomField field) {
        this.field = field;
    }

    public EnumBundle getBundle() {
        return bundle;
    }

    public void setBundle(final EnumBundle bundle) {
        this.bundle = bundle;
    }
}
