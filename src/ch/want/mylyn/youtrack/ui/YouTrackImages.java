package ch.want.mylyn.youtrack.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.want.mylyn.youtrack.PluginConstants;

public class YouTrackImages {

    public static final ImageDescriptor OVERLAY_BUG = create("icons/overlay-bug.png");
    public static final ImageDescriptor OVERLAY_FEATURE = create("icons/overlay-feature.png");
    public static final ImageDescriptor OVERLAY_TASK = create("icons/overlay-task.png");

    private YouTrackImages() {
    }

    private static ImageDescriptor create(final String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PluginConstants.PLUGIN_ID, path);
    }
}
