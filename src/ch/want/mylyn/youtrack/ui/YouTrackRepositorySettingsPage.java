package ch.want.mylyn.youtrack.ui;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;

import ch.want.mylyn.youtrack.PluginConstants;
import ch.want.mylyn.youtrack.core.YouTrackApi;
import ch.want.mylyn.youtrack.core.YouTrackApiImpl;

public class YouTrackRepositorySettingsPage extends AbstractRepositorySettingsPage {

    public YouTrackRepositorySettingsPage(final TaskRepository taskRepository) {
        super(PluginConstants.YOUTRACK_LABEL, "YouTrack description", taskRepository);
    }

    @Override
    protected void createAdditionalControls(final Composite parent) {
        // no-op
    }

    @Override
    public String getConnectorKind() {
        return PluginConstants.CONNECTOR_KIND;
    }

    @Override
    protected Validator getValidator(final TaskRepository repository) {
        return new YouTrackValidator();
    }

    public class YouTrackValidator extends Validator {

        private final String repositoryUrl;
        private final String permanentToken;

        YouTrackValidator() {
            this.repositoryUrl = getRepositoryUrl();
            this.permanentToken = getPassword();
        }

        @Override
        public void run(final IProgressMonitor monitor) throws CoreException {
            monitor.beginTask("Validating YouTrack connection...", 1);
            try {
                monitor.setTaskName("Authenticating with YouTrack");
                final YouTrackApi api = new YouTrackApiImpl(repositoryUrl, permanentToken);
                api.getCurrentUser();
                monitor.worked(1);
            } catch (final IOException e) {
                throw new CoreException(new Status(IStatus.ERROR, PluginConstants.PLUGIN_ID, "Failed to validate YouTrack connection: " + e.getMessage(), e));
            } finally {
                monitor.done();
            }
        }
    }
}
