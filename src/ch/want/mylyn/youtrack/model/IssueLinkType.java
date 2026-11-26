package ch.want.mylyn.youtrack.model;

public class IssueLinkType {

    private String id;
    private String name;
    private String localizedName;
    private String sourceToTarget;
    private String localizedSourceToTarget;
    private String targetToSource;
    private String localizedTargetToSource;
    private Boolean directed;
    private Boolean aggregation;
    private Boolean readOnly;

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

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(final String localizedName) {
        this.localizedName = localizedName;
    }

    public String getSourceToTarget() {
        return sourceToTarget;
    }

    public void setSourceToTarget(final String sourceToTarget) {
        this.sourceToTarget = sourceToTarget;
    }

    public String getLocalizedSourceToTarget() {
        return localizedSourceToTarget;
    }

    public void setLocalizedSourceToTarget(final String localizedSourceToTarget) {
        this.localizedSourceToTarget = localizedSourceToTarget;
    }

    public String getTargetToSource() {
        return targetToSource;
    }

    public void setTargetToSource(final String targetToSource) {
        this.targetToSource = targetToSource;
    }

    public String getLocalizedTargetToSource() {
        return localizedTargetToSource;
    }

    public void setLocalizedTargetToSource(final String localizedTargetToSource) {
        this.localizedTargetToSource = localizedTargetToSource;
    }

    public Boolean getDirected() {
        return directed;
    }

    public void setDirected(final Boolean directed) {
        this.directed = directed;
    }

    public Boolean getAggregation() {
        return aggregation;
    }

    public void setAggregation(final Boolean aggregation) {
        this.aggregation = aggregation;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(final Boolean readOnly) {
        this.readOnly = readOnly;
    }
}