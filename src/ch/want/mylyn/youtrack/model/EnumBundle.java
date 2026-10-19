package ch.want.mylyn.youtrack.model;

import java.util.List;

public class EnumBundle {

    private String id;
    private List<EnumBundleElement> values;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public List<EnumBundleElement> getValues() {
        return values;
    }

    public void setValues(final List<EnumBundleElement> values) {
        this.values = values;
    }
}
