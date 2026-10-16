package ch.want.mylyn.youtrack;

public final class PluginConstants {

    public static final String TASKDATA_VERSION = "1.0";
    public static final String YOUTRACK_LABEL = "YouTrack";
    /**
     * Must match plugin.xml / connectorCore/@id
     */
    public static final String CONNECTOR_KIND = "youtrack";
    public static final String PLUGIN_ID = "ch.want.mylyn.youtrack";
    public static final String ATTRIBUTE_SAVED_QUERY_ID = "youtrack.savedQueryId";
    public static final String ISSUE_URL_PATH = "/issue/"; // NOSONAR

    private PluginConstants() {
    }
}
