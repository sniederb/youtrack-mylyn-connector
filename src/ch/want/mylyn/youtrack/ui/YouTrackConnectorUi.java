package ch.want.mylyn.youtrack.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

import ch.want.mylyn.youtrack.PluginConstants;

public class YouTrackConnectorUi extends AbstractRepositoryConnectorUi {

    public YouTrackConnectorUi() {
    }

    public YouTrackConnectorUi(final AbstractRepositoryConnector connector) {
        super(connector);
    }

    @Override
    public String getConnectorKind() {
        return PluginConstants.CONNECTOR_KIND;
    }

    @Override
    public IWizard getNewTaskWizard(final TaskRepository taskRepository, final ITaskMapping selection) {
        // not supported for now
        return null;
    }

    @Override
    public IWizard getQueryWizard(final TaskRepository taskRepository, final IRepositoryQuery query) {
        final RepositoryQueryWizard wizard = new RepositoryQueryWizard(taskRepository);
        wizard.addPage(new YouTrackQueryPage(taskRepository, query));
        return wizard;
    }

    @Override
    public ITaskRepositoryPage getSettingsPage(final TaskRepository taskRepository) {
        return new YouTrackRepositorySettingsPage(taskRepository);
    }

    @Override
    public boolean hasSearchPage() {
        return false;
    }
}
