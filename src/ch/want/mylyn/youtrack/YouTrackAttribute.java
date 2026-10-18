package ch.want.mylyn.youtrack;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public enum YouTrackAttribute {

    ID("youtrack.id", TaskAttribute.TYPE_SHORT_TEXT, "ID", true, true), //
    KEY(TaskAttribute.TASK_KEY, TaskAttribute.TYPE_SHORT_TEXT, "Key", false, true), //
    PRIORITY(TaskAttribute.PRIORITY, TaskAttribute.TYPE_SHORT_TEXT, "Priority", false, true), //
    TYPE(TaskAttribute.TASK_KIND, TaskAttribute.TYPE_SHORT_TEXT, "Type", true, true), //
    STATUS(TaskAttribute.STATUS, TaskAttribute.TYPE_SHORT_TEXT, "Status", false, true), //
    SUMMARY(TaskAttribute.SUMMARY, TaskAttribute.TYPE_SHORT_RICH_TEXT, "Summary", true, false), //
    DESCRIPTION(TaskAttribute.DESCRIPTION, TaskAttribute.TYPE_LONG_RICH_TEXT, "Description", true, false), //
    CREATION_DATE(TaskAttribute.DATE_CREATION, TaskAttribute.TYPE_DATE, "Created", false, true), //
    MODIFIED_DATE(TaskAttribute.DATE_MODIFICATION, TaskAttribute.TYPE_DATE, "Modified", false, true), //
    TASK_URL(TaskAttribute.TASK_URL, TaskAttribute.TYPE_URL, "URL", false, false), //
    REPORTER(TaskAttribute.USER_REPORTER, TaskAttribute.TYPE_PERSON, "Reported", false, true), //
    ASSIGNED(TaskAttribute.USER_ASSIGNED, TaskAttribute.TYPE_PERSON, "Assigned", false, true), //
    PROJECT(TaskAttribute.PRODUCT, TaskAttribute.TYPE_SINGLE_SELECT, "Project", false, true), //
    NEWCOMMENT(TaskAttribute.COMMENT_NEW, TaskAttribute.TYPE_LONG_RICH_TEXT, "New comment", true, false);

    private final String id;
    private final boolean hidden;
    private final boolean readOnly;
    private final String label;
    private final String controlType; // TaskAttribute.TYPE_SHORT_TEXT

    YouTrackAttribute(final String id, final String controlType, final String label, final boolean hidden, final boolean readOnly) {
        this.id = id;
        this.controlType = controlType;
        this.label = label;
        this.hidden = hidden;
        this.readOnly = readOnly;
    }

    public String id() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getControlType() {
        return controlType;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Returns {@link TaskAttribute#KIND_DEFAULT} for attributes in the standard "attributes" section, null for all others
     *
     * @return
     */
    public String getKind() {
        return hidden ? null : TaskAttribute.KIND_DEFAULT;
    }
}
